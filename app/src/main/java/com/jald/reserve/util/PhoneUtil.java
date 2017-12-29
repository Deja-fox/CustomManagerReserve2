package com.jald.reserve.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.jald.reserve.KBaseApplication;

public class PhoneUtil {
	private static final String TAG = "PhoneUtil";

	public static int getVersionCode() {
		PackageManager manager = KBaseApplication.getInstance().getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(KBaseApplication.getInstance().getPackageName(), 0);
			int versionCode = info.versionCode;
			Log.i(TAG, "versionCode = " + versionCode);
			return versionCode;
		} catch (NameNotFoundException e) {
		}
		return 1;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static String getVersionName() {
		PackageManager manager = KBaseApplication.getInstance().getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(KBaseApplication.getInstance().getPackageName(), 0);
			Log.i(TAG, "pinfo.versionName = " + info.versionName);
			return info.versionName;
		} catch (NameNotFoundException e) {
		}
		return "1.0";
	}

	public static String getIMEICode() {
		TelephonyManager telephonyManager = (TelephonyManager) KBaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI = telephonyManager.getDeviceId();
		if (StringUtil.isStrEmpty(IMEI)) {
			IMEI = System.getString(KBaseApplication.getInstance().getContentResolver(), System.ANDROID_ID);
		}
		return IMEI;
	}

	public static String getOperator() {
		TelephonyManager telephonyManager = (TelephonyManager) KBaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telephonyManager.getSimOperatorName();
		if (StringUtil.isStrEmpty(operator)) {
			operator = telephonyManager.getNetworkOperatorName();
		}
		return operator;
	}

	public static boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) KBaseApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int getSdkVersion() {
		return Build.VERSION.SDK_INT;
	}

	public static boolean validatePhoneNumber(String number) {
		String mobileRegexp = "([0-9]{3})+-([0-9]{4})+-([0-9]{4})+ ";
		Pattern pattern = Pattern.compile(mobileRegexp);
		Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

	public static int getScreenWidth() {
		Display display = ((WindowManager) KBaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight() {
		Display display = ((WindowManager) KBaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getHeight();
	}

	public static boolean isLocationEnabled() {
		LocationManager manager = (LocationManager) KBaseApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
		if (manager != null) {
			boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean net = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			return gps || net;
		}
		return false;
	}

}
