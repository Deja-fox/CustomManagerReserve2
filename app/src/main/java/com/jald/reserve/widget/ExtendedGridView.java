package com.jald.reserve.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ExtendedGridView extends GridView {

	public ExtendedGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExtendedGridView(Context context) {
		super(context);
	}

	public ExtendedGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
