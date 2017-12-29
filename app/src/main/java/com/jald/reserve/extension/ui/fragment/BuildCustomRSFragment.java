package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.jald.reserve.extension.adapter.BuildCustomRSAdapter;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zbw on 2016/11/16.
 * 说明：建立客户关系列表页
 */

public class BuildCustomRSFragment extends Fragment {

    private View mRoot;

    @Bind(R.id.lvCustomList)
    ListView lvCustomList;
    @Bind(R.id.searchEditText)
    EditText searchEditText;
    @Bind(R.id.dialog)
    TextView dialog;
    @Bind(R.id.searchLayout)
    LinearLayout searchLayout;

    BuildCustomRSAdapter customAdapter;
    ArrayList<CustomListResponseBean.KCustomBean> customListData = new ArrayList<CustomListResponseBean.KCustomBean>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.fragment_build_custom_rs, container, false);
        ButterKnife.bind(this, this.mRoot);
        EventBus.getDefault().register(this);
        initUI();
        return this.mRoot;
    }

    void initUI() {
        this.customAdapter = new BuildCustomRSAdapter(getActivity());
        this.lvCustomList.setAdapter(this.customAdapter);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除按钮
                searchEditText.setText("");
                customAdapter.setCustomListData(new ArrayList<CustomListResponseBean.KCustomBean>());
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //搜索
                    String searchText = searchEditText.getText().toString().trim();
                    if (checkText(searchText)) {
                        getSearchList(searchEditText.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });


    }

    /**
     * 校验规则 10位以上 不能为空 专卖证37开头 手机号
     *
     * @param str
     * @return
     */
    boolean checkText(String str) {
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(getActivity(), "请填写专卖证号或手机号", Toast.LENGTH_SHORT).show();
            return false;
        } else if (str.length() < 10) {
            Toast.makeText(getActivity(), "请填写完整专卖证号或手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void getSearchList(String keyWords) {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("slsman_tp_id", KBaseApplication.getInstance().getUserInfoStub().getUuid());
        postJson.put("keyword", keyWords);//测试用例370724106417
        postJson.put("function", "manager_get_customers_by_keyword");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.COMMON_INFO, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                customListData.clear();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    JSONObject jsonObject1 = JSON.parseObject(result.getContent());
                    JSONObject contentJson = JSON.parseObject(jsonObject1.getString("content"));
                    if (contentJson != null) {
                        if ((contentJson.getString("ret_code")).equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            JSONObject data = JSON.parseObject(contentJson.getString("data"));
                            //解析data
                            if (data.size() == 0) {
                                Toast.makeText(getActivity(), "没有查询到客户", Toast.LENGTH_SHORT).show();
                            } else {
                                //临时只有一项数据
                                CustomListResponseBean.KCustomBean bean = JSON.parseObject(data.toString(), CustomListResponseBean.KCustomBean.class);
                                customListData.add(bean);
                            }
                        } else {
                            Toast.makeText(getActivity(), contentJson.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), result.getRet_msg(), Toast.LENGTH_SHORT).show();
                }
                customAdapter.setCustomListData(customListData);
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
}
