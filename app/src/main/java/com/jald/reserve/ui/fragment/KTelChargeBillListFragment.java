package com.jald.reserve.ui.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KTelChargeBillListAdapter;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KTelChargeBillResponseBean;
import com.jald.reserve.bean.http.response.KTelChargeBillResponseBean.TelChargeBillItem;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper.OnPullListener;

import java.util.ArrayList;
import java.util.List;

public class KTelChargeBillListFragment extends Fragment {

    private int PAGE_SIZE = 15;
    private View mRoot;
    private RefreshableListViewWrapper pullListView;
    private ListView listView;
    private KTelChargeBillListAdapter adapter;
    private List<TelChargeBillItem> billList = new ArrayList<TelChargeBillItem>();
    private int curPageNum = -1;
    private int totalCount = -1;

    public static interface VisiblityListener {
        public void onShow();

        public void onHide();
    }

    private VisiblityListener visiblityListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_tel_charge_bill_list, container, false);
        this.pullListView = (RefreshableListViewWrapper) mRoot.findViewById(R.id.bill_list);
        this.listView = pullListView.getListView();
        this.adapter = new KTelChargeBillListAdapter(getActivity());
        this.billList = new ArrayList<TelChargeBillItem>();

        this.pullListView.setOnPullDownListener(new OnPullListener() {

            @Override
            public void onRefresh() {
                doRefresh();
            }

            @Override
            public void onMore() {
                loadMoreBillData();
            }
        });
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (visiblityListener != null) {
            visiblityListener.onShow();
        }
        this.firstLoadBillData();
    }

    @Override
    public void onDestroyView() {
        if (visiblityListener != null) {
            visiblityListener.onHide();
        }
        super.onDestroyView();
    }

    private void firstLoadBillData() {
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TEL_CHARGE_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    curPageNum = 1;
                    KTelChargeBillResponseBean rspBean = JSON.parseObject(result.getContent(), KTelChargeBillResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (totalCount <= PAGE_SIZE) {
                        pullListView.notifyCloseLoadMore(true);
                    }
                    billList = rspBean.getList();
                    adapter.setBillListData(billList);
                    listView.setAdapter(adapter);
                    DialogProvider.hideProgressBar();
                    if (totalCount == 0) {
                        Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    DialogProvider.hideProgressBar();
                }
            }
        });
    }

    private void doRefresh() {
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TEL_CHARGE_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    curPageNum = 1;
                    KTelChargeBillResponseBean rspBean = JSON.parseObject(result.getContent(), KTelChargeBillResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (totalCount <= PAGE_SIZE) {
                        pullListView.notifyCloseLoadMore(true);
                    }
                    billList = rspBean.getList();
                    pullListView.reset();
                    adapter.setBillListData(billList);
                    listView.setAdapter(adapter);
                    pullListView.notifyRefreshComplete();
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                pullListView.notifyRefreshComplete();
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                pullListView.notifyRefreshComplete();
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }

    private void loadMoreBillData() {
        if (curPageNum == -1) {
            return;
        }
        if (this.billList.size() >= totalCount) {
            this.pullListView.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++curPageNum);
        postJson.put("page_size", PAGE_SIZE);
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TEL_CHARGE_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    KTelChargeBillResponseBean rspBean = JSON.parseObject(result.getContent(), KTelChargeBillResponseBean.class);
                    totalCount = rspBean.getTotal();
                    billList.addAll(rspBean.getList());
                    adapter.notifyDataSetChanged();
                    pullListView.notifyDidMore();
                    if (billList.size() >= totalCount) {
                        pullListView.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                pullListView.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                pullListView.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });

    }


    public VisiblityListener getVisiblityListener() {
        return visiblityListener;
    }

    public void setVisiblityListener(VisiblityListener visiblityListener) {
        this.visiblityListener = visiblityListener;
    }

}
