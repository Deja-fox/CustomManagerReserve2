package com.jald.reserve.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.widget.OrderRemindDialog;
import com.jald.reserve.widget.SelfProgressDialog;

public class DialogProvider {

    private static AlertDialog mDialog;
    private static SelfProgressDialog mProgressDialog;
    private static OrderRemindDialog mOrderRemindDialog;


    //-----------------------------------进度对话框部分-----------------------------------------------------------------------

    public static void showProgressBar(final Context context) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new SelfProgressDialog(context);
        mProgressDialog.show();
    }

    public static void showProgressBar(final Context context, OnCancelListener cancelListener) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new SelfProgressDialog(context, cancelListener);
        mProgressDialog.show();
    }

    public static void hideProgressBar() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    //-----------------------------------警告对话框部分-----------------------------------------------------------------------


    public static void hideAlertDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }


    public static void alert(Context context, String msg) {
        hideAlertDialog();
        mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCancelable(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.k_alert_dialog_1btn);
        Button btn = (Button) window.findViewById(R.id.btn_rigth);
        btn.setText("确定");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView txtTitle = (TextView) window.findViewById(R.id.alert_title);
        txtTitle.setText("温馨提示");
        TextView txtAlertMsg = (TextView) window.findViewById(R.id.alert_text);
        txtAlertMsg.setText(msg);
    }

    public static void alert(Context context, String title, String msg, String btnText) {
        hideAlertDialog();
        mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCancelable(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.k_alert_dialog_1btn);
        Button btn = (Button) window.findViewById(R.id.btn_rigth);
        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView titleTextView = (TextView) window.findViewById(R.id.alert_title);
        if (null != title) {
            titleTextView.setText(title);
        }
        TextView alertMsgTextView = (TextView) window.findViewById(R.id.alert_text);
        alertMsgTextView.setText(Html.fromHtml(msg));
    }

    public static void alert(Context context, String title, String msg, String btnText, View.OnClickListener btnListener) {
        hideAlertDialog();
        mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCancelable(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.k_alert_dialog_1btn);
        Button btn = (Button) window.findViewById(R.id.btn_rigth);
        btn.setText(btnText);
        if (btnListener != null) {
            btn.setOnClickListener(btnListener);
        }
        TextView titleTextView = (TextView) window.findViewById(R.id.alert_title);
        if (null != title) {
            titleTextView.setText(title);
        }
        TextView alertMsgTextView = (TextView) window.findViewById(R.id.alert_text);
        alertMsgTextView.setText(msg);
    }

    public static void alert(Context context, String title, String msg, String rightBtnText, View.OnClickListener rightBtnListener, String leftBtnText, View.OnClickListener leftBtnListener) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCancelable(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.k_alert_dialog);
        Button rightBtn = (Button) window.findViewById(R.id.btn_rigth);
        rightBtn.setText(rightBtnText);
        rightBtn.setOnClickListener(rightBtnListener);
        Button leftBtn = (Button) window.findViewById(R.id.btn_left);
        leftBtn.setText(leftBtnText);
        leftBtn.setOnClickListener(leftBtnListener);
        TextView titleTextView = (TextView) window.findViewById(R.id.alert_title);
        if (null != title) {
            titleTextView.setText(title);
        }
        TextView alertMsgTextView = (TextView) window.findViewById(R.id.alert_text);
        alertMsgTextView.setText(msg);
    }


    //-----------------------------------订货提醒对话框部分-----------------------------------------------------------------------


    public static OrderRemindDialog showOrderRemindDialog(Context context, View.OnClickListener goToOrderListener, View.OnClickListener cancelListener) {
        if (mOrderRemindDialog != null) {
            mOrderRemindDialog.dismiss();
        }
        mOrderRemindDialog = new OrderRemindDialog(context, "去订货", goToOrderListener, "取消", cancelListener);
        mOrderRemindDialog.show();
        return mOrderRemindDialog;
    }

    public static void hideOrderRemindDialog() {
        if (mOrderRemindDialog != null) {
            mOrderRemindDialog.dismiss();
        }
    }

}
