package com.jald.reserve.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

public class SystemUtil {
	
	public static int getPackageVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getPackageVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

	public static double getPackageVersionNameDouble(Context context) {
		try {
			return Double.parseDouble(getPackageVersionName(context));
		} catch (Exception e) {
			e.printStackTrace();
			return 1.0;
		}
	}


}
