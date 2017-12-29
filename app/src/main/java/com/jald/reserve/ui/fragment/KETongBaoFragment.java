package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoCustInfoResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KBaiTiaoAndReachPayActivity;
import com.jald.reserve.ui.KBillCenterActivity;
import com.jald.reserve.ui.KFinancingActivity;
import com.jald.reserve.ui.KGoodsWarningListActivity;
import com.jald.reserve.ui.KHistoryOrderActivity;
import com.jald.reserve.ui.KLoginPageActivity;
import com.jald.reserve.ui.KMainActivity;
import com.jald.reserve.ui.KNanYueEAccountMainActivity;
import com.jald.reserve.ui.KNanYueLoanPayActivity;
import com.jald.reserve.ui.KReserveGoodsActivity;
import com.jald.reserve.ui.KPhonefeeChargeActivity;
import com.jald.reserve.ui.KProxyAccountActivity;
import com.jald.reserve.ui.KTrafficFineActivity;
import com.jald.reserve.ui.KWaitToCommetOrderActivity;
import com.jald.reserve.ui.KWaitToPayOrderActivity;
import com.jald.reserve.ui.KWaitToReceiveOrderActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.OrderRemindDialog;
import com.jald.reserve.widget.RiseNumberTextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Random;

public class KETongBaoFragment extends Fragment {

    private KMainActivity parent;
    private View mRoot;

    @ViewInject(R.id.today_sale_amt)
    private RiseNumberTextView txtTodaySaleAmt;

    @ViewInject(R.id.effective_amt)
    private TextView txtAuthorizeAmt;

    @ViewInject(R.id.cust_level)
    private TextView txtCustLevel;

    @ViewInject(R.id.out_of_stock_bubble)
    private TextView txtOutOfStockBubble;

    @ViewInject(R.id.wait_to_pay_order)
    private LinearLayout btnWaitPayOrder;


    @ViewInject(R.id.bubble_count_wait_to_pay_order)
    private TextView txtBubleWaitToPay;

    @ViewInject(R.id.bubble_count_wait_to_receive)
    private TextView txtBubleWaitToReveive;

    @ViewInject(R.id.bubble_count_wait_to_commit)
    private TextView txtBubbleWaitToCommet;

    @ViewInject(R.id.bubble_can_order)
    private TextView txtBubleCanOrder;

    @ViewInject(R.id.history_all_order)
    private TextView btnHistoryOrderSearch;

    @ViewInject(R.id.trans_bill_detail)
    private LinearLayout btnTransBillDetail;

    @ViewInject(R.id.orderRemind)
    private LinearLayout btnOrderRemind;

    @ViewInject(R.id.nanYueEAccountManager)
    private LinearLayout btnNanYueEAccountManager;

    @ViewInject(R.id.rongziPay)
    private LinearLayout btnRongziPay;

    @ViewInject(R.id.waterPay)
    private LinearLayout btnWaterPay;

    @ViewInject(R.id.btn_out_of_stock)
    private View btnOutOfStockWarning;

    @ViewInject(R.id.btnWaitToReceiveOrder)
    private View btnWaitToReciveOrder;

    @ViewInject(R.id.waitToCommetOrder)
    private View btnWaitToCommetOrder;

    @ViewInject(R.id.baitiaoPay)
    private View btnBaiTaoPay;

    @ViewInject(R.id.reachPay)
    private View btnReachPayPay;

    private KTobaccoCustInfoResponseBean custInfo;
    private boolean isCustInfoLoaded = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parent = (KMainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return mRoot;
        }
        mRoot = inflater.inflate(R.layout.k_fragment_etongbao, container, false);
        x.view().inject(this, mRoot);

