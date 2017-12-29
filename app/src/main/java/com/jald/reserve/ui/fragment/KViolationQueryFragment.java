package com.jald.reserve.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KCarTypeSpinnerAdapter;
import com.jald.reserve.bean.http.request.KViolationRecordQueryRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;
import com.jald.reserve.bean.normal.KCarTypeBean;
import com.jald.reserve.consts.KBaseConfig;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class KViolationQueryFragment extends Fragment {

	public interface KViolationQueryFragmentListener {
		public void onViolationQueryReturn(KViolationRecordQueryRequestBean queryCondition, KViolationRecordQueryResponseBean queryResponse);
	}

	public interface OnRequestChangeTitleListener {
		public void requestChangeTitle(String title);
	}

	private View mRoot;
	@ViewInject(R.id.car_type)
	private Spinner spnCarType;
	@ViewInject(R.id.car_palate)
	private EditText edtCarPalate;
	@ViewInject(R.id.owner_name)
	private EditText edtOwnerName;
	@ViewInject(R.id.engine_no)
	private EditText edtEngineNo;
	@ViewInject(R.id.voiture_no)
	private EditText edtVoitureNo;
	@ViewInject(R.id.btn_query_submit)
	private Button btnQuerySubmit;

	private KViolationQueryFragmentListener listener;
	private OnRequestChangeTitleListener onRequestChangeTitleListener;

	private KCarTypeSpinnerAdapter carTypeAdapter;
	private List<KCarTypeBean> carTypeList;

	private KViolationRecordQueryRequestBean querySubmitFormData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mRoot = inflater.inflate(R.layout.k_fragment_violation_query, container, false);
		x.view().inject(this, this.mRoot);
		this.spnCarType.setPrompt("请选择车辆类型");
		this.carTypeAdapter = new KCarTypeSpinnerAdapter(getActivity());
		this.carTypeList = buildCarTypeList();
		this.carTypeAdapter.setCarTypeList(carTypeList);
		this.spnCarType.setAdapter(carTypeAdapter);
		return this.mRoot;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (onRequestChangeTitleListener != null) {
			onRequestChangeTitleListener.requestChangeTitle("违章查询");
		}
	}

	@Event(R.id.btn_query_submit)
	private void onQuerySubmitClickListener(View v) {
		if (KBaseConfig.DEBUG) {
			if (checkInput()) {
				KViolationRecordQueryResponseBean resBean = new KViolationRecordQueryResponseBean();
				resBean.setTotal("10");
				ArrayList<KViolationRecordBean> list = new ArrayList<KViolationRecordBean>();
				for (int i = 0; i < 10; ++i) {
					KViolationRecordBean bean1 = new KViolationRecordBean();
					bean1.setAuthority("");
					bean1.setCity("济南");
					bean1.setCity_area_id("01");
					bean1.setCity_id("02");
					bean1.setDeal_id("");
					bean1.setDealaddress("高新区");
					bean1.setErrror_msg("无");
					bean1.setFine_amount("200");
					bean1.setIs_on_site_single("");
					bean1.setNeeddays("3");
					bean1.setPay_charge("10");
					int point = new Random().nextInt(3);
					bean1.setPorint(point + "");
					bean1.setProvince_id("03");
					bean1.setReference("");
					bean1.setRegulation_id("29");
					bean1.setRegulation_name("不按规定安装号牌,车辆号牌字迹辨认不清");
					bean1.setRegulation_sn("");
					bean1.setSpecial_charge("9");
					bean1.setTemp_id("09");
					bean1.setViolation_road("华能路");
					bean1.setViolation_sn("22");
					bean1.setViolation_time("2015/05/05");
					list.add(bean1);
				}
				resBean.setList(list);
				this.querySubmitFormData = new KViolationRecordQueryRequestBean();
				querySubmitFormData.setShop_sign("鲁B27310");
				if (listener != null) {
					listener.onViolationQueryReturn(querySubmitFormData, resBean);
				}
			}
			return;
		}
		if (checkInput()) {
			DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					KHttpClient.singleInstance().cancel(getActivity());
				}
			});
			KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.VIOLATION_QUERY, querySubmitFormData,
					KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {

						@Override
						public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
							DialogProvider.hideProgressBar();
							if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
								KViolationRecordQueryResponseBean resBean = JSON.parseObject(result.getContent(),
										KViolationRecordQueryResponseBean.class);
								if (listener != null) {
									listener.onViolationQueryReturn(querySubmitFormData, resBean);
								}
							}
						}

					});
		}
	}

	private boolean checkInput() {
		this.querySubmitFormData = new KViolationRecordQueryRequestBean();
		String carPalate = this.edtCarPalate.getText().toString().trim();
		if (carPalate == null || carPalate.equals("")) {
			Toast.makeText(getActivity(), "请输入车牌号", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{5}$");
			Matcher matcher = pattern.matcher(carPalate);
			if (!matcher.matches()) {
				Toast.makeText(getActivity(), "请输入正确的车牌号", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		this.querySubmitFormData.setShop_sign(carPalate);
		int carTypePos = this.spnCarType.getSelectedItemPosition();
		if (carTypePos == 0) {
			this.querySubmitFormData.setCar_type("");
		} else {
			this.querySubmitFormData.setCar_type(this.carTypeList.get(carTypePos).getTypeId());
		}
		String ownerName = this.edtOwnerName.getText().toString().trim();
		this.querySubmitFormData.setOwner_car(ownerName);
		String enginNo = this.edtEngineNo.getText().toString().trim();
		this.querySubmitFormData.setEngine_no(enginNo);
		String voitureNo = this.edtVoitureNo.getText().toString().trim();
		this.querySubmitFormData.setVoiture_no(voitureNo);
		return true;
	}

	private List<KCarTypeBean> buildCarTypeList() {
		List<KCarTypeBean> list = new ArrayList<KCarTypeBean>();
		list.add(new KCarTypeBean(KCarTypeBean.EMPTY_TYPE, "请选择车辆类型"));
		list.add(new KCarTypeBean("01", "小型车"));
		list.add(new KCarTypeBean("03", "大型车"));
		list.add(new KCarTypeBean("04", "外籍汽车"));
		list.add(new KCarTypeBean("05", "两/三轮摩托车"));
		list.add(new KCarTypeBean("06", "境外摩托车"));
		list.add(new KCarTypeBean("07", "外籍摩托车"));
		list.add(new KCarTypeBean("08", "挂车"));
		list.add(new KCarTypeBean("09", "香港入出境车"));
		list.add(new KCarTypeBean("10", "澳门入处境车"));
		return list;
	}

	public KViolationQueryFragmentListener getListener() {
		return listener;
	}

	public void setListener(KViolationQueryFragmentListener listener) {
		this.listener = listener;
	}

	public void setOnRequestChangeTitleListener(OnRequestChangeTitleListener onRequestChangeTitleListener) {
		this.onRequestChangeTitleListener = onRequestChangeTitleListener;
	}

}
