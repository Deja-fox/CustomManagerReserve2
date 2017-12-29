package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.extension.adapter.KCustomListAdapter;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.BuildCustomRSActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.CharacterParser;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.PinyinComparator;
import com.jald.reserve.util.ValueUtil;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.SideBar;
import com.jald.reserve.widget.pullrefresh.RefreshableListViewWrapper;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 客户列表
 */
public class KCustomListFragment extends Fragment {

    private View mRoot;

    @Bind(R.id.lvCustomList)
    RefreshableListViewWrapper refreshListView;
    ListView lvCustomList;


    @Bind(R.id.searchEditText)
    EditText searchEditText;
    @Bind(R.id.dialog)
    TextView dialog;
    @Bind(R.id.sideBar)
    SideBar sideBar;
    @Bind(R.id.searchLayout)
    LinearLayout searchLayout;

    @Bind(R.id.leftbtn)
    Button leftbtn;
    @Bind(R.id.middlebtn)
    Button middlebtn;
    @Bind(R.id.rightbtn)
    Button rightbtn;

    @Bind(R.id.view1)
    View view1;
    @Bind(R.id.view2)
    View view2;
    @Bind(R.id.build_rs)
    ImageView build_im;
    KCustomListAdapter customAdapter;
    ArrayList<CustomListResponseBean.KCustomBean> customListData = new ArrayList<>();

    boolean isDataLoaded = false;

    int checkedIndex = 0;

    // 用户筛选完成事件
    public static class CustomFilterFinishEvent {
        public ArrayList<CustomListResponseBean.KCustomBean> filteredCustomList;

        public CustomFilterFinishEvent(ArrayList<CustomListResponseBean.KCustomBean> filteredCustomList) {
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
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_custom_list, container, false);
        ButterKnife.bind(this, this.mRoot);
        EventBus.getDefault().register(this);
        initUI();
        return this.mRoot;
    }

    void initUI() {
        this.lvCustomList = refreshListView.getListView();
        this.customAdapter = new KCustomListAdapter(getActivity());
        this.lvCustomList.setAdapter(this.customAdapter);

        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = customAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvCustomList.setSelection(position);
                }

            }
        });

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

    @OnClick(R.id.leftbtn)
    void leftbtnClick(View v) {
        changeType(0);
    }

    @OnClick(R.id.middlebtn)
    void middlebtnClick(View v) {
        changeType(1);
    }

    @OnClick(R.id.rightbtn)
    void rightbtnClick(View v) {
        changeType(2);
    }

    @OnClick(R.id.build_rs)
    void buildIm(View v) {
        //跳转建立关系
        Intent intent = new Intent(getActivity(), BuildCustomRSActivity.class);
        startActivity(intent);
    }

    void changeType(int index) {

        leftbtn.setBackgroundResource(R.drawable.bg_corner_white_left);
        middlebtn.setBackgroundResource(R.drawable.bg_corner_white_middle);
        rightbtn.setBackgroundResource(R.drawable.bg_corner_white_right);

        leftbtn.setTextColor(getResources().getColor(R.color.btn_font_color));
        middlebtn.setTextColor(getResources().getColor(R.color.btn_font_color));
        rightbtn.setTextColor(getResources().getColor(R.color.btn_font_color));

        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);


        switch (index) {
            case 0:
                leftbtn.setBackgroundResource(R.drawable.bg_corner_blue_left);
                leftbtn.setTextColor(getResources().getColor(android.R.color.white));
                view1.setVisibility(View.GONE);
                break;
            case 1:
                middlebtn.setBackgroundResource(R.drawable.bg_corner_blue_middle);
                middlebtn.setTextColor(getResources().getColor(android.R.color.white));
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                break;
            case 2:
                rightbtn.setBackgroundResource(R.drawable.bg_corner_blue_right);
                rightbtn.setTextColor(getResources().getColor(android.R.color.white));
                view2.setVisibility(View.GONE);
                break;
        }
        checkedIndex = index;
        filterData(searchEditText.getText().toString());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isDataLoaded) {
            loadCustomList();
        }
    }


    void loadCustomList() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("random", ValueUtil.getRandomString(6));
        postJson.put("stpId", KBaseApplication.getInstance().getUserInfoStub().getStpId());
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.SLSMAN_CUST_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    refreshListView.reset();
                    refreshListView.notifyRefreshComplete();

                    isDataLoaded = true;

                    CustomListResponseBean rspBean = JSON.parseObject(result.getContent(), CustomListResponseBean.class);
                    //存sp_id 更新userInfo
