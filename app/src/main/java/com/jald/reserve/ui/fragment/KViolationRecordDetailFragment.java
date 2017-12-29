package com.jald.reserve.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class KViolationRecordDetailFragment extends Fragment {

	public interface OnRequestChangeTitleListener {
		public void requestChangeTitle(String title);
	}

	public static final String ARG_KEY_VIOLATION_RECORD_BEAN = "KeyViolationRecordBean";

	private View mRoot;
	@ViewInject(R.id.temp_id)
	private TextView txtTempId;
	@ViewInject(R.id.regulation_name)
	private TextView txtRegulationName;
	@ViewInject(R.id.fine_amount)
	private TextView txtFineAmount;
	@ViewInject(R.id.pay_charge)
	private TextView txtPayCharge;
	@ViewInject(R.id.special_charge)
	private TextView txtSpecialCharge;
	@ViewInject(R.id.porint)
	private TextView txtPoint;
	@ViewInject(R.id.violation_sn)
	private TextView txtViolationSN;
	@ViewInject(R.id.violation_time)
	private TextView txtViolationTime;
	@ViewInject(R.id.violation_road)
	private TextView txtViolationRoad;
	@ViewInject(R.id.needdays)
	private TextView txtNeeddays;
	@ViewInject(R.id.authority)
	private TextView txtAuthority;
	@ViewInject(R.id.dealaddress)
	private TextView txtDealaddress;
	@ViewInject(R.id.is_on_site_single)
	private TextView txtIsOnSiteSingle;
	@ViewInject(R.id.errror_msg)
	private TextView txtErrrorMsg;

	private KViolationRecordBean violationRecord;

	private OnRequestChangeTitleListener onRequestChangeTitleListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.violationRecord = (KViolationRecordBean) getArguments().getSerializable(ARG_KEY_VIOLATION_RECORD_BEAN);
		this.mRoot = inflater.inflate(R.layout.k_fragment_violation_record_detail, container, false);
		x.view().inject(this, this.mRoot);
		this.fillFormData();
		return this.mRoot;
	}

	private void fillFormData() {
		this.txtTempId.setText(violationRecord.getTemp_id());
		this.txtAuthority.setText(violationRecord.getAuthority());
		this.txtDealaddress.setText(violationRecord.getDealaddress());
		this.txtErrrorMsg.setText(violationRecord.getErrror_msg());
		this.txtFineAmount.setText(violationRecord.getFine_amount());
		String isOnSiteSingle = violationRecord.getIs_on_site_single();
		if (isOnSiteSingle.equals("0")) {
			this.txtIsOnSiteSingle.setText("否");
		} else {
			this.txtIsOnSiteSingle.setText("是");
		}
		this.txtNeeddays.setText(violationRecord.getNeeddays());
		this.txtPayCharge.setText(violationRecord.getPay_charge());
		this.txtPoint.setText(violationRecord.getPorint());
		this.txtRegulationName.setText(violationRecord.getRegulation_name());
		this.txtSpecialCharge.setText(violationRecord.getSpecial_charge());
		this.txtViolationRoad.setText(violationRecord.getViolation_road());
		this.txtViolationSN.setText(violationRecord.getViolation_sn());
		this.txtViolationTime.setText(violationRecord.getViolation_time());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (onRequestChangeTitleListener != null) {
			onRequestChangeTitleListener.requestChangeTitle("违章详情");
		}
	}

	public void setOnRequestChangeTitleListener(OnRequestChangeTitleListener onRequestChangeTitleListener) {
		this.onRequestChangeTitleListener = onRequestChangeTitleListener;
	}
}
