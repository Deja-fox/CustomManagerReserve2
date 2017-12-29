package com.jald.reserve.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.jald.reserve.R;

public class SelfProgressDialog {

	private Context mContext;
	private Dialog dialog;

	private TextView mMsgView;

	public SelfProgressDialog(Context context) {
		this(context, null);
	}

	public SelfProgressDialog(Context context, final OnCancelListener cancelListener) {
		this.mContext = context;
		dialog = new Dialog(context, R.style.Theme_SelfDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layout = inflater.inflate(R.layout.k_widget_slef_progress_dialog, null);
		mMsgView = (TextView) layout.findViewById(R.id.text_right);
		dialog.setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (cancelListener != null) {
					cancelListener.onCancel(dialog);
				}
			}
		});
	}

	public void setMessage(String message) {
		mMsgView.setText(message);
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

}
