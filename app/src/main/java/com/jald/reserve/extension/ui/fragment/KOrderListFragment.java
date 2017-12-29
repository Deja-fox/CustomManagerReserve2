package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.adapter.KOrderListAdapter;
import com.jald.reserve.extension.bean.response.OrderListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper;
import com.shizhefei.fragment.LazyFragment;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


// 目前需求是加载全部订单
public class KOrderListFragment extends LazyFragment {

    public static class RefreshEvent {

    }

    private View mRoot;

    @Bind(R.id.tabConfirmedOrder)
    TextView tabConfirmedOrder;
    @Bind(R.id.tabUnconfirmedOrder)
    TextView tabUnconfirmedOrder;
    @Bind(R.id.tabAllOrder)
    TextView tabAllOrder;
    @Bind(R.id.lvConfirmedOrderList)
    RefreshableListViewWrapper lvConfirmedOrderList;
    @Bind(R.id.lvUnconfirmedOrderList)
    RefreshableListViewWrapper lvUnconfirmedOrderList;
    @Bind(R.id.lvAllOrderList)
    RefreshableListViewWrapper lvAllOrderList;

    ListView confirmedOrderList;
    ListView unConfirmedOrderList;
    ListView allOrderList;

    private KOrderListAdapter allOrderListAdapter;
    private KOrderListAdapter confirmedOrderListAdapter;
    private KOrderListAdapter unConfirmedOrderListAdapter;
    private ArrayList<OrderListResponseBean.OrderBean> allOrderListData = new ArrayList<>();
    private ArrayList<OrderListResponseBean.OrderBean> confirmedOrderListData = new ArrayList<>();
    private ArrayList<OrderListResponseBean.OrderBean> unConfirmedOrderListData = new ArrayList<>();

    private int PAGE_SIZE = 8;
    // 全部订单相关变量
    private boolean isAllOrderDataLoaded;
    private int allOrderCurPageNum = -1;
    private int allOrderTotalCount = -1;

    // 未确认订单相关变量
    private boolean isUnConfirmedDataLoaded;
    private int unConfirmedOrderCurPageNum = -1;
    private int unConfirmedOrderTotalCount = -1;

