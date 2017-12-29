package com.jald.reserve.extension.ui;


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
import com.jald.reserve.extension.bean.response.FuturesResponseBean;
import com.jald.reserve.extension.ui.fragment.FuturesGoodsFragment;
import com.jald.reserve.ui.KBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 期货产品采集页
 */
public class FuturesGoodsActivity extends KBaseActivity {
    //启动方式
//    Intent intent = new Intent(this, FuturesGoodsActivity.class);
//    intent.putExtra(FuturesGoodsActivity.INTENT_KEY_CUSTOM_INFO, customInfo);
//    startActivity(intent);
    public static final String INTENT_KEY_CUSTOM_INFO = "KeyCustomInfo";
    public static final String ARGU_KEY_AGREEMENT_INFO = "KeyAgreementInfo";

    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.agreeOrderIv)
    ImageView agreeOrderIv;
    private CustomListResponseBean.KCustomBean customInfoBean;
    private FuturesResponseBean.FuturesBean getBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futures_goods);
        customInfoBean = (CustomListResponseBean.KCustomBean) getIntent().getSerializableExtra(INTENT_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(this, "没有传递客户信息,无法进行商品订购", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        getBean = (FuturesResponseBean.FuturesBean) getIntent().getSerializableExtra(ARGU_KEY_AGREEMENT_INFO);
        if (getBean == null) {
            Toast.makeText(this, "获取商品信息失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ButterKnife.bind(this);
        agreeOrderIv.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            loadGoodsSelectFragment();
        }
    }


    void loadGoodsSelectFragment() {
        FuturesGoodsFragment goodsSelectFragment = new FuturesGoodsFragment();
        Bundle args = new Bundle();
        args.putSerializable(FuturesGoodsFragment.ARGU_KEY_CUSTOM_INFO, customInfoBean);
        args.putSerializable(FuturesGoodsFragment.ARGU_KEY_AGREEMENT_INFO, getBean);
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
