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
import com.jald.reserve.extension.ui.fragment.FuturesGoodsFragment;
import com.jald.reserve.extension.ui.fragment.FuturesListFragment;
import com.jald.reserve.ui.KBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 期货列表选择页
 */
public class FuturesListActivity extends KBaseActivity {
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
            Toast.makeText(this, "没有传递客户信息,无法进行商品订购", Toast.LENGTH_SHORT).show();
            return;
        }
        ButterKnife.bind(this);
        agreeOrderIv.setVisibility(View.GONE);
        title.setText("已签协议列表");
        if (savedInstanceState == null) {
            loadGoodsSelectFragment();
        }
    }


    void loadGoodsSelectFragment() {
        FuturesListFragment goodsSelectFragment = new FuturesListFragment();
        Bundle args = new Bundle();
        args.putSerializable(FuturesListFragment.ARGU_KEY_CUSTOM_INFO, customInfoBean);
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