    // 已确认订单相关变量
    private boolean isConfirmedDataLoaded;
    private int confirmedOrderCurPageNum = -1;
    private int confirmedOrderTotalCount = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        this.mRoot = inflater.inflate(R.layout.k_fragment_order_list, null, false);
        ButterKnife.bind(this, this.mRoot);
        initUI();
        tabAllOrder.performClick();
        setContentView(this.mRoot);
    }

    void initUI() {
        this.confirmedOrderList = lvConfirmedOrderList.getListView();
        this.unConfirmedOrderList = lvUnconfirmedOrderList.getListView();
        this.allOrderList = lvAllOrderList.getListView();
    }


    @Subscriber(mode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        this.isAllOrderDataLoaded = false;
    }


    //====================================================加载全部订单相关的数据======================
    private void loadAllOrderListFirstTime() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        postJson.put("order_status", "3");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    lvAllOrderList.reset();
                    lvAllOrderList.notifyRefreshComplete();
                    isAllOrderDataLoaded = true;
                    allOrderCurPageNum = 1;
                    //TODO 期货解析
                    final OrderListResponseBean rspBean = JSON.parseObject(result.getContent(), OrderListResponseBean.class);
                    allOrderTotalCount = rspBean.getTotal();
                    if (allOrderTotalCount <= PAGE_SIZE) {
                        lvAllOrderList.notifyCloseLoadMore(true);
                    }
                    allOrderListData = rspBean.getList();
                    if (allOrderListData == null) {
                        allOrderListData = new ArrayList<>();
                    }
                    allOrderListAdapter = new KOrderListAdapter(getActivity());
                    allOrderListAdapter.setOrderListData(allOrderListData);
                    allOrderList.setAdapter(allOrderListAdapter);
                    lvAllOrderList.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
                        @Override
                        public void onRefresh() {
                            loadAllOrderListFirstTime();
                        }

                        @Override
                        public void onMore() {
                            loadAllOrderListMore();
                        }
                    });
                    DialogProvider.hideProgressBar();
                } else {
                    lvAllOrderList.notifyRefreshComplete();
                    DialogProvider.hideProgressBar();
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                lvAllOrderList.notifyRefreshComplete();
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                lvAllOrderList.notifyRefreshComplete();
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    private void loadAllOrderListMore() {
        if (allOrderCurPageNum == -1) {
            return;
        }
        if (this.allOrderListData.size() >= allOrderTotalCount) {
            this.lvAllOrderList.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++allOrderCurPageNum);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        postJson.put("order_status", "3");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    OrderListResponseBean rspBean = JSON.parseObject(result.getContent(), OrderListResponseBean.class);
                    allOrderTotalCount = rspBean.getTotal();
                    allOrderListData.addAll(rspBean.getList());
                    allOrderListAdapter.notifyDataSetChanged();
                    lvAllOrderList.notifyDidMore();
                    if (allOrderListData.size() >= allOrderTotalCount) {
                        lvAllOrderList.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                lvAllOrderList.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                lvAllOrderList.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }


    //====================================================加载未确认订单相关的数据=====================
    private void loadUnConfirmedOrderListFirstTime() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        postJson.put("order_status", "1");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    lvUnconfirmedOrderList.reset();
                    lvUnconfirmedOrderList.notifyRefreshComplete();
                    isUnConfirmedDataLoaded = true;
                    unConfirmedOrderCurPageNum = 1;
                    final OrderListResponseBean rspBean = JSON.parseObject(result.getContent(), OrderListResponseBean.class);
                    unConfirmedOrderTotalCount = rspBean.getTotal();
                    if (unConfirmedOrderTotalCount <= PAGE_SIZE) {
                        lvUnconfirmedOrderList.notifyCloseLoadMore(true);
                    }
                    unConfirmedOrderListData = rspBean.getList();
                    if (unConfirmedOrderListData == null) {
                        unConfirmedOrderListData = new ArrayList<OrderListResponseBean.OrderBean>();
                    }
                    unConfirmedOrderListAdapter = new KOrderListAdapter(getActivity());
                    unConfirmedOrderListAdapter.setOrderListData(unConfirmedOrderListData);
                    unConfirmedOrderList.setAdapter(unConfirmedOrderListAdapter);
                    lvUnconfirmedOrderList.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
                        @Override
                        public void onRefresh() {
                            loadUnConfirmedOrderListFirstTime();
                        }

                        @Override
                        public void onMore() {
                            loadUnConfirmedOrderListMore();
                        }
                    });
                    DialogProvider.hideProgressBar();
                } else {
                    lvUnconfirmedOrderList.notifyRefreshComplete();
                    DialogProvider.hideProgressBar();
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                lvUnconfirmedOrderList.notifyRefreshComplete();
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                lvUnconfirmedOrderList.notifyRefreshComplete();
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    private void loadUnConfirmedOrderListMore() {
        if (unConfirmedOrderCurPageNum == -1) {
            return;
        }
        if (this.unConfirmedOrderListData.size() >= unConfirmedOrderTotalCount) {
            this.lvUnconfirmedOrderList.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++unConfirmedOrderCurPageNum);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        postJson.put("order_status", "1");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    OrderListResponseBean rspBean = JSON.parseObject(result.getContent(), OrderListResponseBean.class);
                    unConfirmedOrderTotalCount = rspBean.getTotal();
                    unConfirmedOrderListData.addAll(rspBean.getList());
                    unConfirmedOrderListAdapter.notifyDataSetChanged();
                    lvUnconfirmedOrderList.notifyDidMore();
                    if (unConfirmedOrderListData.size() >= unConfirmedOrderTotalCount) {
                        lvUnconfirmedOrderList.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                lvUnconfirmedOrderList.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                lvUnconfirmedOrderList.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    //====================================================加载已确认订单相关的数据=====================
    private void loadConfirmedOrderListFirstTime() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        postJson.put("order_status", "2");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    lvConfirmedOrderList.reset();
                    lvConfirmedOrderList.notifyRefreshComplete();
                    isConfirmedDataLoaded = true;
                    confirmedOrderCurPageNum = 1;
                    final OrderListResponseBean rspBean = JSON.parseObject(result.getContent(), OrderListResponseBean.class);
                    confirmedOrderTotalCount = rspBean.getTotal();
                    if (confirmedOrderTotalCount <= PAGE_SIZE) {
                        lvConfirmedOrderList.notifyCloseLoadMore(true);
                    }
                    confirmedOrderListData = rspBean.getList();
                    if (confirmedOrderListData == null) {
                        confirmedOrderListData = new ArrayList<OrderListResponseBean.OrderBean>();
                    }
                    confirmedOrderListAdapter = new KOrderListAdapter(getActivity());
                    confirmedOrderListAdapter.setOrderListData(confirmedOrderListData);
                    confirmedOrderList.setAdapter(confirmedOrderListAdapter);
                    lvConfirmedOrderList.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
                        @Override
                        public void onRefresh() {
                            loadConfirmedOrderListFirstTime();
                        }

                        @Override
                        public void onMore() {
                            loadConfirmedOrderListMore();
                        }
                    });
                    DialogProvider.hideProgressBar();
                } else {
                    lvConfirmedOrderList.notifyRefreshComplete();
                    DialogProvider.hideProgressBar();
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                lvConfirmedOrderList.notifyRefreshComplete();
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                lvConfirmedOrderList.notifyRefreshComplete();
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    private void loadConfirmedOrderListMore() {
        if (confirmedOrderCurPageNum == -1) {
            return;
        }
        if (this.confirmedOrderListData.size() >= confirmedOrderTotalCount) {
            this.lvConfirmedOrderList.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++confirmedOrderCurPageNum);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "3");
        postJson.put("payment", "0");
        postJson.put("order_status", "2");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    OrderListResponseBean rspBean = JSON.parseObject(result.getContent(), OrderListResponseBean.class);
                    confirmedOrderTotalCount = rspBean.getTotal();
                    confirmedOrderListData.addAll(rspBean.getList());
                    confirmedOrderListAdapter.notifyDataSetChanged();
                    lvConfirmedOrderList.notifyDidMore();
                    if (confirmedOrderListData.size() >= confirmedOrderTotalCount) {
                        lvConfirmedOrderList.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                lvConfirmedOrderList.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                lvConfirmedOrderList.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    @OnClick({R.id.tabConfirmedOrder, R.id.tabUnconfirmedOrder, R.id.tabAllOrder})
    void onTabItemClick(View v) {
        if (v.getId() == R.id.tabConfirmedOrder) {
            changeTab(1);
        } else if (v.getId() == R.id.tabUnconfirmedOrder) {
            changeTab(0);
        } else if (v.getId() == R.id.tabAllOrder) {
            changeTab(2);
        }
    }


    private void changeTab(int index) {
        tabConfirmedOrder.setSelected(false);
        tabAllOrder.setSelected(false);
        tabUnconfirmedOrder.setSelected(false);
        lvAllOrderList.setVisibility(View.GONE);
        lvConfirmedOrderList.setVisibility(View.GONE);
        lvUnconfirmedOrderList.setVisibility(View.GONE);
        switch (index) {
            case 0:
                tabUnconfirmedOrder.setSelected(true);
                lvUnconfirmedOrderList.setVisibility(View.VISIBLE);
                if (!isUnConfirmedDataLoaded) {
                    loadUnConfirmedOrderListFirstTime();
                }
                break;
            case 1:
                tabConfirmedOrder.setSelected(true);
                lvConfirmedOrderList.setVisibility(View.VISIBLE);
                if (!isConfirmedDataLoaded) {
                    loadConfirmedOrderListFirstTime();
                }
                break;
            case 2:
                tabAllOrder.setSelected(true);
                lvAllOrderList.setVisibility(View.VISIBLE);
                if (!isAllOrderDataLoaded) {
                    loadAllOrderListFirstTime();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
