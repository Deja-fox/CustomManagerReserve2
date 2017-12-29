package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.ui.KNanYueEAccountMainActivity;
import com.jald.reserve.util.ViewUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KNanYueEAccountChargeFragment extends Fragment {

    @Bind(R.id.chargeAmt)
    EditText chargeAmt;
    @Bind(R.id.submit)
    Button submit;
    private KNanYueEAccountMainActivity mParent;

    private View mRoot;


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
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_nanyue_eaccount_charge, container, false);
        ButterKnife.bind(this, mRoot);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @OnClick(R.id.submit)
    void onChargeSubmitClick(View view) {
        String chargeAmt = this.chargeAmt.getText().toString().trim();
        if (chargeAmt.equals("")) {
            Toast.makeText(getActivity(), "请输入充值金额", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle args = new Bundle();
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_OP_TYPE, KNanYueEAccountManagerFragment.OP_TYPE_ACCOUNT_CHARGE);
        args.putSerializable(KNanYueEAccountManagerFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
        args.putString(KNanYueEAccountManagerFragment.KEY_CHARGE_AMT, chargeAmt);
        KNanYueEAccountManagerFragment eAccountManageroFragment = new KNanYueEAccountManagerFragment();
        eAccountManageroFragment.setArguments(args);
        mParent.changeFragment(eAccountManageroFragment, true);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}