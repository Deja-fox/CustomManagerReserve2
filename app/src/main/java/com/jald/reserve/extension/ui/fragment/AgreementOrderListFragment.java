package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.adapter.AgreementOrderListAdapter;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.FuturesResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.CharacterParser;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 协议订单列表选择页
 */
public class AgreementOrderListFragment extends Fragment {
    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";
    private View mRoot;

    @Bind(R.id.lvCustomList)
    ListView lvCustomList;
    @Bind(R.id.searchEditText)
    EditText searchEditText;
    @Bind(R.id.dialog)
    TextView dialog;
    @Bind(R.id.searchLayout)
    LinearLayout searchLayout;

    CustomListResponseBean.KCustomBean customInfoBean;
    AgreementOrderListAdapter customAdapter;
    ArrayList<FuturesResponseBean.FuturesBean> customListData = new ArrayList<>();


    // 用户筛选完成事件
    public static class CustomFilterFinishEvent {
        public ArrayList<FuturesResponseBean.FuturesBean> filteredCustomList;

        public CustomFilterFinishEvent(ArrayList<FuturesResponseBean.FuturesBean> filteredCustomList) {
            this.filteredCustomList = filteredCustomList;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customInfoBean = (CustomListResponseBean.KCustomBean) getArguments().getSerializable(ARGU_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(getActivity(), "没有传递客户信息", Toast.LENGTH_SHORT).show();
            return;
        }
        loadCustomList();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.fragment_futures_list, container, false);
        ButterKnife.bind(this, this.mRoot);
        EventBus.getDefault().register(this);
        return this.mRoot;
    }

    void initUI() {
        this.customAdapter = new AgreementOrderListAdapter(getActivity());
        this.customAdapter.setCustomListData(customListData);
        this.customAdapter.setCustomInfoBean(customInfoBean);
        this.lvCustomList.setAdapter(this.customAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterData(s.toString());
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                filterData("");
            }
        });


    }

    //加载期货列表
    void loadCustomList() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("manager_tp_id", KBaseApplication.getInstance().getUserInfoStub().getUuid());//测试用例18766140688
        postJson.put("function", "get_feature_lists");
        postJson.put("customer_tp_id", customInfoBean.getTp_id());
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.COMMON_INFO, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                customListData.clear();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    JSONObject jsonObject1 = JSON.parseObject(result.getContent());
                    String contentJson = jsonObject1.getString("content");
                    if (contentJson != null) {
                        FuturesResponseBean rspBean = JSON.parseObject(contentJson, FuturesResponseBean.class);
                        if (rspBean.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            if (rspBean.getData().size() == 0) {
                                Toast.makeText(getActivity(), "协议列表暂无数据", Toast.LENGTH_SHORT).show();
                                customListData = new ArrayList<FuturesResponseBean.FuturesBean>();
                            } else {
                                customListData = rspBean.getData();
                            }
                            initUI();
                        } else {
                            Toast.makeText(getActivity(), rspBean.getRet_msg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), result.getRet_msg(), Toast.LENGTH_SHORT).show();
                }
                DialogProvider.hideProgressBar();
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });


    }


    private void filterData(String filterStr) {
        if (TextUtils.isEmpty(filterStr)) {
            customAdapter.setCustomListData(customListData);
            customAdapter.notifyDataSetInvalidated();
        } else {
            filterCustomInBackground(filterStr);
        }
    }

    @Subscriber
    public void handleCustomFilterFinish(CustomFilterFinishEvent filterFinishEvent) {
        customAdapter.setCustomListData(filterFinishEvent.filteredCustomList);
        customAdapter.notifyDataSetInvalidated();
    }

    private void filterCustomInBackground(final String filterStr) {
        ArrayList<FuturesResponseBean.FuturesBean> filterDateList = new ArrayList<>();
        String condition = filterStr.toLowerCase();
        for (FuturesResponseBean.FuturesBean item : customListData) {
            boolean hasAdded = false;
            // 名称
            String custName = item.getMarket().getTitle().toLowerCase();
            if (custName.contains(condition) || new CharacterParser().getSpelling(custName).contains(condition)) {
                filterDateList.add(item);
                hasAdded = true;
            }
            // 月数
            String manager = item.getMarket().getC_content().toLowerCase();
            if (manager.contains(condition) || new CharacterParser().getSpelling(manager).contains(condition)) {
                if (!hasAdded) {
                    filterDateList.add(item);
                }
            }
        }
        EventBus.getDefault().post(new CustomFilterFinishEvent(filterDateList));
    }
}
