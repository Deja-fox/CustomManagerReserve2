package com.jald.reserve.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KPayPwdUpdateRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.k_activity_pay_pwd_update)
public class KPayPwdUpdateActivity extends KBaseActivity {

	@ViewInject(R.id.old_password_edittext)
	private EditText edtOldPassword;

	@ViewInject(R.id.new_password_edittext)
	private EditText edtNewPassword;

	@ViewInject(R.id.new_password_again_edittext)
	private EditText edtNewPasswordAgain;

	@ViewInject(R.id.btn_submit)
	private Button btnSubmit;

	private String oldPassword;
	private String newPassword;
	private String newPasswordAgain;

	private KUserInfoStub userInfoStub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		this.userInfoStub = KBaseApplication.getInstance().getUserInfoStub();
	}

	@Event(R.id.btn_pwd_submit)
	private void onSubmitClick(View view) {
		if (checkInputField()) {
			hideSoftInputKeyBoard();
			DialogProvider.showProgressBar(this, new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					KHttpClient.singleInstance().cancel(KPayPwdUpdateActivity.this);
				}
			});
			KPayPwdUpdateRequestBean payPwdUpdateBean = new KPayPwdUpdateRequestBean();
			payPwdUpdateBean.setOld_password(MD5Tools.MD5(this.oldPassword));
			payPwdUpdateBean.setNew_password(MD5Tools.MD5(this.newPassword));

			KHttpClient.singleInstance().postData(KPayPwdUpdateActivity.this, KHttpAdress.PAY_PWD_UPDATE, payPwdUpdateBean, userInfoStub,
					new KHttpCallBack() {
						@Override
						public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
							DialogProvider.hideProgressBar();
							if (isReturnSuccess) {
								onLoginPwdUpdateTransactionSuccess();
							}
						}
					});
		}
	}

	@Event(R.id.btn_pwd_return)
	private void onReturnClick(View view) {
		hideSoftInputKeyBoard();
		DialogProvider.hideProgressBar();
		finish();
	}

	private boolean checkInputField() {
		this.oldPassword = edtOldPassword.getText().toString().trim();
		this.newPassword = edtNewPassword.getText().toString().trim();
		this.newPasswordAgain = edtNewPasswordAgain.getText().toString().trim();
		if (this.oldPassword.equals("")) {
			Toast.makeText(this, "请输入原始密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (this.newPassword.equals("")) {
			Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (this.newPassword.length() < 6) {
			Toast.makeText(this, "新密码过短", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (this.newPasswordAgain.equals("")) {
			Toast.makeText(this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!this.newPasswordAgain.equals(this.newPassword)) {
			Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void hideSoftInputKeyBoard() {
		InputMethodManager imManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void onLoginPwdUpdateTransactionSuccess() {
		Toast.makeText(this, "恭喜您支付密码修改成功", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
