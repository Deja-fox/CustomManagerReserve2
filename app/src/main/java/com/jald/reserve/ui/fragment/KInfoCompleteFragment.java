package com.jald.reserve.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KUserUpdateRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.util.CheckUtil;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class KInfoCompleteFragment extends Fragment {

    public static final String TAG = KInfoCompleteFragment.class.getSimpleName();

    public static interface OnInfoCompleteFinishListener {
        public void onInfoCompleteSubmitSuccess();
    }

    public static final String KEY_ARGU_USER_INFO_STUB = "KeyArguUserInfoStub";

    private View mRoot;

    @ViewInject(R.id.info_complete_identify_edittext)
    private EditText edtIdNumber;

    @ViewInject(R.id.info_complete_realname_edittext)
    private EditText edtRealName;

    @ViewInject(R.id.info_complete_paypwd_edittext)
    private EditText edtPayPwd;

    @ViewInject(R.id.info_complete_repaypwd_edittext)
    private EditText edtRePayPwd;

    @ViewInject(R.id.info_complete_submit_btn)
    private Button btnSubmit;

    private OnInfoCompleteFinishListener infoCompleteFinishListener;

    private String idNumber;
    private String realName;
    private String payPwd;
    private String rePayPwd;

    private KUserInfoStub userInfoStub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.k_fragment_info_complete, container, false);
        x.view().inject(this, mRoot);
        this.userInfoStub = (KUserInfoStub) getArguments().getSerializable(KEY_ARGU_USER_INFO_STUB);
        if (this.userInfoStub == null) {
            Log.e(TAG, "调用完善用户信息页,但是没有传递KUserInfoStub信息");
        }
        return mRoot;
    }

    @Event(R.id.info_complete_submit_btn)
    private void onSubmitClick(View view) {
        if (checkInputField()) {
            hideSoftInputKeyBoard();
            DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            KUserUpdateRequestBean infoUpdatePostBean = new KUserUpdateRequestBean();
            infoUpdatePostBean.setIs_reg("1");
            infoUpdatePostBean.setPay_pwd(MD5Tools.MD5(this.payPwd));
            infoUpdatePostBean.setId_number(this.idNumber);
            infoUpdatePostBean.setManager(this.realName);
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.USER_UPDATE, infoUpdatePostBean, userInfoStub, new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess) {
                        if (infoCompleteFinishListener != null) {
                            infoCompleteFinishListener.onInfoCompleteSubmitSuccess();
                        }
                    }
                }
            });
        }
    }

    private boolean checkInputField() {
        this.idNumber = edtIdNumber.getText().toString().trim();
        this.realName = edtRealName.getText().toString().trim();
        this.payPwd = edtPayPwd.getText().toString().trim();
        this.rePayPwd = edtRePayPwd.getText().toString().trim();
        if (!CheckUtil.vaildateIdCard(idNumber)) {
            Toast.makeText(getActivity(), "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.realName.equals("")) {
            Toast.makeText(getActivity(), "请输入真实姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.payPwd.equals("")) {
            Toast.makeText(getActivity(), "请输入支付密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.payPwd.length() < 6) {
            Toast.makeText(getActivity(), "支付密码过短", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.rePayPwd.equals("")) {
            Toast.makeText(getActivity(), "请再次输入支付密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!this.rePayPwd.equals(this.payPwd)) {
            Toast.makeText(getActivity(), "两次输入的支付密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void hideSoftInputKeyBoard() {
        InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setOnInfoCompleteFinishListener(OnInfoCompleteFinishListener infoCompleteFinishListener) {
        this.infoCompleteFinishListener = infoCompleteFinishListener;
    }

}