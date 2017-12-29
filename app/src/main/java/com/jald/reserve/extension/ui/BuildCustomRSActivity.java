package com.jald.reserve.extension.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.extension.ui.fragment.BuildCustomRSFragment;
import com.jald.reserve.ui.KBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zbw on 2016/11/16.
 * 说明：建立客户关系列表
 */

public class BuildCustomRSActivity extends KBaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.container)
    FrameLayout container;

    BuildCustomRSFragment customListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_custom_rs);

        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        this.customListFragment = new BuildCustomRSFragment();
        jumpToCustomListPage();
    }

    private void jumpToCustomListPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, customListFragment, "customListFragment");
        transaction.commitAllowingStateLoss();

    }
}
