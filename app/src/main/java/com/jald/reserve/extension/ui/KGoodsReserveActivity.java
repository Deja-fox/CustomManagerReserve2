package com.jald.reserve.extension.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.fragment.KGoodsSelectFragment;
import com.jald.reserve.extension.ui.fragment.KGoodsSelectFragmentV2;
import com.jald.reserve.ui.KBaseActivity;

import org.simple.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 商品订购
 */
public class KGoodsReserveActivity extends KBaseActivity {

    public static final String INTENT_KEY_CUSTOM_INFO = "KeyCustomInfo";

    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.searchGoods)
    ImageView searchGoods;

    private CustomListResponseBean.KCustomBean customInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_goods_reserve);
        customInfoBean = (CustomListResponseBean.KCustomBean) getIntent().getSerializableExtra(INTENT_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(this, "没有传递客户信息,无法进行商品订购", Toast.LENGTH_SHORT).show();
            return;
        }
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            loadGoodsSelectFragment();
        }

        title.setText("商品订购（"+KBaseApplication.getInstance().getUserInfoStub().getSupplier_note()+"）");
    }


    void loadGoodsSelectFragment() {
        KGoodsSelectFragmentV2 goodsSelectFragment = new KGoodsSelectFragmentV2();
        Bundle args = new Bundle();
        args.putSerializable(KGoodsSelectFragment.ARGU_KEY_CUSTOM_INFO, customInfoBean);
        goodsSelectFragment.setArguments(args);
        changeFragment(goodsSelectFragment, false);
    }


    @OnClick(R.id.searchGoods)
    void onSearchClick(View v) {
        EventBus.getDefault().post(new KGoodsSelectFragmentV2.GoodsSearchEvent());
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
        this.title.setText(title+"("+KBaseApplication.getInstance().getUserInfoStub().getSupplier_note()+")");
    }

    public void hideSearch() {
        this.searchGoods.setVisibility(View.GONE);
    }

    public void showSearch() {
        this.searchGoods.setVisibility(View.VISIBLE);
    }


}
