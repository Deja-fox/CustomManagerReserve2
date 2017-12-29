package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KAccountInfoResponseBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KNanYueEAccountMainActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KNanYueEAccountMainFragment extends Fragment {

    @Bind(R.id.nanyueBankCard)
    RelativeLayout nanyueBankCard;
    @Bind(R.id.nanYueEAccountCharge)
    RelativeLayout nanYueEAccountCharge;
    @Bind(R.id.nanyueWithdraw)
    RelativeLayout nanyueWithdraw;
    @Bind(R.id.nanyueChangePwd)
    RelativeLayout nanyueChangePwd;
    @Bind(R.id.nanyueResetPwd)
    RelativeLayout nanyueResetPwd;
    private KNanYueEAccountMainActivity mParent;

    private View mRoot;


    @Bind(R.id.nanyueEAccountBalance)
    TextView nanYueEAccountBalance;

    @Bind(R.id.txtEffectiveAmt)
    TextView txtEffectiveAmt;

    @Bind(R.id.nanyueEAccountActive)
    RelativeLayout btnNanyueEAccountActive;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParent = (KNanYueEAccountMainActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
        } else {
            this.mRoot = inflater.inflate(R.layout.k_fragment_nanyue_eaccount_main, container, false);
        }
        ButterKnife.bind(this, mRoot);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String isNanYueEaccountActive = KBaseApplication.getInstance().getUserInfoStub().getActive_ny();
        if (isNanYueEaccountActive != null) {
            if (isNanYueEaccountActive.equals("1")) {
                btnNanyueEAccountActive.setVisibility(View.GONE);
            } else {
                btnNanyueEAccountActive.setVisibility(View.VISIBLE);
            }
        }
        loadAccountInfo();
    }

    private void loadAccountInfo() {
        DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(mParent);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(mParent, KHttpAdress.ACCOUNT_QUERY, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(),
                new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            KAccountInfoResponseBean responseBean = JSON.parseObject(result.getContent(), KAccountInfoResponseBean.class);
                            String balance = responseBean.getBalance();
                            if (balance == null || balance.equals("")) {
                                nanYueEAccountBalance.setText("--");
                            } else if (balance.equals("0")) {
                                nanYueEAccountBalance.setText("0.00");
                            } else {
                                nanYueEAccountBalance.setText(balance);
                            }
                            // 信用余额
                            String effectiveAmt = responseBean.getEffective_amt();
                            if (effectiveAmt == null || effectiveAmt.equals("")) {
                                txtEffectiveAmt.setText("--");
                            } else if (effectiveAmt.equals("0")) {
                                txtEffectiveAmt.setText("0.00");
                            } else {
                                txtEffectiveAmt.setText(effectiveAmt);
                            }

                            if (responseBean.getAuthorize_amt() != null) {
                                // 信用额度,待用
                            }
                        }
                    }
                });
    }

    // 激活包括开户、设置支付密码、电子账户绑卡3个流程
    @OnClick(value = R.id.nanyueEAccountActive)
    public void eAccountActiveClick(View view) {
        DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(mParent);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(mParent, KHttpAdress.NANYUE_OPEN_EACCOUNT, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(),
                new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            JSONObject contentJSON = JSON.parseObject(result.getContent());
                            if (contentJSON == null) {
                                DialogProvider.alert(mParent, "电子账户开户异常:服务器返回的数据无法解析，请尝试重试");
                                return;
                            } else {
                                String accountStatus = contentJSON.getString("user_flag");
                                if (accountStatus == null || accountStatus.equals("")) {
                                    DialogProvider.alert(mParent, "电子账户开户异常:服务器返回的数据无法解析，请尝试重试");
                                    return;
                                }
                                if (accountStatus.equals("0") || accountStatus.equals("2")) {
                                    // 切换到南粤电子账户管理页来设置电子账户密码
                                    Bundle args = new Bundle();
                                    args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_OP_TYPE, KNanYueEAccountManagerFragment.OP_TYPE_SETPWD);
                                    args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
                                    KNanYueEAccountManagerFragment eAccountManageroFragment = new KNanYueEAccountManagerFragment();
                                    eAccountManageroFragment.setArguments(args);
                                    mParent.changeFragment(eAccountManageroFragment, true);
                                } else if (accountStatus.equals("3")) {
                                    // 跳转到南粤电子账户管理页进行绑卡操作
                                    Bundle args = new Bundle();
                                    args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_OP_TYPE, KNanYueEAccountManagerFragment.OP_TYPE_BIND_CARD);
                                    args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
                                    KNanYueEAccountManagerFragment eAccountManageroFragment = new KNanYueEAccountManagerFragment();
                                    eAccountManageroFragment.setArguments(args);
                                    mParent.changeFragment(eAccountManageroFragment, true);
                                } else if (accountStatus.equals("1") || accountStatus.equals("4")) {
                                    // 南粤电子账户不需要任何设置
                                    DialogProvider.alert(mParent, "温馨提示", "恭喜您成功激活电子账户", "确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogProvider.hideAlertDialog();
                                            mParent.popupToRootFragment();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }


    @OnClick(R.id.nanyueBankCard)
    void onMyBankCardClick(View view) {
        Bundle args = new Bundle();
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_OP_TYPE, KNanYueEAccountManagerFragment.OP_TYPE_MY_BANK_CARD);
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
        KNanYueEAccountManagerFragment eAccountManageroFragment = new KNanYueEAccountManagerFragment();
        eAccountManageroFragment.setArguments(args);
        mParent.changeFragment(eAccountManageroFragment, true);
    }

    @OnClick(R.id.nanyueChangePwd)
    void onChangePwdClick(View view) {
        Bundle args = new Bundle();
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_OP_TYPE, KNanYueEAccountManagerFragment.OP_TYPE_CHANGE_PWD);
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
        KNanYueEAccountManagerFragment eAccountManageroFragment = new KNanYueEAccountManagerFragment();
        eAccountManageroFragment.setArguments(args);
        mParent.changeFragment(eAccountManageroFragment, true);
    }

    @OnClick(R.id.nanyueResetPwd)
    void onResetPwdClick(View view) {
        Bundle args = new Bundle();
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_OP_TYPE, KNanYueEAccountManagerFragment.OP_TYPE_RESET_PWD);
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
        KNanYueEAccountManagerFragment eAccountManageroFragment = new KNanYueEAccountManagerFragment();
        eAccountManageroFragment.setArguments(args);
        mParent.changeFragment(eAccountManageroFragment, true);
    }


    @OnClick(R.id.nanYueEAccountCharge)
    void onEaccoutChargeClick(View view) {
        KNanYueEAccountChargeFragment eAccountChargeFragment = new KNanYueEAccountChargeFragment();
        mParent.changeFragment(eAccountChargeFragment, true);
    }


    @OnClick(R.id.nanyueWithdraw)
    void onEaccoutWithdrawClick(View view) {
        KNanYueEAccountWithdrawFragment eAccountWithdrawFragment = new KNanYueEAccountWithdrawFragment();
        mParent.changeFragment(eAccountWithdrawFragment, true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}