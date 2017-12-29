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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KAccountChargeRequestBean;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KAccountChargeActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.StringUtil;

import java.util.List;

public class KAccountChargeFragment extends Fragment {

    public static final String INTENT_KEY_BINED_CARD_LIST = "KeyBindedCardList";

    private KAccountChargeActivity parent;
    private View mRoot;

    private RelativeLayout btnSelectCard;
    private ImageView imgBankLogo;
    private TextView txtAccountNo;
    private TextView txtBankName;
    private TextView txtIsDefault;

    private EditText edtPassword;
    private EditText edtChargeAmount;
    private Button btnCharge;

    private List<KBankCardListResponseBean.KBankCardItemBean> bindedCardList;
    private KBankCardListResponseBean.KBankCardItemBean selectedCardItem;

    private KAccountChargeRequestBean requestFormData = new KAccountChargeRequestBean();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parent = (KAccountChargeActivity) activity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        this.bindedCardList = (List<KBankCardListResponseBean.KBankCardItemBean>) args.getSerializable(INTENT_KEY_BINED_CARD_LIST);
        this.mRoot = inflater.inflate(R.layout.k_fragment_account_charge_main, container, false);
        this.btnSelectCard = (RelativeLayout) mRoot.findViewById(R.id.select_card);
        this.imgBankLogo = (ImageView) mRoot.findViewById(R.id.bank_logo);
        this.txtAccountNo = (TextView) mRoot.findViewById(R.id.account_no);
        this.txtBankName = (TextView) mRoot.findViewById(R.id.bank_name);
        this.txtIsDefault = (TextView) mRoot.findViewById(R.id.is_default);

        this.edtChargeAmount = (EditText) mRoot.findViewById(R.id.charge_money);
        this.edtPassword = (EditText) mRoot.findViewById(R.id.password);
        this.btnCharge = (Button) mRoot.findViewById(R.id.btn_charge_submit);

        this.selectedCardItem = this.bindedCardList.get(0);
        for (int i = 0; i < this.bindedCardList.size(); ++i) {
            KBankCardListResponseBean.KBankCardItemBean item = this.bindedCardList.get(i);
            if (item.getIs_default().equals("1")) {
                this.selectedCardItem = item;
            }
        }

        setSelectedCarItemToUI(this.selectedCardItem);

        this.btnSelectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.changeToCardSelectFragment();
            }
        });

        this.btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChargeSubmit();
            }
        });
        return this.mRoot;
    }

    public void setSelectedCarItemToUI(KBankCardListResponseBean.KBankCardItemBean selectedItem) {
        this.selectedCardItem = selectedItem;
        String logResName = this.selectedCardItem.getBank_id();
        logResName = "bank_" + logResName;
        int logoResId = getActivity().getResources().getIdentifier(logResName, "drawable", getActivity().getPackageName());
        this.imgBankLogo.setImageResource(logoResId);
        this.txtAccountNo.setText(StringUtil.formatDebitCardAccount(this.selectedCardItem.getDebit_accont_no()));
        this.txtBankName.setText(this.selectedCardItem.getBank_name());
        String isDefault = this.selectedCardItem.getIs_default().trim();
        if (isDefault.equals("1")) {
            this.txtIsDefault.setVisibility(View.VISIBLE);
        } else {
            this.txtIsDefault.setVisibility(View.GONE);
        }
    }

    private boolean inputCheck() {
        String accountBankId = this.selectedCardItem.getBank_id().trim();
        String accountNo = this.selectedCardItem.getDebit_accont_no().trim();
        String transAmount = this.edtChargeAmount.getText().toString().trim();
        String password = this.edtPassword.getText().toString().trim();

        if (StringUtil.isStrEmpty(transAmount)) {
            Toast.makeText(parent, "请输入大于1元的充值金额", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Double.parseDouble(transAmount) < 1.0) {
            Toast.makeText(parent, "请输入大于1元的充值金额", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isStrEmpty(password)) {
            Toast.makeText(parent, "请输入交易密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        this.requestFormData.setAcc_bank_id(accountBankId);
        this.requestFormData.setAcc_no(accountNo);
        this.requestFormData.setPayment_password(MD5Tools.MD5(password));
        this.requestFormData.setTrans_amt(transAmount);
        double amount = Double.parseDouble(transAmount);
        String fee = unionPayFee(amount);
        this.requestFormData.setFee(fee);
        return true;
    }

    private String unionPayFee(double rechargeAmt) {
        if (rechargeAmt > 0 && rechargeAmt < 999) {
            return "1";
        } else if (rechargeAmt >= 998.5 && rechargeAmt <= 4998.5) {
            return "1.5";
        } else {
            return "2";
        }
    }

    private void doChargeSubmit() {
        if (inputCheck()) {
            hideSoftInputKeyBoard();
            DialogProvider.alert(parent, "信息确认", "充值金额:" + requestFormData.getTrans_amt() + "元,手续费:" + requestFormData.getFee() + "元", "确认转入", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    DialogProvider.showProgressBar(parent, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            KHttpClient.singleInstance().cancel(parent);
                        }
                    });
                    double transAmt = Double.parseDouble(requestFormData.getTrans_amt());
                    double fee = Double.parseDouble(requestFormData.getFee());
                    requestFormData.setTrans_amt(String.valueOf(transAmt + fee));
                    KHttpClient.singleInstance().postData(parent, KHttpAdress.RECHARGE, requestFormData, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                        @Override
                        protected void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                            DialogProvider.hideProgressBar();
                            if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                                Toast.makeText(parent, "充值成功", Toast.LENGTH_SHORT).show();
                                edtChargeAmount.getEditableText().clear();
                                edtPassword.getEditableText().clear();
                                getActivity().setResult(Activity.RESULT_OK);
                            }
                        }
                    });
                }
            }, "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideProgressBar();
                    DialogProvider.hideAlertDialog();
                }
            });
        }
    }

    private void hideSoftInputKeyBoard() {
        InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}