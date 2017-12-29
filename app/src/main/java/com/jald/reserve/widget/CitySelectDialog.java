package com.jald.reserve.widget;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.jald.reserve.R;
import com.jald.reserve.bean.normal.CityModel;
import com.jald.reserve.bean.normal.DistrictModel;
import com.jald.reserve.bean.normal.ProvinceModel;
import com.jald.reserve.widget.wheelview.OnWheelChangedListener;
import com.jald.reserve.widget.wheelview.WheelView;
import com.jald.reserve.widget.wheelview.adapter.ArrayWheelAdapter;
import com.jald.reserve.xml.XmlParserHandler;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class CitySelectDialog implements OnWheelChangedListener {

	public static interface OnCitySelectListener {
		public void onCitySelected(String p, String c, String d);
	}

	private Context mContext;
	private Dialog dialog;

	private View mRoot;
	@ViewInject(R.id.id_province)
	private WheelView mViewProvince;
	@ViewInject(R.id.id_city)
	private WheelView mViewCity;
	@ViewInject(R.id.id_district)
	private WheelView mViewDistrict;

	@ViewInject(R.id.btn_ok)
	private Button btnOk;

	protected String[] mProvinceDatas;
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	protected String mCurrentProviceName;
	protected String mCurrentCityName;
	protected String mCurrentDistrictName = "";
	protected String mCurrentZipCode = "";

	private OnCitySelectListener onCitySelectListener;

	@SuppressLint("InflateParams")
	public CitySelectDialog(Context context) {
		this.mContext = context;
		dialog = new Dialog(context, R.style.Theme_SelfDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mRoot = inflater.inflate(R.layout.k_dialog_city_select, null);
		dialog.setContentView(mRoot, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
		dialog.getWindow().getAttributes().width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8);

		x.view().inject(this, mRoot);
		setUpListener();
		setUpData();
	}

	@Event(R.id.btn_ok)
	private void onOkClick(View v) {
		if (onCitySelectListener != null) {
			onCitySelectListener.onCitySelected(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName);
		}
		this.dismiss();
	}

	private void setUpListener() {
		mViewProvince.addChangingListener(this);
		mViewCity.addChangingListener(this);
		mViewDistrict.addChangingListener(this);
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);
		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
		mViewDistrict.setCurrentItem(0);
	}

	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = mContext.getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			provinceList = handler.getDataList();
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j).getDistrictList();
					String[] distrinctNameArray = new String[districtList.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
					for (int k = 0; k < districtList.size(); k++) {
						DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
						mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void show() {
		if (!dialog.isShowing()) {
			try {
				dialog.show();
			} catch (Exception e) {
			}
		}
	}

	public void dismiss() {
		try {
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	public void cancel() {
		try {
			dialog.cancel();
		} catch (Exception e) {
		}
	}

	public OnCitySelectListener getOnCitySelectListener() {
		return onCitySelectListener;
	}

	public void setOnCitySelectListener(OnCitySelectListener onCitySelectListener) {
		this.onCitySelectListener = onCitySelectListener;
	}

}
