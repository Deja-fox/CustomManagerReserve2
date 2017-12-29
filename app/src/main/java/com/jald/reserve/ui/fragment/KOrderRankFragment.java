package com.jald.reserve.ui.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KCommodityOrderRankAdapter;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KCommodityOrderRankResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoCustRankResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.ExtendedGridView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Random;

public class KOrderRankFragment extends Fragment {

    private View mRoot;

    @ViewInject(R.id.ball_container)
    private View viewBallContainer;

    @ViewInject(R.id.rank_text_container)
    private View viewRankTextContainer;


    @ViewInject(R.id.title_city_order_rank)
    private ImageView imgTitleCityRank;

    @ViewInject(R.id.title_cust_order_rank)
    private ImageView imgTitleCustRank;

    @ViewInject(R.id.city_commodity_rank_grid)
    private ExtendedGridView gridCityCommoRank;

    @ViewInject(R.id.cust_commodity_rank_grid)
    private ExtendedGridView gridCustCommoRank;

    private KCommodityOrderRankAdapter cityRankAdapter;
    private KCommodityOrderRankAdapter custRankAdapter;

    @ViewInject(R.id.city_name)
    private TextView txtCityName;

    @ViewInject(R.id.city_rank)
    private TextView txtCityRank;

    @ViewInject(R.id.dept_name)
    private TextView txtDeptName;

    @ViewInject(R.id.dept_rank)
    private TextView txtDeptRank;

    @ViewInject(R.id.city_rank_first)
    private TextView txtCityRankFirst;

    @ViewInject(R.id.city_rank_second)
    private TextView txtCityRankSecond;

    @ViewInject(R.id.city_rank_third)
    private TextView txtCityRankThird;

    @ViewInject(R.id.cust_rank_first)
    private TextView txtCustRankFirst;

    @ViewInject(R.id.cust_rank_second)
    private TextView txtCustRankSecond;

    @ViewInject(R.id.cust_rank_third)
    private TextView txtCustRankThird;

    private boolean hasDataLoaded = false;

    private KTobaccoCustRankResponseBean cusRankBean;
    private KCommodityOrderRankResponseBean cityRankInfo = new KCommodityOrderRankResponseBean();
    private KCommodityOrderRankResponseBean custRankInfo = new KCommodityOrderRankResponseBean();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_order_rank, container, false);
        x.view().inject(this, this.mRoot);
        float desity = getResources().getDisplayMetrics().density;
        int sWidth = getResources().getDisplayMetrics().widthPixels;
        int size = (int) (sWidth / 2.0);
        if (size < desity * 220) {
            size = (int) (desity * 220);
        }
        viewBallContainer.getLayoutParams().height = size;
        viewBallContainer.getLayoutParams().width = size;
        viewRankTextContainer.getLayoutParams().height = (int) (viewBallContainer.getLayoutParams().height * 0.65);
        viewRankTextContainer.getLayoutParams().width = (int) (viewBallContainer.getLayoutParams().width * 0.65);

        cityRankAdapter = new KCommodityOrderRankAdapter(getActivity());
        cityRankAdapter.setCommodityRankInfo(cityRankInfo);
        this.gridCityCommoRank.setAdapter(cityRankAdapter);

        custRankAdapter = new KCommodityOrderRankAdapter(getActivity());
        custRankAdapter.setCommodityRankInfo(custRankInfo);
        this.gridCustCommoRank.setAdapter(custRankAdapter);
        return this.mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!hasDataLoaded) {
            loadData();
        }
    }

    private void loadData() {
        hasDataLoaded = false;
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TOBACCO_CUST_RANK, json.toJSONString(),
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            cusRankBean = JSON.parseObject(result.getContent(), KTobaccoCustRankResponseBean.class);
                            txtCityName.setText(cusRankBean.getCom_name());
                            txtCityRank.setText("排行" + cusRankBean.getCom_ranking() + "名");
                            txtDeptName.setText(cusRankBean.getDpt_name());
                            txtDeptRank.setText("排行" + cusRankBean.getDpt_ranking() + "名");

                            loadCommodityCityRankList();
                        }
                    }
                });
    }

    private void loadCommodityCityRankList() {
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject json = new JSONObject();
        json.put("page_num", "1");
        json.put("page_size", "10");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TOBACCO_COMMODITY_CITY_RANK, json.toJSONString(),
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            cityRankInfo = JSON.parseObject(result.getContent(), KCommodityOrderRankResponseBean.class);
                            if (cityRankInfo != null) {
                                if (cityRankInfo.getList().size() >= 1) {
                                    txtCityRankFirst.setText(cityRankInfo.getList().get(0).getItem_name());
                                }
                                if (cityRankInfo.getList().size() >= 2) {
                                    txtCityRankSecond.setText(cityRankInfo.getList().get(1).getItem_name());
                                }
                                if (cityRankInfo.getList().size() >= 3) {
                                    txtCityRankThird.setText(cityRankInfo.getList().get(2).getItem_name());
                                }
                            }
                            cityRankAdapter.setCommodityRankInfo(cityRankInfo);

                            loadCommodityCustRankList();
                        }
                    }
                });
    }

    private void loadCommodityCustRankList() {
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject json = new JSONObject();
        json.put("page_num", "1");
        json.put("page_size", "10");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TOBACCO_COMMODITY_CUST_RANK, json.toJSONString(),
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            custRankInfo = JSON.parseObject(result.getContent(), KCommodityOrderRankResponseBean.class);
                            if (custRankInfo != null) {
                                if (custRankInfo.getList().size() >= 1) {
                                    txtCustRankFirst.setText(custRankInfo.getList().get(0).getItem_name());
                                }
                                if (custRankInfo.getList().size() >= 2) {
                                    txtCustRankSecond.setText(custRankInfo.getList().get(1).getItem_name());
                                }
                                if (custRankInfo.getList().size() >= 3) {
                                    txtCustRankThird.setText(custRankInfo.getList().get(2).getItem_name());
                                }
                            }
                            custRankAdapter.setCommodityRankInfo(custRankInfo);

                            hasDataLoaded = true;
                        }
                    }
                });
    }

    @Event(R.id.title_city_order_rank)
    private void onTitleCityRankClick(View view) {
        if (this.imgTitleCityRank.getTag() == null) {
            this.imgTitleCityRank.setTag("0");
        }
        if (this.imgTitleCityRank.getTag().equals("0")) {
            this.imgTitleCityRank.setImageResource(R.drawable.icon_arrow_bottom);
            this.imgTitleCityRank.setTag("1");
            this.gridCityCommoRank.setVisibility(View.VISIBLE);
        } else {
            this.imgTitleCityRank.setTag("0");
            this.imgTitleCityRank.setImageResource(R.drawable.icon_arrow_top);
            this.gridCityCommoRank.setVisibility(View.GONE);
        }
    }

    @Event(R.id.title_cust_order_rank)
    private void onTitleCustRankClick(View view) {
        if (this.imgTitleCustRank.getTag() == null) {
            this.imgTitleCustRank.setTag("0");
        }
        if (this.imgTitleCustRank.getTag().equals("0")) {
            this.imgTitleCustRank.setImageResource(R.drawable.icon_arrow_bottom);
            this.imgTitleCustRank.setTag("1");
            this.gridCustCommoRank.setVisibility(View.VISIBLE);
        } else {
            this.imgTitleCustRank.setTag("0");
            this.imgTitleCustRank.setImageResource(R.drawable.icon_arrow_top);
            this.gridCustCommoRank.setVisibility(View.GONE);
        }
    }

}
