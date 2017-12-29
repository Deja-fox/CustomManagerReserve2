package com.jald.reserve.widget.pullrefresh;

import android.view.View;

import com.jald.reserve.widget.pullrefresh.PullToRefreshBase.OnRefreshListener;

public interface IPullToRefresh<T extends View> {

	public void setPullRefreshEnabled(boolean pullRefreshEnabled);

	public void setPullLoadEnabled(boolean pullLoadEnabled);

	public void setScrollLoadEnabled(boolean scrollLoadEnabled);

	public boolean isPullRefreshEnabled();

	public boolean isPullLoadEnabled();

	public boolean isScrollLoadEnabled();

	public void setOnRefreshListener(OnRefreshListener<T> refreshListener);

	public void onPullDownRefreshComplete();

	public void onPullUpRefreshComplete();

	public T getRefreshableView();

	public LoadingLayout getHeaderLoadingLayout();

	public LoadingLayout getFooterLoadingLayout();

	public void setLastUpdatedLabel(CharSequence label);
}
