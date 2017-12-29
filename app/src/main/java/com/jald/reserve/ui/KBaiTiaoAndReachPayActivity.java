package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KBaiTiaoAndReachPayTransListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KBaiTiaoAndReachPayActivity extends KBaseActivity {

    public static final String INTENT_KEY_TRANS_TYPE = "KeyTransType";

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.container)
    FrameLayout container;

    private String argTransType = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_baitao_and_reach_pay);
        ButterKnife.bind(this);

        loadActivityParam();

        if (savedInstanceState == null) {
            KBaiTiaoAndReachPayTransListFragment fragment = new KBaiTiaoAndReachPayTransListFragment();
            Bundle args = new Bundle();
            args.putString(KBaiTiaoAndReachPayTransListFragment.ARG_KEY_TRANS_TYPE, argTransType);
            fragment.setArguments(args);
            changeFragment(fragment, false);
        }
    }

    private void loadActivityParam() {
        this.argTransType = getIntent().getStringExtra(INTENT_KEY_TRANS_TYPE);
        if (this.argTransType.equals("1")) {
            this.title.setText("账期支付");
        } else if (this.argTransType.equals("2")) {
            this.title.setText("货到付款");
        }
    }

    public void changeFragment(Fragment fragment, boolean isAddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        if (isAddToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }


}
