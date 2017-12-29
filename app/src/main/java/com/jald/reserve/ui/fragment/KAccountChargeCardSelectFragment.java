package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KAccountChargeCardSelectAdapter;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean;
import com.jald.reserve.ui.KAccountChargeActivity;
import com.jald.reserve.util.ViewUtil;

import java.util.List;


public class KAccountChargeCardSelectFragment extends Fragment {

    public static final String INTENT_KEY_BINED_CARD_LIST = "KeyBindedCardList";

    private View mRoot;
    private ListView mCardListView;
    private KAccountChargeCardSelectAdapter adapter;
    private List<KBankCardListResponseBean.KBankCardItemBean> bindedCardList;
    private KAccountChargeActivity parentActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = (KAccountChargeActivity) activity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_card_select, container, false);
        this.mCardListView = (ListView) mRoot.findViewById(R.id.card_list);
        this.adapter = new KAccountChargeCardSelectAdapter(parentActivity);
        this.bindedCardList = (List<KBankCardListResponseBean.KBankCardItemBean>) getArguments().getSerializable(INTENT_KEY_BINED_CARD_LIST);
        this.adapter.setCardList(this.bindedCardList);
        this.mCardListView.setAdapter(adapter);

        this.mCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parentActivity.onNewCardItemSelected(bindedCardList.get(position));
            }
        });
        return mRoot;
    }
}