package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KNanYueLoanListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KNanYueLoanPayActivity extends KBaseToolBarActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_nanyue_loan_pay_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            loadNanYueLoanListFragment();
        }
    }

    void loadNanYueLoanListFragment() {
        KNanYueLoanListFragment loanListFragment = new KNanYueLoanListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, loanListFragment);
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

    public void popupFragmet() {
        super.onBackPressed();
    }


}
