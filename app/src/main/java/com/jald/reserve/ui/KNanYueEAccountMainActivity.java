package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KNanYueEAccountMainFragment;

import org.simple.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KNanYueEAccountMainActivity extends KBaseToolBarActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static final String ROOT_STACK_ENTRY_TAG = "RootStackEntry";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_nanyue_eaccount_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);
        if (savedInstanceState == null) {
            loadNanYueEaccountMainFragment();
        }
    }

    void loadNanYueEaccountMainFragment() {
        KNanYueEAccountMainFragment mainFragment = new KNanYueEAccountMainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, mainFragment);
        transaction.addToBackStack(ROOT_STACK_ENTRY_TAG);
        transaction.commitAllowingStateLoss();
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


    public void popupToRootFragment() {
        getSupportFragmentManager().popBackStackImmediate(ROOT_STACK_ENTRY_TAG, 0);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
