package com.jald.reserve.widget.pullrefresh;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jald.reserve.R;

public class RefreshableListView extends ListView implements OnScrollListener {

	private int mLastDownRawY;
	private int mBottomPosition;

	private final int STATE_PULL_TO_REFRESH = 0;
	private final int STATE_RELEASE_TO_REFRESH = 1;
	private final int STATE_REFRESHING = 2;
	private final int STATE_IDLE = 3;
	private final int STATE_LOADING = 4;

	private final int FINGER_MOVE_DIVIDE_REAL_MOVE_DISTANCE_RATIO = 3;

	private LayoutInflater inflater;
	private LinearLayout headerContainerView;
	private TextView headerStateTipsTextview;
	private TextView headerLastUpdatedTimeTextView;
	private ImageView headerArrowImageView;
	private ProgressBar headerRefreshIndicatorProgressBar;
	private RotateAnimation headerArrowAnimation;
	private RotateAnimation headerArrowReverseAnimation;
	private boolean isRecoredTouchDown;
	private int headContentHeight;
	private int startDownY;
	private int firstItemIndexInAdapter;
	private int currentState;

	private boolean isBackFromRelaseToPullRefresh;
	public boolean shouldOpenHeaderViewWhenTouch = true;
	public boolean canRefreashWhenReleaseFinger = false;

	public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RefreshableListView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mBottomPosition = 0;
		setCacheColorHint(0);
		inflater = LayoutInflater.from(context);
		headerContainerView = (LinearLayout) inflater.inflate(R.layout.pull_to_refresh_header_v1, this, false);
		headerArrowImageView = (ImageView) headerContainerView.findViewById(R.id.pull_to_refresh_image);
		headerArrowImageView.setMinimumWidth(70);
		headerArrowImageView.setMinimumHeight(50);
		headerRefreshIndicatorProgressBar = (ProgressBar) headerContainerView.findViewById(R.id.head_progressBar);
		headerStateTipsTextview = (TextView) headerContainerView.findViewById(R.id.pull_to_refresh_text);
		headerLastUpdatedTimeTextView = (TextView) headerContainerView.findViewById(R.id.pull_to_refresh_updated_at);
		measureView(headerContainerView);
		headContentHeight = headerContainerView.getMeasuredHeight();
		headerContainerView.setPadding(0, -1 * headContentHeight, 0, 0);
		headerContainerView.invalidate();
		addHeaderView(headerContainerView, null, false);
		setOnScrollListener(this);

		headerArrowAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		headerArrowAnimation.setInterpolator(new LinearInterpolator());
		headerArrowAnimation.setDuration(250);
		headerArrowAnimation.setFillAfter(true);
		headerArrowReverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		headerArrowReverseAnimation.setInterpolator(new LinearInterpolator());
		headerArrowReverseAnimation.setDuration(200);
		headerArrowReverseAnimation.setFillAfter(true);

		currentState = STATE_IDLE;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int y = (int) ev.getRawY();
		cancelLongPress();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			if (firstItemIndexInAdapter == 0 && !isRecoredTouchDown) {
				isRecoredTouchDown = true;
				startDownY = (int) ev.getY();
			}
			mLastDownRawY = y;
			final boolean isHandled = mOnGestureListener.onMotionDown(ev);
			if (isHandled) {
				mLastDownRawY = y;
				return isHandled;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int touchMoveY = (int) ev.getY();
			if (shouldOpenHeaderViewWhenTouch) {
				if (!isRecoredTouchDown && firstItemIndexInAdapter == 0) {
					isRecoredTouchDown = true;
					startDownY = touchMoveY;
				}
				if (currentState != STATE_REFRESHING && currentState != STATE_LOADING && isRecoredTouchDown) {
					if (currentState == STATE_RELEASE_TO_REFRESH) {
						setSelection(0);
						if (((touchMoveY - startDownY) / FINGER_MOVE_DIVIDE_REAL_MOVE_DISTANCE_RATIO < headContentHeight)
								&& (touchMoveY - startDownY) > 0) {
							currentState = STATE_PULL_TO_REFRESH;
							applyCurrentStateToHeaderView();
						} else if (touchMoveY - startDownY <= 0) {
							currentState = STATE_IDLE;
							applyCurrentStateToHeaderView();
						}
					}
					if (currentState == STATE_PULL_TO_REFRESH) {
						setSelection(0);
						if ((touchMoveY - startDownY) / FINGER_MOVE_DIVIDE_REAL_MOVE_DISTANCE_RATIO >= headContentHeight) {
							currentState = STATE_RELEASE_TO_REFRESH;
							isBackFromRelaseToPullRefresh = true;
							applyCurrentStateToHeaderView();
						} else if (touchMoveY - startDownY <= 0) {
							currentState = STATE_IDLE;
							applyCurrentStateToHeaderView();
						}
					}
					if (currentState == STATE_IDLE) {
						if (touchMoveY - startDownY > 0) {
							currentState = STATE_PULL_TO_REFRESH;
							applyCurrentStateToHeaderView();
						}
					}
					if (currentState == STATE_PULL_TO_REFRESH) {
						headerContainerView.setPadding(0, -1 * headContentHeight + (touchMoveY - startDownY)
								/ FINGER_MOVE_DIVIDE_REAL_MOVE_DISTANCE_RATIO, 0, 0);
					}
					if (currentState == STATE_RELEASE_TO_REFRESH) {
						headerContainerView.setPadding(0,
								(touchMoveY - startDownY) / FINGER_MOVE_DIVIDE_REAL_MOVE_DISTANCE_RATIO - headContentHeight, 0, 0);
					}
				}
			}
			final int childCount = getChildCount();
			if (childCount == 0)
				return super.onTouchEvent(ev);
			final int itemCount = getAdapter().getCount() - mBottomPosition;
			final int deltaY = y - mLastDownRawY;
			final int lastBottom = getChildAt(childCount - 1).getBottom();
			final int end = getHeight() - getPaddingBottom();
			final int firstVisiblePosition = getFirstVisiblePosition();

			final boolean isHandleMotionMove = mOnGestureListener.onMotionMove(ev, deltaY);
			if (isHandleMotionMove) {
				mLastDownRawY = y;
				return true;
			}
			if (firstVisiblePosition + childCount >= itemCount && lastBottom <= end && deltaY < 0) {
				final boolean isHandleOnListViewBottomAndPullDown;
				isHandleOnListViewBottomAndPullDown = mOnGestureListener.onListViewBottomAndPullUp(deltaY);
				if (isHandleOnListViewBottomAndPullDown) {
					mLastDownRawY = y;
					return true;
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (currentState != STATE_REFRESHING && currentState != STATE_LOADING) {
				if (currentState == STATE_IDLE) {
				}
				if (currentState == STATE_PULL_TO_REFRESH) {
					currentState = STATE_IDLE;
					applyCurrentStateToHeaderView();
				}
				if (currentState == STATE_RELEASE_TO_REFRESH) {
					currentState = STATE_REFRESHING;
					applyCurrentStateToHeaderView();
					canRefreashWhenReleaseFinger = true;
				}
			}
			isRecoredTouchDown = false;
			isBackFromRelaseToPullRefresh = false;
			final boolean isHandlerMotionUp = mOnGestureListener.onMotionUp(ev);
			if (isHandlerMotionUp) {
				mLastDownRawY = y;
				return true;
			}
			break;
		}
		}
		mLastDownRawY = y;
		return super.onTouchEvent(ev);
	}

