package com.jald.reserve.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ViewUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KSpinnerChargePhoneFeeAdapter;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.fragment.KTelChargeBillListFragment;
import com.jald.reserve.ui.fragment.KTelChargeBillListFragment.VisiblityListener;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.ValueUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.k_activity_phonefee_charge_layout)
public class KPhonefeeChargeActivity extends KBaseActivity {

    public static final String TAG = KPhonefeeChargeActivity.class.getSimpleName();

    private StepOneFragment stepOneFragment;
    private StepTwoFragment stepTwoFragment;
    private KTelChargeBillListFragment telBillFragment;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.charge_record)
    private TextView chargeRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        stepOneFragment = new StepOneFragment();
        stepTwoFragment = new StepTwoFragment();
        telBillFragment = new KTelChargeBillListFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, stepOneFragment);
        transaction.commit();
    }

    public void changeTitle(String title) {
        this.title.setText(title);
    }

    public void setRightMenuVisiblity(boolean isShow) {
        if (isShow) {
            this.chargeRecord.setVisibility(View.VISIBLE);
        } else {
            this.chargeRecord.setVisibility(View.GONE);
        }
    }

    private void changeToNextStepFragment(Bundle args) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        stepTwoFragment.setArguments(args);
        transaction.replace(R.id.fragment_container, stepTwoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Event(R.id.charge_record)
    private void onChargeRecordClick(View v) {
        this.telBillFragment.setVisiblityListener(new VisiblityListener() {
            @Override
            public void onShow() {
                changeTitle("充值记录");
                setRightMenuVisiblity(false);
            }

            @Override
            public void onHide() {
                changeTitle("话费充值");
                setRightMenuVisiblity(true);
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container, this.telBillFragment, "telBillFragment");
        transaction.addToBackStack("telBillFragmentEntry");
        transaction.commitAllowingStateLoss();
    }

    public static class ChargeAmountInfoBean {
        private int total;
        private List<ChargeAmountItemBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ChargeAmountItemBean> getList() {
            return list;
        }

        public void setList(List<ChargeAmountItemBean> list) {
            this.list = list;
        }

    }

    public static class ChargeAmountItemBean {
        private String recharge_amt;
        private String return_amt;

        public String getRecharge_amt() {
            return recharge_amt;
        }

        public void setRecharge_amt(String recharge_amt) {
            this.recharge_amt = recharge_amt;
        }

        public String getReturn_amt() {
            return return_amt;
        }

        public void setReturn_amt(String return_amt) {
            this.return_amt = return_amt;
        }
    }

    public static class StepOneFragment extends Fragment {

        private View mRoot;
        @ViewInject(R.id.phonefee_phonenum_edittext)
        private EditText edtPhoneNum;
        @ViewInject(R.id.select_phonefee_amount)
        private Spinner amountSpinner;
        @ViewInject(R.id.btn_next_step)
        private Button btnNext;

        private KSpinnerChargePhoneFeeAdapter chargeAmountListAdapter;
        private List<ChargeAmountItemBean> amountChoiceList = new ArrayList<ChargeAmountItemBean>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (mRoot != null) {
                ((ViewGroup) mRoot.getParent()).removeAllViews();
                return mRoot;
            }
            mRoot = inflater.inflate(R.layout.k_fragment_phonefee_charge_layout, container, false);
            x.view().inject(this, mRoot);
            this.amountSpinner.setPrompt("请选择充值金额");
            this.chargeAmountListAdapter = new KSpinnerChargePhoneFeeAdapter(getActivity());
            this.chargeAmountListAdapter.setAmountChoiceList(amountChoiceList);
            this.amountSpinner.setAdapter(chargeAmountListAdapter);
            SharedPreferences shared = getActivity().getPreferences(Context.MODE_PRIVATE);
            if (shared.getString("lastSuccessChargeTelNumber", null) != null) {
                this.edtPhoneNum.getEditableText().clear();
                this.edtPhoneNum.getEditableText().append(shared.getString("lastSuccessChargeTelNumber", null));
            }
            getChargeAmountList();
            this.edtPhoneNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 11) {
                        if (!ValueUtil.isMobileNO(s.toString())) {
                            Toast.makeText(getActivity(), "手机号码的格式不正确", Toast.LENGTH_SHORT).show();
                        } else {
                            hideSoftInputKeyBoard();
                            edtPhoneNum.clearFocus();
                            mRoot.findViewById(R.id.dummy_foucus_target).requestFocus();
                        }
                    }
                }
            });
            return mRoot;
        }

        @Event(R.id.btn_next_step)
        private void onNextSteClick(View view) {
            if (!ValueUtil.isMobileNO(edtPhoneNum.getText().toString())) {
                Toast.makeText(getActivity(), "手机号码的格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            int selectedItemPos = amountSpinner.getSelectedItemPosition();
            final String chargeAmount = amountChoiceList.get(selectedItemPos).getRecharge_amt();
            final String telNumber = edtPhoneNum.getText().toString();
            DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            JSONObject json = new JSONObject();
            json.put("tel", telNumber);
            json.put("amt", chargeAmount);
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TEL_CHARGE_VALIDATE, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        Log.e(TAG, "手机号验证接口获取手机号信息成功");
                        JSONObject retJSON = JSONObject.parseObject(result.getContent());
                        String spName = retJSON.getString("boss_type");
                        String province = retJSON.getString("province");
                        String city = retJSON.getString("city");
                        Bundle args = new Bundle();
                        args.putString("telNumber", telNumber);
                        args.putString("chargeAmount", chargeAmount);
                        args.putString("spName", spName);
                        args.putString("province", province);
                        args.putString("city", city);
                        ((KPhonefeeChargeActivity) getActivity()).changeToNextStepFragment(args);
                    }
                }
            });
        }

        private void getChargeAmountList() {
            DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            JSONObject json = new JSONObject();
            json.put("random", ValueUtil.getRandomString(8));
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TEL_CHARGE_AMOUNTLIST, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        Log.e(TAG, "获取充值金额成功");
                        ChargeAmountInfoBean amountInfo = JSON.parseObject(result.getContent(), ChargeAmountInfoBean.class);
                        amountChoiceList.clear();
                        amountChoiceList.addAll(amountInfo.getList());
                        chargeAmountListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        private void hideSoftInputKeyBoard() {
            InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }
    }

    public static class StepTwoFragment extends Fragment {

        private View mRoot;
        @ViewInject(R.id.charge_number_confirm)
        private TextView txtPhoneNumConfirm;

        @ViewInject(R.id.charge_amount_confirm)
        private TextView txtChargeAmountConfirm;

        @ViewInject(R.id.tel_own_place)
        private TextView txtOwnPlace;

        @ViewInject(R.id.edt_charge_paypwd)
        private EditText edtPwd;

        @ViewInject(R.id.btn_charge_submit)
        private Button btnChargeSubmit;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (mRoot != null) {
                ((ViewGroup) mRoot.getParent()).removeAllViews();
                return mRoot;
            }
            this.mRoot = inflater.inflate(R.layout.k_fragment_phonefee_charge_step2_layout, container, false);
            x.view().inject(this, mRoot);
            return this.mRoot;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            txtPhoneNumConfirm.setText(getArguments().getString("telNumber"));
            txtChargeAmountConfirm.setText(getArguments().getString("chargeAmount") + "元");
            String spName = getArguments().getString("spName");
            if (spName.equals("1")) {
                spName = "移动";
            } else if (spName.equals("2")) {
                spName = "联通";
            } else {
                spName = "电信";
            }
            txtOwnPlace.setText(getArguments().getString("province") + "-" + getArguments().getString("city") + spName);
        }

        @Event(R.id.btn_charge_submit)
        private void onChargeSubmitClick(View view) {
            if (edtPwd.getText().toString().length() == 0) {
                Toast.makeText(getActivity(), "请输入支付密码", Toast.LENGTH_SHORT).show();
                return;
            }
            hideSoftInputKeyBoard();
            DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            JSONObject json = new JSONObject();
            json.put("tel", getArguments().getString("telNumber"));
            json.put("amt", getArguments().getString("chargeAmount"));
            String pwd = this.edtPwd.getText().toString();
            pwd = MD5Tools.MD5(pwd);
            json.put("password", pwd);
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.TEL_CHARGE, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        SharedPreferences shared = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("lastSuccessChargeTelNumber", getArguments().getString("telNumber"));
                        editor.commit();
                        DialogProvider.alert(getActivity(), "温馨提示", "提交订单成功,预计10分钟内到账,您可到账单查询模块查看订单状态", "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogProvider.hideAlertDialog();
                                getActivity().finish();
                            }
                        });
                    }
                }
            });
        }

        private void hideSoftInputKeyBoard() {
            InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }
    }

}
