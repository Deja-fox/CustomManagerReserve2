package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KBottomMenuAdapter;
import com.jald.reserve.bean.normal.KBottomTabItem;
import com.jald.reserve.ui.fragment.KETongBaoFragment;
import com.jald.reserve.ui.fragment.KOrderRankFragment;
import com.jald.reserve.ui.fragment.KUserCenterFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@ContentView(R.layout.k_activity_main)
public class KMainActivity extends KBaseActivity {

    @ViewInject(R.id.title)
    private TextView titleView;

    @ViewInject(R.id.left_title)
    private TextView leftTitle;

    @ViewInject(R.id.btn_refresh)
    private ImageButton btnRefresh;

    @ViewInject(R.id.self_tabhost)
    private GridView tabMenuGrid;

    private List<KBottomTabItem> tabMenuItems = new ArrayList<KBottomTabItem>();
    private Map<String, Fragment> tabFragmentMap = new HashMap<String, Fragment>();
    private KBottomMenuAdapter tabMenuAdapter;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        hideLeftTitle();
        hideRefresh();
        initTabHost();
        tabMenuGrid.performItemClick(null, 0, 0);
    }

    private void initTabHost() {
        tabMenuItems = new ArrayList<KBottomTabItem>();

        tabMenuItems.add(new KBottomTabItem(R.drawable.tab_btn_etongbao, "e通宝"));
        tabMenuItems.add(new KBottomTabItem(R.drawable.tab_btn_rank, "经营分析"));
        tabMenuItems.add(new KBottomTabItem(R.drawable.tab_btn_my, "我的"));

        tabFragmentMap = new HashMap<String, Fragment>();
        tabFragmentMap.put("e通宝", new KETongBaoFragment());
        tabFragmentMap.put("经营分析", new KOrderRankFragment());
        tabFragmentMap.put("我的", new KUserCenterFragment());

        tabMenuAdapter = new KBottomMenuAdapter(this);
        tabMenuAdapter.setTabItemList(tabMenuItems);
        tabMenuGrid.setAdapter(tabMenuAdapter);
        tabMenuAdapter.setSelection(0);
        tabMenuGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KBottomTabItem selectedMenu = tabMenuItems.get(position);
                changeTab(position, selectedMenu);
            }
        });
    }

    public void showRefresh(final OnClickListener onRefreshClickListener) {
        btnRefresh.setVisibility(View.VISIBLE);
        btnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRefreshClickListener != null) {
                    onRefreshClickListener.onClick(v);
                }
            }
        });
    }

    public void hideRefresh() {
        this.btnRefresh.setVisibility(View.GONE);
    }

    public void showTitle(String title) {
        this.titleView.setVisibility(View.VISIBLE);
        this.titleView.setText(title);
    }

    public void hideTitle() {
        this.titleView.setVisibility(View.GONE);
    }

    public void showLeftTitle(String title) {
        this.leftTitle.setVisibility(View.VISIBLE);
        this.leftTitle.setText(title);
    }

    public void hideLeftTitle() {
        this.leftTitle.setVisibility(View.GONE);
    }

    private void changeTab(int position, KBottomTabItem selectedMenu) {
        Fragment selectedFragment = tabFragmentMap.get(selectedMenu.getTabLabel());
        if (selectedFragment == activeFragment) {
            return;
        }
        activeFragment = selectedFragment;
        String tabLabel = selectedMenu.getTabLabel();
        tabMenuAdapter.setSelection(position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_content, activeFragment);
        if (tabLabel.equals("e通宝")) {
            String storeName = "";
            if (KBaseApplication.getInstance() != null) {
                storeName = KBaseApplication.getInstance().getUserInfoStub().getName();
            }
            if (storeName != null && !storeName.equals("")) {
                hideTitle();
                showLeftTitle("e通宝-" + storeName);
            } else {
                hideLeftTitle();
                showTitle(tabLabel);
            }
        } else {
            hideLeftTitle();
            showTitle(tabLabel);
        }
        titleView.setText(tabLabel);
        transaction.commitAllowingStateLoss();
    }

    private boolean isWaitingExit = false;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isWaitingExit) {
                isWaitingExit = false;
                KBaseApplication.getInstance().logout();
                finish();
            } else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                isWaitingExit = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isWaitingExit = false;
                    }
                }, 2000);
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