	private OnGesterListener mOnGestureListener = new OnGesterListener() {

		@Override
		public boolean onListViewTopAndPullDown(int delta) {
			return false;
		}

		@Override
		public boolean onListViewBottomAndPullUp(int delta) {
			return false;
		}

		@Override
		public boolean onMotionDown(MotionEvent ev) {
			return false;
		}

		@Override
		public boolean onMotionMove(MotionEvent ev, int delta) {
			return false;
		}

		@Override
		public boolean onMotionUp(MotionEvent ev) {
			return false;
		}

	};

	public void setTopPosition(int index) {
		if (getAdapter() == null)
			throw new NullPointerException("You must set adapter before setTopPosition!");
		if (index < 0)
			throw new IllegalArgumentException("Top position must > 0");
	}

	public void setBottomPosition(int index) {
		mBottomPosition = index;
	}

	public void setOnScrollOverListener(OnGesterListener onGestureListener) {
		mOnGestureListener = onGestureListener;
	}

	public interface OnGesterListener {

		boolean onListViewTopAndPullDown(int delta);

		boolean onListViewBottomAndPullUp(int delta);

		boolean onMotionDown(MotionEvent ev);

		boolean onMotionMove(MotionEvent ev, int delta);

		boolean onMotionUp(MotionEvent ev);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		firstItemIndexInAdapter = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	public void onRefreshComplete() {
		currentState = STATE_IDLE;
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
		headerLastUpdatedTimeTextView.setText("最近更新:" + dateStr);
		applyCurrentStateToHeaderView();
	}

	public void performRefresh() {
		currentState = STATE_REFRESHING;
		setSelection(0);
		applyCurrentStateToHeaderView();
	}

	private void applyCurrentStateToHeaderView() {
		switch (currentState) {
		case STATE_RELEASE_TO_REFRESH:
			headerArrowImageView.setVisibility(View.VISIBLE);
			headerRefreshIndicatorProgressBar.setVisibility(View.GONE);
			headerStateTipsTextview.setVisibility(View.VISIBLE);
			headerLastUpdatedTimeTextView.setVisibility(View.VISIBLE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.startAnimation(headerArrowAnimation);
			headerStateTipsTextview.setText("松开刷新");
			break;
		case STATE_PULL_TO_REFRESH:
			headerRefreshIndicatorProgressBar.setVisibility(View.GONE);
			headerStateTipsTextview.setVisibility(View.VISIBLE);
			headerLastUpdatedTimeTextView.setVisibility(View.VISIBLE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.setVisibility(View.VISIBLE);
			if (isBackFromRelaseToPullRefresh) {
				isBackFromRelaseToPullRefresh = false;
				headerArrowImageView.clearAnimation();
				headerArrowImageView.startAnimation(headerArrowReverseAnimation);
				headerStateTipsTextview.setText("下拉刷新");
			} else {
				headerStateTipsTextview.setText("下拉刷新");
			}
			break;
		case STATE_REFRESHING:
			headerContainerView.setPadding(0, 0, 0, 0);
			headerRefreshIndicatorProgressBar.setVisibility(View.VISIBLE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.setVisibility(View.GONE);
			headerStateTipsTextview.setText("正在刷新...");
			headerLastUpdatedTimeTextView.setVisibility(View.VISIBLE);
			break;
		case STATE_IDLE:
			headerContainerView.setPadding(0, -1 * headContentHeight, 0, 0);
			headerRefreshIndicatorProgressBar.setVisibility(View.GONE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.setImageResource(R.drawable.pulltorefresh_ic_arrow_v1);
			headerStateTipsTextview.setText("下拉刷新");
			headerLastUpdatedTimeTextView.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);

	}
}
