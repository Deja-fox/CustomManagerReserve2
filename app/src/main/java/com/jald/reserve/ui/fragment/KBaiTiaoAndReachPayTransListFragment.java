package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KBaiTiaoAndReachPayTransListAdapter;
import com.jald.reserve.bean.http.response.KBaiTiaoAndReachPayTransListResponseBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KBaiTiaoAndReachPayActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KBaiTiaoAndReachPayTransListFragment extends Fragment {

    public static final String TAG = KBaiTiaoAndReachPayTransListFragment.class.getSimpleName();

    public static final String ARG_KEY_TRANS_TYPE = "keyTransType";

    private KBaiTiaoAndReachPayActivity mParent;

    private View mRoot;
    @Bind(R.id.transListView)
    RefreshableListViewWrapper transListViewWraper;
    ListView transListView;
    KBaiTiaoAndReachPayTransListAdapter adapter;
    List<KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem> transList;
    private int PAGE_SIZE = 15;
    private int curPageNum = -1;
    private int totalCount = -1;

    // 1-白条支付,2-货到付款
    String transTypeArg = "-1";

    private boolean isDataLoaded = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParent = (KBaiTiaoAndReachPayActivity) activity;
        if (getArguments() != null) {
            this.transTypeArg = getArguments().getString(ARG_KEY_TRANS_TYPE);
        } else {
            Log.e(TAG, "错误:没有传递要查询的交易类型,应该做错误处理");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_baitiao_and_reachpay_translist, container, false);
        ButterKnife.bind(this, this.mRoot);
        initUI();
        return this.mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!isDataLoaded) {
            if (transTypeArg.equals("-1")) {
                Toast.makeText(getActivity(), "参数错误,必须修改代码", Toast.LENGTH_SHORT).show();
                return;
            }
            firstLoadTransListByType(transTypeArg);
        }
    }

    private void initUI() {
        this.transListViewWraper.setHideHeader();
        this.transListView = transListViewWraper.getListView();
        this.transListView.setDivider(new ColorDrawable(Color.parseColor("#00000000")));
        this.adapter = new KBaiTiaoAndReachPayTransListAdapter(getActivity());
        this.transList = new ArrayList<KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem>();
    }

    private void firstLoadTransListByType(final String transType) {
        DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("trans_type", transType);
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.GRANT_TRANS_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    isDataLoaded = true;
                    curPageNum = 1;
                    final KBaiTiaoAndReachPayTransListResponseBean rspBean = JSON.parseObject(result.getContent(), KBaiTiaoAndReachPayTransListResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (totalCount <= PAGE_SIZE) {
                        transListViewWraper.notifyCloseLoadMore(true);
                    }
                    transList = rspBean.getList();
                    if (transList != null) {
                        adapter.setTransList(transList);
                        transListView.setAdapter(adapter);
                        transListViewWraper.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
                            @Override
                            public void onRefresh() {
                            }

                            @Override
                            public void onMore() {
                                loadMoreTransListByType(transType);
                            }
                        });
                        transListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (position != 0) {
                                    grantPay(transList.get(position - 1));
                                }
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

    private void grantPay(final KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem item) {
        String tips = "您确定要支付吗?";
        if (transTypeArg.equals("1")) {
            tips = "您确定要支付吗? 支付成功后请于今日22:00前到融资还款模块进行还款，逾期将产生相应的利息。";
        } else if (transTypeArg.equals("2")) {
            tips = "您确定要支付吗? 支付成功后请于今日22:00前到融资还款模块进行还款，逾期将产生相应的利息。";
        }
        DialogProvider.alert(getActivity(), "温馨提示", tips, "确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProvider.hideAlertDialog();
                DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        KHttpClient.singleInstance().cancel(getActivity());
                    }
                });
                JSONObject postJson = new JSONObject();
                postJson.put("trans_id", item.getTrans_id());
                KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.GRANT_PAY, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            DialogProvider.hideProgressBar();
                            Toast.makeText(getActivity(), "恭喜您支付成功", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {
                            DialogProvider.hideProgressBar();
                        }
                    }
                });
            }
        }, "取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProvider.hideAlertDialog();
            }
        });
    }

    private void loadMoreTransListByType(String transType) {
        if (curPageNum == -1) {
            return;
        }
        if (this.transList.size() >= totalCount) {
            this.transListViewWraper.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++curPageNum);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("trans_type", transType);
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.GRANT_TRANS_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    transListViewWraper.notifyDidMore();
                    KBaiTiaoAndReachPayTransListResponseBean rspBean = JSON.parseObject(result.getContent(), KBaiTiaoAndReachPayTransListResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (rspBean.getList() != null) {
                        transList.addAll(rspBean.getList());
                        adapter.notifyDataSetChanged();
                    }
                    if (transList.size() >= totalCount) {
                        transListViewWraper.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                transListViewWraper.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                transListViewWraper.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex,isOnCallback);
            }
        });
    }


}
