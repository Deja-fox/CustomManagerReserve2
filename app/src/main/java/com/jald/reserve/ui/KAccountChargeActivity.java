package com.jald.reserve.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KAccountChargeCardSelectAdapter;
import com.jald.reserve.bean.http.request.KAccountChargeRequestBean;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean.KBankCardItemBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.fragment.KAccountChargeCardSelectFragment;
import com.jald.reserve.ui.fragment.KAccountChargeFragment;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.StringUtil;
import com.jald.reserve.util.ViewUtil;

import java.io.Serializable;
import java.util.List;

public class KAccountChargeActivity extends KBaseActivity {

    public static final String TAG = KAccountChargeActivity.class.getSimpleName();

    public static final String INTENT_KEY_BINED_CARD_LIST = "KeyBindedCardList";

    private List<KBankCardItemBean> bindedCardList;

    private KAccountChargeFragment accountChargeFragment;
    private KAccountChargeCardSelectFragment cardSelectFragment;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_account_charge);

        this.bindedCardList = (List<KBankCardItemBean>) getIntent().getSerializableExtra(INTENT_KEY_BINED_CARD_LIST);
        if (this.bindedCardList == null || this.bindedCardList.size() == 0) {
            Log.e(TAG, "传递过来的银行卡列表为空");
            finish();
        }

        this.accountChargeFragment = new KAccountChargeFragment();
        this.cardSelectFragment = new KAccountChargeCardSelectFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable(KAccountChargeFragment.INTENT_KEY_BINED_CARD_LIST, (Serializable) bindedCardList);
        this.accountChargeFragment.setArguments(args);
        transaction.replace(R.id.container, this.accountChargeFragment, "AccountChargeFragment");
        transaction.commitAllowingStateLoss();
    }

    public void changeToCardSelectFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        Bundle args = new Bundle();
        args.putSerializable(KAccountChargeCardSelectFragment.INTENT_KEY_BINED_CARD_LIST, (Serializable) bindedCardList);
        this.cardSelectFragment.setArguments(args);
        transaction.replace(R.id.container, this.cardSelectFragment, "CardSelectFragment");
        transaction.addToBackStack("SecondPage");
        transaction.commitAllowingStateLoss();
    }

    public void onNewCardItemSelected(KBankCardItemBean newSelectedCardItem) {
        getSupportFragmentManager().popBackStackImmediate();
        KAccountChargeFragment firstFragment = (KAccountChargeFragment) getSupportFragmentManager().findFragmentByTag("AccountChargeFragment");
        firstFragment.setSelectedCarItemToUI(newSelectedCardItem);
    }
}




