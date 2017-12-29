package com.jald.reserve.extension.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.fragment.KCustomListFragment;
import com.jald.reserve.extension.ui.fragment.KOrderListFragment;
import com.jald.reserve.ui.KBaseActivity;
import com.jald.reserve.ui.ScannerActivity;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.data;

/**
 * 首页
 */
public class KCustomManagerMainActivity extends KBaseActivity {

    @Bind(R.id.tabIndicator)
    FixedIndicatorView tabIndicator;
    @Bind(R.id.fragmentViewPager)
    ViewPager fragmentViewPager;
    IndicatorViewPager indicatorViewPager;
    @Bind(R.id.qr_code_btn)
    ImageView qrCodeBtn;
    private KCustomListFragment custonFrg = new KCustomListFragment();
    JSONArray supListdata = new JSONArray();

    @Bind(R.id.settings)
    ImageView settings;

    ArrayList<String> stpidlist=new ArrayList<>();

    int currentindex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_custom_manager_main);
        ButterKnife.bind(this);
        initUI();
        //设置别名
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.addExclusiveAlias(KBaseApplication.getInstance().getUserInfoStub
                        ().getTp_id(), "tp_id",
                new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean isSuccess, String message) {
                        Log.e("mytoken", message + "----保存的是：" + KBaseApplication.getInstance().getUserInfoStub
                                ().getTp_id());
                    }
                });

    }

    private void initUI() {
        try {
            if(KBaseApplication.getInstance().getUserInfoStub().getStplist()!=null){
                supListdata=new JSONArray(KBaseApplication.getInstance().getUserInfoStub().getStplist());

                for(int i=0;i<supListdata.length();i++){

                    if(supListdata.getJSONObject(i).optString("is_propietary").equals("1")){

                        stpidlist.add(supListdata.getJSONObject(i).optString("supplier_note")+"(自营)");
                    }else{
                        stpidlist.add(supListdata.getJSONObject(i).optString("supplier_note")+"(辅销)");

                    }

                }

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionsPickerView pvOptions = new  OptionsPickerView.Builder(KCustomManagerMainActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {


                        KUserInfoStub loginInfo=KBaseApplication.getInstance().getUserInfoStub();
                        try {
                            loginInfo.setStpId(supListdata.getJSONObject(options1).optString("supplier_tp_id"));
                            loginInfo.setMtpId(supListdata.getJSONObject(options1).optString("client_manager_tp_id"));
                            loginInfo.setSupplier_note(supListdata.getJSONObject(options1).optString("supplier_note"));
                            KBaseApplication.getInstance().setUserInfoStub(loginInfo);
                            currentindex=options1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }).setSelectOptions(currentindex).setCancelText("搜索").build();
                pvOptions.setPicker(stpidlist);
                pvOptions.show();
            }
        });


        int colorThemeBlue = getResources().getColor(R.color.theme_light_blue);
        tabIndicator.setScrollBar(new ColorBar(getApplicationContext(), colorThemeBlue, 5));
        float unSelectSize = 15;
        float selectSize = unSelectSize * 1.1f;
        int selectColor = colorThemeBlue;
        int unSelectColor = Color.parseColor("#666666");
        tabIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
        indicatorViewPager = new IndicatorViewPager(tabIndicator, fragmentViewPager);
        indicatorViewPager.setPageOffscreenLimit(1);
        indicatorViewPager.setAdapter(new IndicatorViewPager.IndicatorFragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public View getViewForTab(int position, View convertView, ViewGroup container) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.k_tab_indicator, container, false);
                }
                TextView textView = (TextView) convertView;
                if (position == 0) {
                    textView.setText("客户列表");
                } else {
                    textView.setText("订单查询");
                }
                return convertView;
            }

            @Override
            public Fragment getFragmentForPage(int position) {
                if (position == 0) {
                    return custonFrg;
//                    return new KCustomListFragment();
                } else {
                    return new KOrderListFragment();
                }
            }
        });
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

    boolean isWaitingExit = false;

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

    @OnClick(R.id.qr_code_btn)
    void onQrCodeBtnClick(View v) {
        //二维码扫描
        ArrayList<CustomListResponseBean.KCustomBean> bean = custonFrg.getCustomListData();
        if (bean == null || bean.isEmpty()) {
            Toast.makeText(KCustomManagerMainActivity.this, "客户列表信息获取失败，请刷新重试", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, ScannerActivity.class);
            intent.putExtra("custom", bean);
            startActivity(intent);
        }
    }
}
