package com.jald.reserve.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KViolationRecordQueryRequestBean;
import com.jald.reserve.bean.http.response.KViolationPaymentQueryResponseBean;
import com.jald.reserve.bean.http.response.KViolationPaymentQueryResponseBean.ViolationPaymentItem;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;
import com.jald.reserve.bean.normal.KTrafficPostInfoFrom;
import com.jald.reserve.consts.KBaseConfig;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.CitySelectDialog;
import com.jald.reserve.widget.CitySelectDialog.OnCitySelectListener;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class KViolationPostAddressFragment extends Fragment {

	public static interface KViolationPostAddressFragmentListener {
		public void onPageShow();

		public void onPageHide();

		public void onNextStepClick(KViolationRecordQueryRequestBean queryCondition, ArrayList<KViolationRecordBean> selectedRecordList,
				KTrafficPostInfoFrom postInfo, KViolationPaymentQueryResponseBean paymentInfoBean);
	}

	public static final String ARG_KEY_QUERY_CONDITION = "KeyQueryCondition";
	public static final String ARG_KEY_SELECTED_VIOLATION_RECORD = "KeySelectedViolationRecord";

	private KViolationRecordQueryRequestBean queryCondition;
	private ArrayList<KViolationRecordBean> selectedRecordList = new ArrayList<KViolationRecordBean>();

	private View mRoot;

	@ViewInject(R.id.is_need_penalty_peceipts_container)
	private LinearLayout isNeedPenaltyPeceiptsContainer;
	@ViewInject(R.id.is_need_penalty_peceipts_checkbox)
	private CheckBox isNeedPenaltyPeceiptsCheckBox;

	@ViewInject(R.id.is_need_mail_invoice_container)
	private LinearLayout isNeedMailInvoiceContainer;
	@ViewInject(R.id.is_need_mail_invoice_checkbox)
	private CheckBox isNeedMailInvoiceCheckBox;

	@ViewInject(R.id.sex_spinner)
	private Spinner sexSpinner;
	private ArrayAdapter<String> sexAdapter;
	private String[] sexLabel = new String[] { "男", "女" };
	private String[] sexKey = new String[] { "1", "0" };

	@ViewInject(R.id.btn_city_selected)
	private TextView txtCitySelect;

	private CitySelectDialog citySelectDialog;

	@ViewInject(R.id.btn_next_step)
	private Button btnNextStep;

	private KTrafficPostInfoFrom postInfo;

	private KViolationPostAddressFragmentListener listener;

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (this.mRoot != null && mRoot.getParent() != null) {
			ViewUtil.detachFromParent(this.mRoot);
			return mRoot;
		}
		this.mRoot = inflater.inflate(R.layout.k_fragment_violation_post_address, container, false);
		x.view().inject(this, mRoot);
		if (getArguments() != null) {
			this.queryCondition = (KViolationRecordQueryRequestBean) getArguments().getSerializable(ARG_KEY_QUERY_CONDITION);
			this.selectedRecordList = (ArrayList<KViolationRecordBean>) getArguments().getSerializable(ARG_KEY_SELECTED_VIOLATION_RECORD);
		}
		this.initSexSpinner();
		return this.mRoot;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (listener != null) {
			listener.onPageShow();
		}
	}

	@Override
	public void onDestroyView() {
		if (listener != null) {
			listener.onPageHide();
		}
		super.onDestroyView();
	}

	@Event(R.id.btn_next_step)
	private void onNextStepClick(View v) {
		if (KBaseConfig.DEBUG) {
			// 1先保存邮寄信息
			postInfo = new KTrafficPostInfoFrom();
			postInfo.setCity_name("山东济南");
			postInfo.setCust_name("张廷发");
			postInfo.setGender("1");
			postInfo.setInvoice_title("北京京安利德有限公司");
			postInfo.setIs_need_mail_invoice("1");
			postInfo.setIs_need_penalty_peceipts("1");
			postInfo.setLink_man("赵方面");
			postInfo.setMail_type_id("2");
			postInfo.setMobile_phone("13045452545");
			postInfo.setPostal_code("272100");
			postInfo.setStreet_address("高新区凤凰路");
			postInfo.setTele_phone("13547486125");
			// 2走违章记录的缴费查询接口
			KViolationPaymentQueryResponseBean paymentInfoBean = new KViolationPaymentQueryResponseBean();
			paymentInfoBean.setMail_type_name("EMS");
			paymentInfoBean.setPostage("20");
			paymentInfoBean.setTotal_amount("200");
			paymentInfoBean.setViolations_mlatefee("10");
			List<ViolationPaymentItem> list = new ArrayList<ViolationPaymentItem>();
			ViolationPaymentItem payItem1 = new ViolationPaymentItem("001", "鲁A27310", "小车", "违规停车", "200", "10", "15", "225");
			ViolationPaymentItem payItem2 = new ViolationPaymentItem("001", "鲁A27310", "小车", "违规停车", "200", "10", "15", "225");
			list.add(payItem1);
			list.add(payItem2);
			paymentInfoBean.setList(list);
			if (listener != null) {
				listener.onNextStepClick(queryCondition, selectedRecordList, postInfo, paymentInfoBean);
			}
		}
	}

	@Event(R.id.btn_city_selected)
	private void onCitySelectClick(View v) {
		if (citySelectDialog != null) {
			citySelectDialog.dismiss();
		}
		citySelectDialog = new CitySelectDialog(getActivity());
		citySelectDialog.setOnCitySelectListener(new OnCitySelectListener() {
			@Override
			public void onCitySelected(String p, String c, String d) {
				txtCitySelect.setText(p + c + d);
			}
		});
		citySelectDialog.show();
	}

	@Event(R.id.is_need_penalty_peceipts_container)
	private void OnNeedPeanltyPeceiptContainerClick(View v) {
		isNeedPenaltyPeceiptsCheckBox.setChecked(!isNeedPenaltyPeceiptsCheckBox.isChecked());
	}

	@Event(R.id.is_need_mail_invoice_container)
	private void OnNeedMailInvoiceContainerClick(View v) {
		isNeedMailInvoiceCheckBox.setChecked(!isNeedMailInvoiceCheckBox.isChecked());
	}

	private void initSexSpinner() {
		sexAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_choose_sex, sexLabel);
		sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexSpinner.setAdapter(sexAdapter);
	}

	public KViolationPostAddressFragmentListener getListener() {
		return listener;
	}

	public void setListener(KViolationPostAddressFragmentListener listener) {
		this.listener = listener;
	}

}
