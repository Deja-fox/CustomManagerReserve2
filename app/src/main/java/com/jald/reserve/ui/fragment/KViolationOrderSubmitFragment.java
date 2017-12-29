package com.jald.reserve.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KVilationPaymentListAdapter;
import com.jald.reserve.bean.http.response.KViolationPaymentQueryResponseBean.ViolationPaymentItem;
import com.jald.reserve.bean.normal.KTrafficFineContextBean;
import com.jald.reserve.consts.KBaseConfig;
import com.jald.reserve.widget.ExtendedListView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class KViolationOrderSubmitFragment extends Fragment {

    public static interface KViolationOrderSubmitFragmentListener {
        public void onPageShow();

        public void onPageHide();

        public void onSubmitSuccess();
    }

    public static final String ARG_KEY_TRAFFIC_FINE_CONTEXT_BEAN = "KTrafficFineContextBean";

    private View mRoot;

    @ViewInject(R.id.car_no)
    private TextView txtCarNo;

    @ViewInject(R.id.mail_type_name)
    private TextView txtMailInfo;

    @ViewInject(R.id.postage)
    private TextView txtPostage;

    @ViewInject(R.id.total_amount)
    private TextView txtTotalAmount;

    @ViewInject(R.id.btn_pay_submit)
    private Button btnSubmit;

    @ViewInject(R.id.payment_listview)
    private ExtendedListView paymentListView;

    private KVilationPaymentListAdapter paymentListAdapter;
    private List<ViolationPaymentItem> paymentListData = new ArrayList<ViolationPaymentItem>();

    private KTrafficFineContextBean contextBean;

    private KViolationOrderSubmitFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRoot = inflater.inflate(R.layout.k_fragment_violation_order_submit, container, false);
        x.view().inject(this, this.mRoot);
        if (getArguments() != null) {
            this.contextBean = (KTrafficFineContextBean) getArguments().getSerializable(ARG_KEY_TRAFFIC_FINE_CONTEXT_BEAN);
        }

        this.txtCarNo.setText(contextBean.getQueryCondition().getShop_sign());
        this.txtMailInfo.setText(contextBean.getPaymentInfoBean().getMail_type_name());
        this.txtPostage.setText(contextBean.getPaymentInfoBean().getPostage());
        this.txtTotalAmount.setText(contextBean.getPaymentInfoBean().getTotal_amount());

        this.paymentListAdapter = new KVilationPaymentListAdapter(getActivity());
        if (KBaseConfig.DEBUG) {
            paymentListData.clear();
            paymentListData.add(new ViolationPaymentItem("001", "鲁A27310", "小型车", "信号路口不按规定行驶", "200", "30", "20", "250"));
            paymentListData.add(new ViolationPaymentItem("002", "鲁A27310", "小型车", "不按规定安装号牌,车辆号牌字迹辨认不清", "300", "30", "20", "350"));
            paymentListAdapter.setPaymentList(paymentListData);
        }
        this.paymentListView.setAdapter(paymentListAdapter);
        return this.mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (listener != null) {
            listener.onPageShow();
        }
    }

    @Event(R.id.btn_pay_submit)
    private void onSubmitClick(View v) {
        if (listener != null) {
            Toast.makeText(getActivity(), "恭喜您,支付成功", Toast.LENGTH_SHORT).show();
            listener.onSubmitSuccess();
        }
    }

    @Override
    public void onDestroyView() {
        if (listener != null) {
            listener.onPageHide();
        }
        super.onDestroyView();
    }

    public KViolationOrderSubmitFragmentListener getListener() {
        return listener;
    }

    public void setListener(KViolationOrderSubmitFragmentListener listener) {
        this.listener = listener;
    }

}
