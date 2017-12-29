package com.jald.reserve.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.ProgressWebView;

public class KHtmlFragment extends Fragment {

	public static final String ARG_KEY_URL = "ArgKeyUrl";

	private View mRoot;
	private ProgressWebView mWebView;
	private String url = "about://blank";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (this.mRoot != null) {
			ViewUtil.detachFromParent(this.mRoot);
			return this.mRoot;
		}
		this.mRoot = inflater.inflate(R.layout.k_fragment_html5, container, false);
		this.mWebView = (ProgressWebView) mRoot.findViewById(R.id.webview);
		this.url = getArguments().getString(ARG_KEY_URL);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(getActivity(), description, Toast.LENGTH_LONG).show();
			}
		});
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		loadUrl();
		return this.mRoot;
	}

	private void loadUrl() {
		mWebView.loadUrl(url);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mWebView != null && keyCode == KeyEvent.KEYCODE_BACK) {
			WebBackForwardList currentList = mWebView.copyBackForwardList();
			WebHistoryItem item = currentList.getItemAtIndex(0);
			if (item != null) {
				String firstUrl = item.getUrl();
				String currentUrl = mWebView.getUrl();
				if (currentUrl.equals(firstUrl)) {
					return false;
				} else {
					mWebView.goBack();
					return true;
				}
			}
		}
		return false;
	}
}
