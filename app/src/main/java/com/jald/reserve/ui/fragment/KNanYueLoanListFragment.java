package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.BuildConfig;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KNanYueLoanListAdapter;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KNanYueLoanListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KNanYueLoanPayActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper;


import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class KNanYueLoanListFragment extends Fragment {

    private KNanYueLoanPayActivity mParent;

    private View mRoot;

    @Bind(R.id.loan_list)
    RefreshableListViewWrapper loanListWrapper;

    private ListView loanListView;

    private int PAGE_SIZE = 15;
    private int curPageNum = -1;
    private int totalCount = -1;

    private ArrayList<KNanYueLoanListResponseBean.KNanYueLoanItem> loanListData;

    private KNanYueLoanListAdapter adapter;

    private boolean hasDataLoaded = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParent = (KNanYueLoanPayActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_nanyue_loan_list, container, false);
        ButterKnife.bind(this, mRoot);

        this.loanListView = this.loanListWrapper.getListView();
        this.adapter = new KNanYueLoanListAdapter(mParent);
        this.loanListWrapper.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }

            @Override
            public void onMore() {
                loadMoreBillData();

            }
        });
        this.loanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KNanYueLoanListResponseBean.KNanYueLoanItem loanItem = loanListData.get(position - 1);
                if (loanItem.getAccount_no() == null || loanItem.getAccount_no().trim().equals("")) {
                    Toast.makeText(getActivity(), "贷款编号为空,不能还款", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (loanItem.getAmount_balance() != null) {
                    double unPayAmt = Double.parseDouble(loanItem.getAmount_balance());
                    if (unPayAmt == 0) {
                        Toast.makeText(getActivity(), "该笔贷款已经还清", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getActivity(), "该笔贷款的还款金额为空,不能还款", Toast.LENGTH_SHORT).show();
                    return;
                }
                KNanYueLoanPayFragment payFragment = new KNanYueLoanPayFragment();
                Bundle args = new Bundle();
                args.putSerializable(KNanYueLoanPayFragment.ARGS_KEY_LOAN_PAY_ITEM, loanItem);
                payFragment.setArguments(args);
                mParent.changeFragment(payFragment, true);

            }
        });
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!hasDataLoaded) {
            if (BuildConfig.DEBUG) {
                loadLoanList();
            } else {
                loadLoanList();
            }
        }
    }


    private void loadLoanList() {
        DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        // 未还清
        postJson.put("status", "02");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.NANYUE_LOAN_LIST, postJson,
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        hasDataLoaded = true;
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            curPageNum = 1;
                            KNanYueLoanListResponseBean rspBean = JSON.parseObject(result.getContent(), KNanYueLoanListResponseBean.class);
                            totalCount = rspBean.getTotal();
                            if (totalCount <= PAGE_SIZE) {
                                loanListWrapper.notifyCloseLoadMore(true);
                            }
                            loanListData = rspBean.getList();
                            adapter.setLoanListData(loanListData);
                            loanListView.setAdapter(adapter);
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
        // 未还清
        postJson.put("status", "02");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.NANYUE_LOAN_LIST, postJson,
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            curPageNum = 1;
                            KNanYueLoanListResponseBean rspBean = JSON.parseObject(result.getContent(), KNanYueLoanListResponseBean.class);
                            totalCount = rspBean.getTotal();
                            if (totalCount <= PAGE_SIZE) {
                                loanListWrapper.notifyCloseLoadMore(true);
                            }
                            loanListData = rspBean.getList();
                            adapter.setLoanListData(loanListData);
                            loanListWrapper.reset();
                            loanListView.setAdapter(adapter);
                            loanListWrapper.notifyRefreshComplete();
                            if (totalCount == 0) {
                                Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                        loanListWrapper.notifyRefreshComplete();
                        super.handleBusinessLayerFailure(responseBean, statusCode);
                    }

                    @Override
                    protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                        loanListWrapper.notifyRefreshComplete();
                        super.handleHttpLayerFailure(ex, isOnCallback);
                    }
                });
    }


    private void loadMoreBillData() {
        if (curPageNum == -1) {
            return;
        }
        if (this.loanListData.size() >= totalCount) {
            this.loanListWrapper.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++curPageNum);
        postJson.put("page_size", PAGE_SIZE);
        // 未还清
        postJson.put("status", "02");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.NANYUE_LOAN_LIST, postJson,
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            KNanYueLoanListResponseBean rspBean = JSON.parseObject(result.getContent(), KNanYueLoanListResponseBean.class);
                            totalCount = rspBean.getTotal();
                            loanListData.addAll(rspBean.getList());
                            adapter.notifyDataSetChanged();
                            loanListWrapper.notifyDidMore();
                            if (loanListData.size() >= totalCount) {
                                loanListWrapper.notifyCloseLoadMore(true);
                                return;
                            }
                        }
                    }

                    @Override
                    protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                        loanListWrapper.notifyCloseLoadMore(true);
                        super.handleBusinessLayerFailure(responseBean, statusCode);
                    }

                    @Override
                    protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                        loanListWrapper.notifyCloseLoadMore(true);
                        super.handleHttpLayerFailure(ex, isOnCallback);
                    }
                });
    }

    private void loadLoanListDebug() {
        curPageNum = 1;
        totalCount = 20;
        if (totalCount <= PAGE_SIZE) {
            loanListWrapper.notifyCloseLoadMore(true);
        }
        loanListData = new ArrayList<KNanYueLoanListResponseBean.KNanYueLoanItem>();
        for (int i = 0; i < 9; ++i) {
            loanListData.add(new KNanYueLoanListResponseBean.KNanYueLoanItem());
        }
        adapter.setLoanListData(loanListData);
        loanListView.setAdapter(adapter);

        if (totalCount == 0) {
            Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}