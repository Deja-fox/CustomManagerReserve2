package com.jald.reserve.extension.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.fragment.AgreementListFragment;
import com.jald.reserve.ui.KBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 协议列表选择页
 */
public class AgreementListActivity extends KBaseActivity {
    //启动方式
//    Intent intent = new Intent(this, FuturesListActivity.class);
//    intent.putExtra(FuturesListActivity.INTENT_KEY_CUSTOM_INFO, customInfo);
//    startActivity(intent);
    public static final String INTENT_KEY_CUSTOM_INFO = "KeyCustomInfo";

    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.agreeOrderIv)
    ImageView agreeOrderIv;

    private CustomListResponseBean.KCustomBean customInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futures_goods);
        customInfoBean = (CustomListResponseBean.KCustomBean) getIntent().getSerializableExtra(INTENT_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(this, "没有传递客户信息", Toast.LENGTH_SHORT).show();
            finish();
        }
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            loadGoodsSelectFragment();
        }
    }

    /**
     * 协议订单
     *
     * @param view
     */
    @OnClick(R.id.agreeOrderIv)
    void onAgreeOrderIvClick(View view) {
        //跳转协议订单
        Intent intent = new Intent(this, AgreementOrderListActivity.class);
        intent.putExtra(AgreementOrderListActivity.INTENT_KEY_CUSTOM_INFO, customInfoBean);
        startActivity(intent);
    }

    void loadGoodsSelectFragment() {
        AgreementListFragment goodsSelectFragment = new AgreementListFragment();
        Bundle args = new Bundle();
        args.putSerializable(AgreementListFragment.ARGU_KEY_CUSTOM_INFO, customInfoBean);
        goodsSelectFragment.setArguments(args);
        changeFragment(goodsSelectFragment, false);
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

    public void changeTitle(String title) {
        this.title.setText(title);
    }


}