//                    KUserInfoStub userInfo = KBaseApplication.getInstance().getUserInfoStub();
//                    userInfo.setStpId(rspBean.getStpId());
//                    userInfo.setMtpId(rspBean.getMtpId());
//                    KBaseApplication.getInstance().setUserInfoStub(userInfo);

                    if (rspBean.getTotal() == 0) {
                        DialogProvider.hideProgressBar();
                        Toast.makeText(getActivity(), "客户列表暂无数据", Toast.LENGTH_SHORT).show();
                    } else {
                        customListData = rspBean.getList();
                        //用于扫码验证
                        setCustomListData(customListData);
                        generatorPinYinIndex(customListData);
                        Collections.sort(customListData, new PinyinComparator());
                        customAdapter.setCustomListData(customListData);
                        customAdapter.notifyDataSetInvalidated();

                        DialogProvider.hideProgressBar();
                    }
                    refreshListView.setOnPullDownListener(new RefreshableListViewWrapper.OnPullListener() {
                        @Override
                        public void onRefresh() {
                            loadCustomList();
                        }

                        @Override
                        public void onMore() {
                            loadCustomList();
                        }
                    });
                    refreshListView.notifyCloseLoadMore(true);
                } else {
                    refreshListView.notifyRefreshComplete();
                    DialogProvider.hideProgressBar();
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                refreshListView.notifyRefreshComplete();
                isDataLoaded = false;
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                refreshListView.notifyRefreshComplete();
                isDataLoaded = false;
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }


    private void generatorPinYinIndex(ArrayList<CustomListResponseBean.KCustomBean> customListData) {
        if (customListData == null) {
            return;
        }
        for (int i = 0; i < customListData.size(); i++) {
            CustomListResponseBean.KCustomBean kCustomBean = customListData.get(i);
            String pinyin = new CharacterParser().getSpelling(kCustomBean.getCust_name());
            if (!TextUtils.isEmpty(pinyin)) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    kCustomBean.setSort_letter(sortString.toUpperCase());
                } else {
                    kCustomBean.setSort_letter("#");
                }
            } else {
                kCustomBean.setSort_letter("#");
                Toast.makeText(getActivity(), "客户列表数据异常，排序失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void filterData(String filterStr) {
        if (TextUtils.isEmpty(filterStr) && checkedIndex == 0) {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CustomListResponseBean.KCustomBean> filterDateList = new ArrayList<>();
                String condition = filterStr.toLowerCase();
                for (CustomListResponseBean.KCustomBean item : customListData) {
                    boolean hasAdded = false;


                    if (checkedIndex == 1) {
                        if (!item.getIsCredit().equals("1")) {
                            continue;
                        }
                    }
                    if (checkedIndex == 2) {
                        if (!item.getIsCredit().equals("0")) {
                            continue;
                        }
                    }

                    // 店名
                    String custName = item.getCust_name().toLowerCase();
                    if (custName.contains(condition) || new CharacterParser().getSpelling(custName).contains(condition)) {
                        filterDateList.add(item);
                        hasAdded = true;
                    }
                    // 负责人
                    String manager = item.getManager().toLowerCase();
                    if (manager.contains(condition) || new CharacterParser().getSpelling(manager).contains(condition)) {
                        if (!hasAdded) {
                            filterDateList.add(item);
                            hasAdded = true;
                        }
                    }
                    // 专卖证号
                    String liceID = item.getLice_id().toLowerCase();
                    if (liceID.contains(condition) || new CharacterParser().getSpelling(liceID).contains(condition)) {
                        if (!hasAdded) {
                            filterDateList.add(item);
                            hasAdded = true;
                        }
                    }
                    // 手机号
                    String tel = item.getTel().toLowerCase();
                    if (tel.contains(condition) || new CharacterParser().getSpelling(tel).contains(condition)) {
                        if (!hasAdded) {
                            filterDateList.add(item);
                            hasAdded = true;
                        }
                    }
                    // 地址
                    String address = item.getAddress().toLowerCase();
                    if (address.contains(condition) || new CharacterParser().getSpelling(address).contains(condition)) {
                        if (!hasAdded) {
                            filterDateList.add(item);
                        }
                    }
                }
                EventBus.getDefault().post(new CustomFilterFinishEvent(filterDateList));
            }
        }).start();
    }

    public ArrayList<CustomListResponseBean.KCustomBean> getCustomListData() {
        return customListData;
    }

    public void setCustomListData(ArrayList<CustomListResponseBean.KCustomBean> customListData) {
        this.customListData = customListData;
    }
}
