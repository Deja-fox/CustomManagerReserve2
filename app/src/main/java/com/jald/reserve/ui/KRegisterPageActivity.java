package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.ui.fragment.KRegisterFirstStepFragment;
import com.jald.reserve.ui.fragment.KRegisterSecondStepFragment;
import com.jald.reserve.ui.fragment.KNanYueEAccountManagerFragment;
import com.jald.reserve.util.DialogProvider;

import org.simple.eventbus.EventBus;

public class KRegisterPageActivity extends KBaseActivity {

    private TextView mTitle;
    private KRegisterFirstStepFragment mFirstStepFragment;
    private KRegisterSecondStepFragment mSecondStepFragment;

    private Fragment mActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_register_layout);
        this.mTitle = (TextView) findViewById(R.id.title);
        this.mFirstStepFragment = new KRegisterFirstStepFragment();
        this.mSecondStepFragment = new KRegisterSecondStepFragment();

        EventBus.getDefault().register(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, mFirstStepFragment);
            this.mActiveFragment = mFirstStepFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    public void onNextStepClicked(KUserInfoStub registerContextBean) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        Bundle args = new Bundle();
        args.putSerializable(KRegisterSecondStepFragment.KEY_ARGU_USER_INFO_STUB, registerContextBean);
        mSecondStepFragment.setArguments(args);
        transaction.replace(R.id.container, mSecondStepFragment);
        transaction.commitAllowingStateLoss();
        this.mActiveFragment = mSecondStepFragment;
        this.mTitle.setText("信息完善");
    }

    public void onSecondStepSubmitSuccess1() {
        Toast.makeText(this, "恭喜您注册成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        this.mActiveFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if (mActiveFragment == mFirstStepFragment) {
            DialogProvider.alert(this, "温馨提示", "确定要放弃注册么", "确定", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    finish();
                }
            }, "取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                }
            });
        } else if (mActiveFragment == mSecondStepFragment) {
            DialogProvider.alert(this, "温馨提示", "您已注册成功,当前可用手机号登录,但是如不完善信息,部分功能将不可用,确定要放弃么?", "确定", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    finish();
                }
            }, "取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                }
            });
        } else if (mActiveFragment instanceof KNanYueEAccountManagerFragment) {
            super.onBackPressed();
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
