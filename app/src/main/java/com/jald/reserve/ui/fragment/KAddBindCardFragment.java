package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KBankSelectSpinnerAdapter;
import com.jald.reserve.bean.http.request.KBankCardBindRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KBankItemBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KMyBankCardActivity;
import com.jald.reserve.util.CheckUtil;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.StringUtil;
import com.jald.reserve.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class KAddBindCardFragment extends Fragment {

    private static final String APP_CACHE_TAG_KEY_SUPPORT_BANKS = "KeySupportBankList";

    private KMyBankCardActivity mParent;
    private View mRoot;
    private EditText edtAccountName;
    private EditText edtIdentifyNo;
    private EditText edtAccountNo;
    private Button mBindCard;
    private Spinner mBankSeletctSpinner;
    private KBankSelectSpinnerAdapter adapter;
    private List<KBankItemBean> bankList = new ArrayList<KBankItemBean>();

    private KBankCardBindRequestBean formData;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParent = (KMyBankCardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_add_bank_card, container, false);

        this.edtAccountName = (EditText) mRoot.findViewById(R.id.account_name);
        this.edtIdentifyNo = (EditText) mRoot.findViewById(R.id.id_number);
        this.edtAccountNo = (EditText) mRoot.findViewById(R.id.debit_accont_no);

        this.mBindCard = (Button) mRoot.findViewById(R.id.btn_bind);
        this.mBindCard.setOnClickListener(onBindCardSubmit);

        this.mBankSeletctSpinner = (Spinner) mRoot.findViewById(R.id.bankSelectSpinner);
        this.mBankSeletctSpinner.setPrompt("请选择绑定卡所属的银行");
        this.adapter = new KBankSelectSpinnerAdapter(getActivity());
        this.bankList = new ArrayList<KBankItemBean>();
        this.adapter.setBankList(bankList);
        this.mBankSeletctSpinner.setAdapter(adapter);

        return mRoot;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.mParent.changeTitle("绑定银行卡");
        this.mParent.hideAddButton(true);
        List<KBankItemBean> cachedList = (List<KBankItemBean>) KBaseApplication.getInstance().getTag(APP_CACHE_TAG_KEY_SUPPORT_BANKS);
        if (cachedList == null || cachedList.size() == 0) {
            loadSupportBanckList();
        } else {
            this.bankList = cachedList;
            this.adapter.setBankList(this.bankList);
            this.adapter.notifyDataSetChanged();
        }
    }

    private View.OnClickListener onBindCardSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkInput()) {
                hideSoftInputKeyBoard();
                DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        KHttpClient.singleInstance().cancel(getActivity());
                    }
                });
                KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.BANK_CARD_BIND, formData,
                        KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                            @Override
                            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                                DialogProvider.hideProgressBar();
                                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                                    Toast.makeText(getActivity(), "银行卡绑定成功", Toast.LENGTH_SHORT).show();
                                    mBankSeletctSpinner.setSelection(0);
                                    edtAccountNo.getText().clear();
                                    mParent.onAddNewCardSuccess();
                                }
                            }
                        });
            }
        }
    };

    private boolean checkInput() {
        String accountName = this.edtAccountName.getText().toString();
        String idNumber = this.edtIdentifyNo.getText().toString();
        String accountNo = this.edtAccountNo.getText().toString();
        int selBankIndex = this.mBankSeletctSpinner.getSelectedItemPosition();
        String bankId = this.bankList.get(selBankIndex).getBank_id();
        if (StringUtil.isStrEmpty(accountName)) {
            Toast.makeText(getActivity(), "持卡人姓名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isStrEmpty(idNumber)) {
            Toast.makeText(getActivity(), "身份证号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!CheckUtil.vaildateIdCard(idNumber)) {
            Toast.makeText(getActivity(), "身份证号格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selBankIndex == 0) {
            Toast.makeText(getActivity(), "请选择绑定卡所属银行", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isStrEmpty(accountNo)) {
            Toast.makeText(getActivity(), "银行卡号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (accountNo.trim().length() != 16) {
            Toast.makeText(getActivity(), "请输入正确的银行卡号", Toast.LENGTH_SHORT).show();
            return false;
        }
        this.formData = new KBankCardBindRequestBean();
        this.formData.setAccount_name(accountName);
        this.formData.setBank_id(bankId);
        this.formData.setDebit_accont_no(accountNo);
        this.formData.setId_number(idNumber);
        this.formData.setAccount_category("01");
        this.formData.setAccount_nature("01");
        this.formData.setDebit_bind_type("1");
        return true;
    }

    private void loadSupportBanckList() {
        DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        KHttpClient httpTools = KHttpClient.singleInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_type", "11");
        KUserInfoStub user = new KUserInfoStub();
        httpTools.postData(getActivity(), KHttpAdress.BANK_LIST, jsonObject, user, new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().endsWith(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    bankList.clear();
                    bankList.add(new KBankItemBean(KBankSelectSpinnerAdapter.HEADER_TITLE_ID, "请选择绑定卡所属银行"));
                    String retJson = result.getContent();
                    JSONObject jObj = JSON.parseObject(retJson);
                    String jsonList = jObj.getString("list");
                    List<KBankItemBean> tempList = JSON.parseArray(jsonList, KBankItemBean.class);
                    bankList.addAll(tempList);
                    KBaseApplication.getInstance().setTag(APP_CACHE_TAG_KEY_SUPPORT_BANKS, bankList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void hideSoftInputKeyBoard() {
        InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}