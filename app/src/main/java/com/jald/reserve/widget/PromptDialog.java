package com.jald.reserve.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jald.reserve.R;

public class PromptDialog {

    public static interface OnOkClickListener {
        public void onOkClicked(String number);
    }

    private Dialog dialog;
    private Button okButton;
    private Button cancelButton;
    private EditText edtNumber;

    public PromptDialog(Context context, String okText, final OnOkClickListener okListener, String cancelText, View.OnClickListener cancelListener) {
        dialog = new Dialog(context, R.style.Theme_SelfDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.k_prompt_dialog, null);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.75);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(layout, layoutParams);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        edtNumber = (EditText) layout.findViewById(R.id.number);
        okButton = (Button) layout.findViewById(R.id.btn_rigth);
        okButton.setText(okText);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onOkClicked(edtNumber.getText().toString().trim());
                }
            }
        });
        cancelButton = (Button) layout.findViewById(R.id.btn_left);
        cancelButton.setText(cancelText);
        cancelButton.setOnClickListener(cancelListener);
    }


    public void show(String number) {
        edtNumber.getText().clear();
        edtNumber.getText().append(number);
        edtNumber.setSelection(0, number.length());
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) edtNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edtNumber, 0);
            }
        }, 200);
    }

    public void dismiss() {
        dialog.dismiss();
    }

}