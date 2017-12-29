package com.jald.reserve.widget.pullrefresh;

public interface ILoadingLayout {

	public enum State {
		NONE, RESET, PULL_TO_REFRESH, RELEASE_TO_REFRESH, REFRESHING, NO_MORE_DATA,
	}

	public void setState(State state);

	public State getState();

	public int getContentSize();

	public void onPull(float scale);
}
