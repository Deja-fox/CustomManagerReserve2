package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KInfoCompleteFragment;
import com.jald.reserve.ui.fragment.KInfoCompleteFragment.OnInfoCompleteFinishListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.k_activity_info_complete)
public class KInfoCompleteActivity extends KBaseActivity implements OnInfoCompleteFinishListener {

	private KInfoCompleteFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		this.fragment = new KInfoCompleteFragment();
		this.fragment.setOnInfoCompleteFinishListener(this);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putSerializable(KInfoCompleteFragment.KEY_ARGU_USER_INFO_STUB, KBaseApplication.getInstance().getUserInfoStub());
		this.fragment.setArguments(args);
		fragmentTransaction.replace(R.id.contianer, fragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	public void onInfoCompleteSubmitSuccess() {
		Toast.makeText(this, "信息提交成功", Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

}
