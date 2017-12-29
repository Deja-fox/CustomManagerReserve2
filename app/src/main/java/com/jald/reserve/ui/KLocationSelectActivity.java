package com.jald.reserve.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;

import org.xutils.common.util.LogUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zbw on 2017/2/14.
 * 说明：发送位置
 */

public class KLocationSelectActivity extends KBaseActivity {

    public static final String INTENT_KEY_CUSTOM_INFO = "KeyCustomInfo";
    @Bind(R.id.mapView)
    MapView mapView;

    @Bind(R.id.curLocIndicator)
    ImageView curLocIndicator;

    @Bind(R.id.txtCurLocDesc)
    TextView txtCurLocDesc;

    @Bind(R.id.btnSubmit)
    ImageButton btnSubmit;


    BaiduMap baiduMap;

    LocationClient mLocationClient = null;
    GeoCoder mGeoCoder = GeoCoder.newInstance();


    private String selLatStr;
    private String selLngStr;
    private String address;
    private CustomListResponseBean.KCustomBean customInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_location_select);
        customInfoBean = (CustomListResponseBean.KCustomBean) getIntent().getSerializableExtra(INTENT_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(this, "客户信息传递失败,无法进行位置采集", Toast.LENGTH_SHORT).show();
            return;
        }
        ButterKnife.bind(this);

        initLocation();
        configMap();
    }

    private void configMap() {
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setTrafficEnabled(false);
        MapStatus mMapStatus = new MapStatus.Builder().zoom(15).build();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                getTargetLatlngAndGeoCode();

                mLocationClient.registerLocationListener(new BDLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation location) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        String city = location.getCity() == null ? "" : location.getCity();
                        String district = location.getDistrict() == null ? "" : location.getDistrict();
                        String street = location.getStreet() == null ? "" : location.getStreet();
                        String streetNo = location.getStreetNumber() == null ? "" : location.getStreetNumber();
                        String locDesc = location.getLocationDescribe() == null ? "" : location.getLocationDescribe();
                        String firstPoiName = "";
                        String errorMsg = null;
                        if (location.getLocType() == BDLocation.TypeGpsLocation) {
                            LogUtil.e("GPS定位成功");
                        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                            LogUtil.e("网络定位成功");
                        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                            LogUtil.e("离线定位成功");
                        } else if (location.getLocType() == BDLocation.TypeServerError) {
                            errorMsg = "网络定位失败,服务器出错啦";
                        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                            errorMsg = "定位失败,请检查网络是否畅通";
                        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                            errorMsg = "定位失败,请检查网络是否正常";
                        }
                        List<Poi> list = location.getPoiList();
                        if (list != null && list.size() > 0) {
                            firstPoiName = list.get(0).getName();
                        }
                        if (errorMsg != null) {
                        } else {
                            String resultStr = city + district + street + streetNo;
                            if (firstPoiName != null) {
                                resultStr += firstPoiName;
                            } else {
                                resultStr += locDesc;
                            }
                            setMapTarget(lat, lng);
                            getTargetLatlngAndGeoCode();
                        }
                        mLocationClient.stop();
                    }
                });
                mLocationClient.start();
            }
        });
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                txtCurLocDesc.setVisibility(View.GONE);
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                txtCurLocDesc.setText("位置获取中...");
                txtCurLocDesc.setVisibility(View.VISIBLE);
                getTargetLatlngAndGeoCode();
            }
        });
    }

    private void getTargetLatlngAndGeoCode() {
        LatLng targetLatlng = baiduMap.getMapStatus().target;
        selLatStr = targetLatlng.latitude + "";
        selLngStr = targetLatlng.longitude + "";

        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                LogUtil.e(reverseGeoCodeResult.toString());
                if (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    address = "";
                    txtCurLocDesc.setText("位置获取失败,请检查网络连接");
                } else {
                    String locDesc = reverseGeoCodeResult.getAddress();
                    if (reverseGeoCodeResult.getPoiList() != null && reverseGeoCodeResult.getPoiList().get(0) != null) {
                        PoiInfo poi = reverseGeoCodeResult.getPoiList().get(0);
                        locDesc += poi.name;
                    }
                    address = locDesc;
                    txtCurLocDesc.setText(address);
                }
            }
        });
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(targetLatlng));
    }


    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(true);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    private void setMapTarget(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        MapStatus mMapStatus = new MapStatus.Builder().target(latLng).zoom(15).build();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_location);
        OverlayOptions option = new MarkerOptions().position(latLng).icon(markerIcon).zIndex(9);
        baiduMap.addOverlay(option);
    }

    @OnClick(R.id.btnSubmit)
    void onLatlngSubmitClick(View v) {
        if (selLatStr == null || selLatStr.equals("") || selLngStr == null || selLngStr.equals("")) {
            Toast.makeText(KLocationSelectActivity.this, "暂无法获取您设置的位置信息,请重试", Toast.LENGTH_SHORT).show();
        } else {
            uploadLatlngtoServer(selLatStr, selLngStr, address);
        }
    }

    /**
     * 上传经纬度和具体地址
     *
     * @param lat
     * @param lng
     * @param address
     */
    private void uploadLatlngtoServer(final String lat, final String lng, final String address) {
        DialogProvider.showProgressBar(this);
        JSONObject postJson = new JSONObject();
        postJson.put("supplier_tp_id", KBaseApplication.getInstance().getUserInfoStub().getStpId());//测试用例18766140688
        postJson.put("function", "set_store_map");
        postJson.put("customer_tp_id", customInfoBean.getTp_id());
        postJson.put("latitude", lat);//纬度
        postJson.put("longitude", lng);//经度
        postJson.put("address", address);
        KHttpClient.singleInstance().postData(this, KHttpAdress.COMMON_INFO, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    JSONObject jsonObject1 = JSON.parseObject(result.getContent());
                    JSONObject content = JSON.parseObject(jsonObject1.getString("content"));
                    if (content != null) {
                        if (content.getString("ret_code").equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            Toast.makeText(KLocationSelectActivity.this, content.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(KLocationSelectActivity.this, content.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(KLocationSelectActivity.this, "采购商地址保存失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(KLocationSelectActivity.this, result.getRet_msg(), Toast.LENGTH_SHORT).show();
                }
                DialogProvider.hideProgressBar();
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }


    @Override
    protected void onDestroy() {
        mGeoCoder.destroy();
        super.onDestroy();
    }
}
