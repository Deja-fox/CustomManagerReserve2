package com.jald.reserve.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {

	public static View detachFromParent(View view) {
		if (view.getParent() != null) {
			((ViewGroup) view.getParent()).removeView(view);
		}
		return view;
	}

	public static int fitScreenHeight(View view, double percent, int minHeightInDP) {
		Context context = view.getContext();
		int minHinPx = (int) (context.getResources().getDisplayMetrics().density * minHeightInDP);
		int screenHpx = context.getResources().getDisplayMetrics().heightPixels;
		int expectHpx = (int) (screenHpx * percent);
		if (expectHpx <= minHinPx) {
			expectHpx = minHinPx;
		}
		view.getLayoutParams().height = expectHpx;
		return expectHpx;
	}
}
