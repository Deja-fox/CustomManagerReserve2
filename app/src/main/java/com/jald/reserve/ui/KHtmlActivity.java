package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KHtmlFragment;

public class KHtmlActivity extends KBaseActivity {

	public static String INTENT_KEY_TITLE = "KeyTitle";
	public static String INTENT_KEY_CONTENT_URL = "KeyContentUrl";

	private ImageView btnUpToHome;
	private TextView txtTitle;

	private KHtmlFragment htmlFragment;
	private String url;
	String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.k_activity_html);
		this.title = getIntent().getStringExtra(INTENT_KEY_TITLE);
		this.url = getIntent().getStringExtra(INTENT_KEY_CONTENT_URL);
		this.txtTitle = (TextView) findViewById(R.id.title);
		if (this.title != null) {
			this.txtTitle.setText(title);
		}
		this.btnUpToHome = (ImageView) findViewById(R.id.up_to_home);
		this.btnUpToHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		htmlFragment = new KHtmlFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putString(KHtmlFragment.ARG_KEY_URL, url);
		htmlFragment.setArguments(args);
		transaction.replace(R.id.fragment_container, htmlFragment);
		transaction.commitAllowingStateLoss();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (htmlFragment.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
