package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KAddBindCardFragment;
import com.jald.reserve.ui.fragment.KBindedCardFragment;

public class KMyBankCardActivity extends KBaseActivity {

    private TextView mTitle;
    private View mbtnAddCard;

    private Fragment bindedCardListFragment;
    private Fragment addCardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_my_bank_card);
        this.mTitle = (TextView) findViewById(R.id.title);
        this.mbtnAddCard = findViewById(R.id.btnAddCard);
        this.bindedCardListFragment = new KBindedCardFragment();
        this.addCardFragment = new KAddBindCardFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, this.bindedCardListFragment, "BindedCardListFragment");
        transaction.commitAllowingStateLoss();

        this.mbtnAddCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.container, addCardFragment, "AddCardFragment");
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });
    }

    public void changeTitle(String tilte) {
        this.mTitle.setText(tilte);
    }

    public void hideAddButton(boolean isHide) {
        if (isHide) {
            this.mbtnAddCard.setVisibility(View.GONE);
        } else {
            this.mbtnAddCard.setVisibility(View.VISIBLE);
        }
    }

    public void onAddNewCardSuccess() {
        KBindedCardFragment bindedCardListFragment = (KBindedCardFragment) getSupportFragmentManager().findFragmentByTag("BindedCardListFragment");
        bindedCardListFragment.setRefreshImmediate(true);
        getSupportFragmentManager().popBackStack();
    }

}

