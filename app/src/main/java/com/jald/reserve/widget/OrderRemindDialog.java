package com.jald.reserve.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jald.reserve.R;

public class OrderRemindDialog {


    private Dialog dialog;
    private Button gotoOrderButton;
    private Button cancelButton;
    private TextView beginOrderTime;
    private TextView endOrderTime;

    public OrderRemindDialog(Context context, String gotoOrderText, View.OnClickListener goToOrderListener, String cancelText, View.OnClickListener cancelListener) {
        dialog = new Dialog(context, R.style.Theme_SelfDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.k_order_remind_dialog, null);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(layout, layoutParams);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        beginOrderTime = (TextView) layout.findViewById(R.id.beginOrderTime);
        endOrderTime = (TextView) layout.findViewById(R.id.endOrderTime);

        gotoOrderButton = (Button) layout.findViewById(R.id.btn_rigth);
        gotoOrderButton.setText(gotoOrderText);
        gotoOrderButton.setOnClickListener(goToOrderListener);
        cancelButton = (Button) layout.findViewById(R.id.btn_left);
        cancelButton.setText(cancelText);
        cancelButton.setOnClickListener(cancelListener);
    }

    public void setBeginOrderTime(String beginOrderTime) {
        if (beginOrderTime != null && !beginOrderTime.equals(""))
            this.beginOrderTime.setText(beginOrderTime);
    }

    public void setEndOrderTime(String endOrderTime) {
        if (endOrderTime != null && !endOrderTime.equals(""))
            this.endOrderTime.setText(endOrderTime);
    }

    public void setGotoOrderButtonVisibility(int visibility) {
        this.gotoOrderButton.setVisibility(visibility);
    }

    public void setCancelButtonText(String text) {
        cancelButton.setText(text);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}