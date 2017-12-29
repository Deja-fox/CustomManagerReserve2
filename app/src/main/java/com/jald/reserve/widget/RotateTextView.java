package com.jald.reserve.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class RotateTextView extends TextView {
    public RotateTextView(Context context) {
        super(context);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        canvas.rotate(-45f, width / 2.0f, height / 2.0f);
        super.onDraw(canvas);
        canvas.restore();
    }
}
