package com.jald.reserve.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KPhoneUpdateRequestBean;
import com.jald.reserve.bean.http.request.KSmsInfoRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.ValueUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.k_activity_phone_update)
public class KPhoneUpdateActivity extends KBaseActivity 
{
	@ViewInject(R.id.phone_number_edit_text)
	private EditText phoneNumberEditText;
	@ViewInject(R.id.smscode_edit_text)
	private EditText smscodeEditText;
	@ViewInject(R.id.pay_pwd_edit_text)
	private EditText payPwdEditText;
	@ViewInject(R.id.get_sms_code_btn)
	private Button getSmsCodeBtn;
	
	private String phoneNumber;
	private String smscode;
	private String payPwd;
	private KUserInfoStub userInfoStub;
	private CountDownTimer countDownTimer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		this.userInfoStub = KBaseApplication.getInstance().getUserInfoStub();
	}
	@Event(R.id.btn_phohe_update_return)
	private void onReturnClick(View view) {
		hideSoftInputKeyBoard();
		DialogProvider.hideProgressBar();
		finish();
	}
	@Event(R.id.get_sms_code_btn)
	private void getSmsCode(View view)
	{
		this.phoneNumber = this.phoneNumberEditText.getText().toString().trim();
		if (!ValueUtil.isMobileNO(this.phoneNumber)) {
			Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if (this.userInfoStub.getTelephone()!=null&&this.userInfoStub.getTelephone().equals(phoneNumber)) 
		{
			Toast.makeText(this, "更改的手机号与当前绑定的手机号一样,不需要更改", Toast.LENGTH_SHORT).show();
			return;
		}
		startTimer();
		KSmsInfoRequestBean bean = new KSmsInfoRequestBean(this.phoneNumber, "01");
		KHttpClient httpTools = KHttpClient.singleInstance();
		httpTools.postData(this, KHttpAdress.SEND_SMS, bean, userInfoStub, new KHttpCallBack() {
			@Override
			public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
				onSendSmsTransactionSuccess();
			}
		});
	}
	@Event(R.id.confirm)
	private void confirm(View view)
	{
		if(checkInput())
		{
			hideSoftInputKeyBoard();
			DialogProvider.showProgressBar(this, new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					KHttpClient.singleInstance().cancel(KPhoneUpdateActivity.this);
				}
			});
			KPhoneUpdateRequestBean bean = new KPhoneUpdateRequestBean();
			bean.setTel(this.phoneNumber);
			bean.setPayment_password(MD5Tools.MD5(this.payPwd));
			bean.setSms_code(this.smscode);
			KHttpClient.singleInstance().postData(KPhoneUpdateActivity.this, KHttpAdress.BIND_TELPHONE, bean, userInfoStub,
					new KHttpCallBack() {
						@Override
						public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
							DialogProvider.hideProgressBar();
							if (isReturnSuccess) 
							{
								
								onPhoneUpdateTransactionSuccess();
							}
						}
					});
		}
	}
	private boolean checkInput()
	{
		this.phoneNumber = this.phoneNumberEditText.getText().toString().trim();
		this.smscode= this.smscodeEditText.getText().toString().trim();
		this.payPwd = this.payPwdEditText.getText().toString().trim();
		if (!ValueUtil.isMobileNO(this.phoneNumber)) {
			Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (this.userInfoStub.getTelephone()!=null&&this.userInfoStub.getTelephone().equals(phoneNumber)) 
		{
			Toast.makeText(this, "更改的手机号与当前绑定的手机号一样,不需要更改", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(smscode.equals(""))
		{
			Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(payPwd.equals(""))
		{
			Toast.makeText(this, "请输入支付密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void hideSoftInputKeyBoard() 
	{
		InputMethodManager imManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	private void onSendSmsTransactionSuccess()
	{
		Toast.makeText(this, "发送成功,请注意查收", Toast.LENGTH_SHORT).show();
	}
	private void onPhoneUpdateTransactionSuccess()
	{
		userInfoStub.setTelephone(this.phoneNumber);
		Toast.makeText(this, "手机号更改成功", Toast.LENGTH_SHORT).show();
		finish();
	}
	private void startTimer() {
		if (countDownTimer == null) {
			countDownTimer = new CountDownTimer(180000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					getSmsCodeBtn.setText(millisUntilFinished / 1000 + "秒");
					getSmsCodeBtn.setClickable(false);
				}

				@Override
				public void onFinish() {
					getSmsCodeBtn.setText("获取验证码");
					getSmsCodeBtn.setClickable(true);
				}
			};
		} else {
			countDownTimer.cancel();
		}
		countDownTimer.start();
	}
	
}
