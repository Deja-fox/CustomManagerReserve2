package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KTobaccoOrderListAdapter;
import com.jald.reserve.adapter.KTobaccoOrderListAdapter.OnOrderDetailClickListener;
import com.jald.reserve.adapter.KYoumktOrderListAdapter;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequesContextBean;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KOrderPayChannelResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean.KTobaccoOrderItem;
import com.jald.reserve.bean.http.response.KYoumktOrderListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KHistoryOrderActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KHistoryOrderFragment extends Fragment {

    public static interface OnOrderPayClickCallback {
        public void onOrderPay(KTobaccoOrderListResponseBean orderDetailInfo, int position, KTobaccoOrderItem orderItem);
    }

    public static interface IHistoryOrderContainer {
        public void showSearch(OnClickListener clickLReceiver);

        public void hideSearch();
    }

    private KHistoryOrderActivity mParent;
    private View mRoot;

    @Bind(R.id.begin_date)
    EditText txtBeginDate;
    @Bind(R.id.end_date)
    EditText txtEndDate;
    @Bind(R.id.bill_list)
    ListView billList;
    @Bind(R.id.summary_container)
    View viewSummaryContainer;
    @Bind(R.id.total_order_count)
    TextView txtTotalOrderCount;
    @Bind(R.id.total_order_amt)
    TextView txtTotlaOrderAmt;
    @Bind(R.id.tabTobaccoOrder)
    TextView tabTobaccoOrder;
    @Bind(R.id.tabYoumktOrder)
    TextView tabYoumktOrder;
    @Bind(R.id.tabContentYoumktOrderContainer)
    LinearLayout tabContentYoumktOrderContainer;
    @Bind(R.id.tabContentTobaccoOrderContainer)
    LinearLayout tabContentTobaccoOrderContainer;

    private DatePickerDialog dialogBeginDate;
    private DatePickerDialog dialogEndDate;
    private KTobaccoOrderListAdapter billAdapter;
    private KTobaccoOrderListResponseBean orderInfoBean;
    private ArrayList<KTobaccoOrderItem> billListData;
    private OnOrderDetailClickListener onOrderDetailClickListener;
    private OnOrderPayClickCallback onOrderPayClickCallback;
    private JSONObject formData;

    // 优市网订单相关
    @Bind(R.id.youmktOrderList)
    RefreshableListViewWrapper youmktOrderListWrapper;
    ListView youmktOrderListView;
    private int PAGE_SIZE = 15;
    private int curPageNum = -1;
    private int totalCount = -1;
    private KYoumktOrderListAdapter youmktOrderAdapter;
    private ArrayList<KYoumktOrderListResponseBean.KYoumktOrderItem> youmktOrderListData;
    private boolean isYoumktOrderDataLoaded = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParent = (KHistoryOrderActivity) activity;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot == null) {
            this.mRoot = inflater.inflate(R.layout.k_fragment_history_order, container, false);
            ButterKnife.bind(this, mRoot);

            setDefaultDate();

            this.youmktOrderListWrapper.setHideHeader();
            this.youmktOrderListView = this.youmktOrderListWrapper.getListView();
            this.youmktOrderAdapter = new KYoumktOrderListAdapter(getActivity());

            changeTab(R.id.tabTobaccoOrder);
            return this.mRoot;
        } else {
            return ViewUtil.detachFromParent(mRoot);
        }
    }


    private void changeTab(int tabId) {
        tabTobaccoOrder.setSelected(false);
        tabYoumktOrder.setSelected(false);
        tabContentYoumktOrderContainer.setVisibility(View.GONE);
        tabContentTobaccoOrderContainer.setVisibility(View.GONE);
        switch (tabId) {
            case R.id.tabTobaccoOrder:
                tabTobaccoOrder.setSelected(true);
                tabContentTobaccoOrderContainer.setVisibility(View.VISIBLE);
                viewSummaryContainer.setVisibility(View.GONE);
                ((IHistoryOrderContainer) mParent).showSearch(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTobaccoOrderListInfo();
                    }
                });
                break;
            case R.id.tabYoumktOrder:
                tabYoumktOrder.setSelected(true);
                tabContentYoumktOrderContainer.setVisibility(View.VISIBLE);
                if (!isYoumktOrderDataLoaded) {
                    firstLoadYoumktOrderData();
                }
                ((IHistoryOrderContainer) mParent).hideSearch();
                break;
        }
    }

    // 加载优士网订单相关-
    private void firstLoadYoumktOrderData() {
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        // 查询所有状态的订单
        postJson.put("pay_status", "3");
        // 所有付款方式的订单
        postJson.put("payment", "0");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.YOUMKT_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    isYoumktOrderDataLoaded = true;
                    curPageNum = 1;
                    final KYoumktOrderListResponseBean rspBean = JSON.parseObject(result.getContent(), KYoumktOrderListResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (totalCount <= PAGE_SIZE) {
                        youmktOrderListWrapper.notifyCloseLoadMore(true);
                    }
                    youmktOrderListData = rspBean.getList();
                    if (youmktOrderListData != null) {
                        youmktOrderAdapter.setOrderList(youmktOrderListData);
                        youmktOrderListView.setAdapter(youmktOrderAdapter);
                        youmktOrderListWrapper.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
                            @Override
                            public void onRefresh() {
                            }

                            @Override
                            public void onMore() {
                                loadMoreYoumktOrderData();
                            }
                        });
                        youmktOrderAdapter.setOnOrderDetailClickListener(new KYoumktOrderListAdapter.OnOrderDetailClickListener() {
                            @Override
                            public void onOrderDetailClicked(KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
                                jumpToYoumktOrderDetailPage(orderItem);
                            }
                        });
                        youmktOrderAdapter.setOnOrderPayClickListener(new KYoumktOrderListAdapter.OnOrderPayClickListener() {
                            @Override
                            public void onOrderPay(int position, KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
                                jumpToYoumktOrderPayPage(rspBean, orderItem);
                            }
                        });
                    }
                    DialogProvider.hideProgressBar();
                } else {
                    DialogProvider.hideProgressBar();
                }
            }
        });
    }

    private void loadMoreYoumktOrderData() {
        if (curPageNum == -1) {
            return;
        }
        if (this.youmktOrderListData.size() >= totalCount) {
            this.youmktOrderListWrapper.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++curPageNum);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.YOUMKT_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    youmktOrderListWrapper.notifyDidMore();
                    KYoumktOrderListResponseBean rspBean = JSON.parseObject(result.getContent(), KYoumktOrderListResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (rspBean.getList() != null) {
                        youmktOrderListData.addAll(rspBean.getList());
                        youmktOrderAdapter.notifyDataSetChanged();
                    }
                    if (youmktOrderListData.size() >= totalCount) {
                        youmktOrderListWrapper.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                youmktOrderListWrapper.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                youmktOrderListWrapper.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    private void jumpToYoumktOrderDetailPage(KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
        KYoumktOrderDetailFragment orderDetailFragment = new KYoumktOrderDetailFragment();
        Bundle arg = new Bundle();
        arg.putSerializable(KYoumktOrderDetailFragment.ARG_KEY_ORDER_ITEM, orderItem);
        orderDetailFragment.setArguments(arg);
        mParent.changeFragment(orderDetailFragment, true);
    }

    private void jumpToYoumktOrderPayPage(final KYoumktOrderListResponseBean orderDetailInfo, final KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
        DialogProvider.showProgressBar(mParent, new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(mParent);
            }
        });
        JSONObject json = new JSONObject();
        json.put("com_id", orderItem.getCom_tp_id());
        KHttpClient.singleInstance().postData(mParent, KHttpAdress.ORDER_PAY_MODEL_LIST, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    KOrderPayChannelResponseBean channelInfo = JSON.parseObject(result.getContent(), KOrderPayChannelResponseBean.class);
                    KNayYueOrderPayFragment orderPayFragment = new KNayYueOrderPayFragment();
                    KNanYueOrderPayRequesContextBean orderPayContextInfo = new KNanYueOrderPayRequesContextBean();
                    orderPayContextInfo.setPayChannelInfo(channelInfo);
                    KNanYueOrderPayRequestBean orderPayRequestBean = new KNanYueOrderPayRequestBean();
                    orderPayRequestBean.setAmt(orderItem.getAmt());
                    orderPayRequestBean.setCo_num(orderItem.getCo_num());
                    orderPayRequestBean.setCom_name(orderItem.getCom_name());
                    orderPayRequestBean.setCom_id(orderItem.getCom_tp_id());
                    orderPayRequestBean.setDays(orderItem.getDays());
                    orderPayContextInfo.setOrderPayRequestBean(orderPayRequestBean);
                    Bundle args = new Bundle();
                    args.putSerializable(KNayYueOrderPayFragment.ARG_KEY_ORDER_PAY_INFO, orderPayContextInfo);
                    orderPayFragment.setArguments(args);
                    mParent.changeFragment(orderPayFragment, true);
                }
            }
        });
    }
    // --优市网订单相关结束--


    private void loadTobaccoOrderListInfo() {
        if (!inputCheck()) {
            return;
        }
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TOBACCO_HISTORY_ORDER_QUERY, formData,
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            orderInfoBean = JSON.parseObject(result.getContent(), KTobaccoOrderListResponseBean.class);
                            if (orderInfoBean == null) {
                                Toast.makeText(getActivity(), "暂无订单", Toast.LENGTH_SHORT).show();
                                viewSummaryContainer.setVisibility(View.GONE);
                                return;
                            } else if (orderInfoBean.getTotal() == 0) {
                                Toast.makeText(getActivity(), "暂无订单", Toast.LENGTH_SHORT).show();
                                viewSummaryContainer.setVisibility(View.GONE);
                                billListData = orderInfoBean.getList();
                                if (billListData == null) {
                                    billListData = new ArrayList<KTobaccoOrderItem>();
                                }
                                billAdapter = new KTobaccoOrderListAdapter(getActivity());
                                billAdapter.setBillList(billListData);
                                billList.setAdapter(billAdapter);
                                return;
                            } else {
                                billListData = orderInfoBean.getList();
                                billAdapter = new KTobaccoOrderListAdapter(getActivity());
                                billAdapter.setBillList(billListData);
                                billList.setAdapter(billAdapter);
                                totalSummary(billListData);
                                billAdapter.setOnOrderDetailClickListener(new OnOrderDetailClickListener() {
                                    @Override
                                    public void onOrderDetailClicked(KTobaccoOrderItem orderItem) {
                                        if (onOrderDetailClickListener != null) {
                                            onOrderDetailClickListener.onOrderDetailClicked(orderItem);
                                        }
                                    }
                                });
                                billAdapter.setOnOrderPayClickListener(new KTobaccoOrderListAdapter.OnOrderPayClickListener() {
                                    @Override
                                    public void onOrderPay(int position, KTobaccoOrderItem orderItem) {
                                        if (orderItem.getIs_pay().equals("0")) {
                                            Toast.makeText(getActivity(), "当前时间烟草公司不允许支付", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if (onOrderPayClickCallback != null) {
                                            onOrderPayClickCallback.onOrderPay(orderInfoBean, position, orderItem);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void totalSummary(ArrayList<KTobaccoOrderItem> billData) {
        viewSummaryContainer.setVisibility(View.VISIBLE);
        BigDecimal totalOrderCount = new BigDecimal(0);
        BigDecimal totalOrderAmt = new BigDecimal(0);
        for (int i = 0; i < billData.size(); ++i) {
            totalOrderCount = totalOrderCount.add(new BigDecimal(billData.get(i).getQty()));
            totalOrderAmt = totalOrderAmt.add(new BigDecimal(billData.get(i).getAmt()));
        }
        DecimalFormat df = new DecimalFormat("#.##");
        this.txtTotalOrderCount.setText("总订购量: " + df.format(totalOrderCount) + "条");
        this.txtTotlaOrderAmt.setText("总订购金额: " + df.format(totalOrderAmt) + "元");
    }

    private boolean inputCheck() {
        formData = new JSONObject();
        String strBenginDate = this.txtBeginDate.getText().toString().trim();
        String strEndDate = this.txtEndDate.getText().toString().trim();
        if (strBenginDate.equals("")) {
            Toast.makeText(getActivity(), "请选择开始日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strEndDate.equals("")) {
            Toast.makeText(getActivity(), "请选择结束日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strBenginDate.compareTo(strEndDate) > 0) {
            Toast.makeText(getActivity(), "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        formData.put("begin_date", strBenginDate);
        formData.put("end_date", strEndDate);
        return true;
    }

    @OnClick(R.id.begin_date)
    void onBenginDateClick(View view) {
        if (dialogBeginDate != null) {
            dialogBeginDate.dismiss();
        }
        Calendar calendar = new GregorianCalendar();
        String strBeginDate = this.txtBeginDate.getText().toString().trim();
        if (!strBeginDate.equals("")) {
            calendar.set(Calendar.YEAR, Integer.parseInt(strBeginDate.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(strBeginDate.substring(4, 6)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strBeginDate.substring(6, 8)));
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dialogBeginDate = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear < 10 ? "0" + monthOfYear : monthOfYear + "";
                String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                txtBeginDate.setText(year + "" + month + day);
            }
        }, year, month, day);
        dialogBeginDate.show();
    }

    @OnClick(R.id.end_date)
    void onEndDateClick(View view) {
        if (dialogEndDate != null) {
            dialogEndDate.dismiss();
        }
        Calendar calendar = new GregorianCalendar();
        String strEndDate = this.txtEndDate.getText().toString().trim();
        if (!strEndDate.equals("")) {
            calendar.set(Calendar.YEAR, Integer.parseInt(strEndDate.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(strEndDate.substring(4, 6)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strEndDate.substring(6, 8)));
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dialogEndDate = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear < 10 ? "0" + monthOfYear : monthOfYear + "";
                String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                txtEndDate.setText(year + "" + month + day);
            }
        }, year, month, day);
        dialogEndDate.show();
    }

    private void setDefaultDate() {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String month = monthOfYear < 10 ? "0" + monthOfYear : monthOfYear + "";
        String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
        txtEndDate.setText(year + "" + month + day);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 7);
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH) + 1;
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = monthOfYear < 10 ? "0" + monthOfYear : monthOfYear + "";
        day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
        txtBeginDate.setText(year + "" + month + day);
        loadTobaccoOrderListInfo();
    }


    @OnClick({R.id.tabTobaccoOrder, R.id.tabYoumktOrder})
    void onTabClick(View v) {
        changeTab(v.getId());
    }

    @Override
    public void onDestroyView() {
        Activity parent = getActivity();
        if (parent instanceof IHistoryOrderContainer) {
            ((IHistoryOrderContainer) parent).hideSearch();
        }
        super.onDestroyView();
    }

    public void setOnOrderPayClickCallback(OnOrderPayClickCallback onOrderPayClickCallback) {
        this.onOrderPayClickCallback = onOrderPayClickCallback;
    }

    public void setOnOrderDetailClickListener(OnOrderDetailClickListener onOrderDetailClickListener) {
        this.onOrderDetailClickListener = onOrderDetailClickListener;
    }

}