        this.txtOutOfStockBubble.setVisibility(View.GONE);
        this.txtBubleWaitToPay.setVisibility(View.GONE);
        this.txtBubleWaitToReveive.setVisibility(View.GONE);
        this.txtBubleCanOrder.setVisibility(View.GONE);
        this.txtBubbleWaitToCommet.setVisibility(View.GONE);
        this.bindEvent();
        return this.mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.parent.showRefresh(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    loadTobaccoCustInfo();
                }
            }
        });
        if (!isCustInfoLoaded) {
            loadTobaccoCustInfo();
        }
    }

    private void bindEvent() {
        this.btnTransBillDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KBillCenterActivity.class);
                    startActivity(intent);
                }
            }
        });
        this.btnHistoryOrderSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KHistoryOrderActivity.class);
                    startActivity(intent);
                }
            }
        });
        this.btnOutOfStockWarning.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KGoodsWarningListActivity.class);
                    startActivity(intent);
                }
            }
        });
        // 订货提醒
        this.btnOrderRemind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "数据还在加载中,请稍等", Toast.LENGTH_SHORT).show();
            }
        });
        this.btnOrderRemind.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getActivity(), KReserveGoodsActivity.class);
                startActivity(intent);
                return false;
            }
        });


        this.btnNanYueEAccountManager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KNanYueEAccountMainActivity.class);
                    startActivity(intent);
                }
            }
        });
        // 待支付订单
        this.btnWaitPayOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KWaitToPayOrderActivity.class);
                    startActivity(intent);
                }
            }
        });
        // 待收货订单
        this.btnWaitToReciveOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KWaitToReceiveOrderActivity.class);
                    startActivity(intent);
                }
            }
        });
        // 待评价订单
        this.btnWaitToCommetOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent intent = new Intent(getActivity(), KWaitToCommetOrderActivity.class);
                    startActivity(intent);
                }
            }
        });
        // 融资还款
        this.btnRongziPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KNanYueLoanPayActivity.class);
                startActivity(intent);
            }
        });
        this.btnWaterPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "该功能还未实现,敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        // 白条支付
        this.btnBaiTaoPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KBaiTiaoAndReachPayActivity.class);
                // 1是白条支付
                intent.putExtra(KBaiTiaoAndReachPayActivity.INTENT_KEY_TRANS_TYPE, "1");
                startActivity(intent);
            }
        });
        // 货到支付
        this.btnReachPayPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KBaiTiaoAndReachPayActivity.class);
                // 2是货到支付
                intent.putExtra(KBaiTiaoAndReachPayActivity.INTENT_KEY_TRANS_TYPE, "2");
                startActivity(intent);
            }
        });

    }

    private void loadTobaccoCustInfo() {
        isCustInfoLoaded = false;
        txtTodaySaleAmt.setText("...");
        txtAuthorizeAmt.setText("...");
        txtCustLevel.setText("...");
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TOBACCO_CUST_INFO, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            isCustInfoLoaded = true;
                            custInfo = JSON.parseObject(result.getContent(), KTobaccoCustInfoResponseBean.class);
                            txtTodaySaleAmt.withNumber(Float.parseFloat(custInfo.getSale_amt()));
                            txtTodaySaleAmt.setDuration(1200);
                            txtTodaySaleAmt.start();
                            txtAuthorizeAmt.setText(custInfo.getAuthorize_amt());
                            String custLevel = custInfo.getCust_level();
                            if (custLevel == null || custLevel.equals("")) {
                                txtCustLevel.setText("--");
                            } else {
                                txtCustLevel.setText(custLevel + "级");
                            }

                            String outStockAccount = custInfo.getOut_of_stock_count();
                            if (outStockAccount != null && !outStockAccount.trim().equals("0")) {
                                txtOutOfStockBubble.setVisibility(View.VISIBLE);
                                txtOutOfStockBubble.setText(outStockAccount.trim());
                            } else {
                                txtOutOfStockBubble.setVisibility(View.GONE);
                            }

                            String waitPayCount = custInfo.getTo_be_paid_count();
                            if (waitPayCount != null && !waitPayCount.equals("") && !waitPayCount.equals("0")) {
                                txtBubleWaitToPay.setVisibility(View.VISIBLE);
                                txtBubleWaitToPay.setText(waitPayCount);
                            }

                            String waitToReceiveCount = custInfo.getTo_be_harvested_count();
                            if (waitToReceiveCount != null && !waitToReceiveCount.equals("") && !waitToReceiveCount.equals("0")) {
                                txtBubleWaitToReveive.setVisibility(View.VISIBLE);
                                txtBubleWaitToReveive.setText(waitToReceiveCount);
                            }

                            // 待评价数量,目前写死,正式版先隐藏
                            txtBubbleWaitToCommet.setVisibility(View.GONE);

                            // 处理订货提醒
                            if (!custInfo.getCanOrder()) {
                                txtBubleCanOrder.setVisibility(View.GONE);
                            } else {
                                txtBubleCanOrder.setVisibility(View.VISIBLE);
                            }
                            btnOrderRemind.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    OrderRemindDialog dialog = DialogProvider.showOrderRemindDialog(getActivity(), new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogProvider.hideOrderRemindDialog();
                                        }
                                    });
                                    dialog.setBeginOrderTime(custInfo.getOrder_begin_date_time());
                                    dialog.setEndOrderTime(custInfo.getOrder_end_date_time());
                                    if (!custInfo.getCanOrder()) {
                                        dialog.setGotoOrderButtonVisibility(View.GONE);
                                        dialog.setCancelButtonText("确定");
                                    } else {
                                        dialog.setGotoOrderButtonVisibility(View.VISIBLE);
                                        dialog.setCancelButtonText("取消");
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                        super.handleBusinessLayerFailure(responseBean, statusCode);
                        txtTodaySaleAmt.setText("--");
                        txtAuthorizeAmt.setText("--");
                        txtCustLevel.setText("--");
                    }

                    @Override
                    protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                        super.handleHttpLayerFailure(ex, isOnCallback);
                        txtTodaySaleAmt.setText("--");
                        txtAuthorizeAmt.setText("--");
                        txtCustLevel.setText("--");
                    }
                });
    }

    @Event(R.id.proxyAccount)
    private void onProxyAccountClick(View v) {
        Intent intent = new Intent(getActivity(), KProxyAccountActivity.class);
        startActivity(intent);
    }

    @Event(R.id.financing)
    private void onFinancingClick(View v) {
        Intent intent = new Intent(getActivity(), KFinancingActivity.class);
        startActivity(intent);
    }


    @Event(R.id.phoneCharge)
    private void onPhonefeeChargeClick(View v) {
        Intent intent = new Intent(getActivity(), KPhonefeeChargeActivity.class);
        startActivity(intent);
    }

    @Event(R.id.trafficFine)
    private void onTrafficFineClick(View v) {
        Intent intent = new Intent(getActivity(), KTrafficFineActivity.class);
        startActivity(intent);
    }


    private boolean checkLogin() {
        if (KBaseApplication.getInstance().getUserInfoStub() == null) {
            Intent intent = new Intent(getActivity(), KLoginPageActivity.class);
            Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
            getActivity().startActivity(intent);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        parent.hideRefresh();
        KHttpClient.singleInstance().cancel(getActivity());
    }

}
