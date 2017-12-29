package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KBindedCardListAdapter;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KMyBankCardActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.SlideDeleteListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KBindedCardFragment extends Fragment {

    private View mRoot;
    private SlideDeleteListView mCardListView;
    private KBindedCardListAdapter adapter;
    private List<KBankCardListResponseBean.KBankCardItemBean> bindedCardList = new ArrayList<KBankCardListResponseBean.KBankCardItemBean>();
    private KMyBankCardActivity parent;

    private boolean shouldRefresh = false;
    private boolean isLoadCardListSuccess = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parent = (KMyBankCardActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_binded_bank_card, container, false);
        this.mCardListView = (SlideDeleteListView) mRoot.findViewById(R.id.card_list);
        this.adapter = new KBindedCardListAdapter(parent);
        this.bindedCardList = new ArrayList<KBankCardListResponseBean.KBankCardItemBean>();
        this.adapter.setCardList(this.bindedCardList);
        this.mCardListView.setAdapter(adapter);
        this.mCardListView.setRemoveListener(new SlideDeleteListView.RemoveListener() {
            @Override
            public void removeItem(final int position) {
                DialogProvider.alert(parent, "温馨提示", "确定要解除绑定该银行卡么?", "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogProvider.hideAlertDialog();
                        DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                KHttpClient.singleInstance().cancel(parent);
                            }
                        });
                        final int index = position - 1;
                        KBankCardListResponseBean.KBankCardItemBean selectedItem = bindedCardList.get(index);
                        JSONObject json = new JSONObject();
                        json.put("debit_accont_no", selectedItem.getDebit_accont_no());
                        json.put("debit_bind_type", "0");
                        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.BANK_CARD_BIND, json,
                                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                                    @Override
                                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                                        DialogProvider.hideProgressBar();
                                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                                            Toast.makeText(getActivity(), "解绑成功", Toast.LENGTH_SHORT).show();
                                            bindedCardList.remove(index);
                                            mCardListView.reset();
                                            adapter.notifyDataSetInvalidated();
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
        });
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parent.changeTitle("我的银行卡");
        parent.hideAddButton(false);
        if (!isLoadCardListSuccess) {
            this.loadBindedCardList();
        }
        if (this.shouldRefresh) {
            this.loadBindedCardList();
            this.shouldRefresh = false;
        }
    }

    private void loadBindedCardList() {
        DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(parent);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.BANKS_QUERY, json.toJSONString(),
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            KBankCardListResponseBean responseBean = JSON.parseObject(result.getContent(), KBankCardListResponseBean.class);
                            bindedCardList.clear();
                            bindedCardList.addAll(responseBean.getList());
                            adapter.notifyDataSetChanged();
                            if (bindedCardList.size() == 0) {
                                DialogProvider.alert(parent, "温馨提示", "您还没有绑定任何银行卡,请点击右上角加号按钮进行绑定", "确定");
                            }
                            isLoadCardListSuccess = true;
                        } else {
                            isLoadCardListSuccess = false;
                        }
                    }
                });
    }

    public void setRefreshImmediate(boolean refreshImmediate) {
        this.isLoadCardListSuccess = true;
        this.shouldRefresh = refreshImmediate;
    }

}