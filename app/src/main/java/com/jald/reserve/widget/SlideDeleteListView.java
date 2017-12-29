package com.jald.reserve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.Toast;
import com.jald.reserve.R;

public class SlideDeleteListView extends ListView {

	private Context mContext;
	private int slidePosition, preSlidePosition;
	private int downY;
	private int downX;
	public View itemView, preItemView;
	public boolean isTheSameView = false;
	private Scroller scroller;
	private final int SNAP_VELOCITY = 600;
	private VelocityTracker velocityTracker;
	public boolean isSlide = false;
	public int sildeOritation = -1;
	private boolean hasNotResponse = true;
	public boolean shouldShowOrHide = true;
	private int mTouchSlop;

	private RemoveListener mRemoveListener;
	private RemoveListener mInnerRemoveListener = new RemoveListener() {
		@Override
		public void removeItem(int position) {
			position++;
			if (mRemoveListener == null) {
				Toast.makeText(mContext, "点击了第" + position + "项", Toast.LENGTH_SHORT).show();
			} else {
				mRemoveListener.removeItem(position);
			}
		}
	};

	private Animation hideAnimation;
	private Animation showAnimation;

	public SlideDeleteListView(Context context) {
		this(context, null);
		mContext = context;
	}

	public SlideDeleteListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public SlideDeleteListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public void reset() {
		slidePosition = 0;
		preSlidePosition = 0;
		downY = 0;
		downX = 0;
		itemView = null;
		preItemView = null;
		isTheSameView = false;
		isSlide = false;
		sildeOritation = -1;
		hasNotResponse = true;
		shouldShowOrHide = true;
	}

	public void setRemoveListener(RemoveListener removeListener) {
		this.mRemoveListener = removeListener;
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			sildeOritation = -1;
			isTheSameView = false;
			addVelocityTracker(event);
			downX = (int) event.getX();
			downY = (int) event.getY();
			slidePosition = pointToPosition(downX, downY);
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}
			if (preItemView != null && preItemView.findViewById(R.id.tv_coating).getVisibility() == View.GONE) {
				View current = getChildAt(slidePosition - getFirstVisiblePosition());
				if (current != preItemView) {
					isTheSameView = false;
				} else {
					isTheSameView = true;
				}
				itemView = preItemView;
				slidePosition = preSlidePosition;
			} else {
				itemView = getChildAt(slidePosition - getFirstVisiblePosition());
				preItemView = itemView;
				preSlidePosition = slidePosition;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int v = getScrollVelocity();
			float deltX = event.getX() - downX;
			float deltY = event.getY() - downY;
			if (Math.abs(v) > SNAP_VELOCITY || (Math.abs(deltX) > mTouchSlop && Math.abs(deltY) < mTouchSlop)) {
				isSlide = true;
				if (v != 0) {
					if (v > 0) {
						sildeOritation = 1;
					} else {
						sildeOritation = 0;
					}
				} else {
					if (deltX > 0) {
						sildeOritation = 1;
					} else {
						sildeOritation = 0;
					}
				}
			} else if (Math.abs(deltY) > Math.abs(deltX)) {
				if (preItemView != null && preItemView.findViewById(R.id.tv_coating).getVisibility() == View.GONE) {
					return false;
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			shouldShowOrHide = true;
			sildeOritation = -1;
			isTheSameView = false;
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (itemView.findViewById(R.id.tv_coating).getVisibility() == View.VISIBLE) {
				isSlide = false;
			} else {
				isSlide = true;
			}
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent ev) {
		if (slidePosition != AdapterView.INVALID_POSITION) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				if (itemView != null && itemView.findViewById(R.id.tv_coating).getVisibility() == View.GONE) {
					showAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
					showAnimation.setDuration(250);
					showAnimation.setAnimationListener(new AnimationListener() {

						public void onAnimationStart(Animation animation) {
							shouldShowOrHide = false;
						}

						public void onAnimationRepeat(Animation animation) {
						}

						public void onAnimationEnd(Animation animation) {
							isSlide = false;
							itemView.findViewById(R.id.tv_coating).setVisibility(View.VISIBLE);
							itemView.findViewById(R.id.tv_functions).setClickable(false);
						}
					});
					itemView.findViewById(R.id.tv_coating).startAnimation(showAnimation);
				}
			}
		}
		if (isSlide && slidePosition != AdapterView.INVALID_POSITION && sildeOritation != -1) {
			addVelocityTracker(ev);
			final int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (shouldShowOrHide) {
					if (itemView.findViewById(R.id.tv_coating).getVisibility() == View.VISIBLE && hasNotResponse == true && sildeOritation == 0) {
						hideAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f);
						hideAnimation.setDuration(250);
						hideAnimation.setAnimationListener(new AnimationListener() {
							public void onAnimationStart(Animation animation) {
								hasNotResponse = false;
								shouldShowOrHide = false;
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationEnd(Animation animation) {
								hasNotResponse = true;
								itemView.findViewById(R.id.tv_coating).setVisibility(View.GONE);
								itemView.findViewById(R.id.tv_functions).setClickable(true);
								itemView.findViewById(R.id.tv_functions).setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										mInnerRemoveListener.removeItem(slidePosition);
									}

								});

							}
						});
						itemView.findViewById(R.id.tv_coating).startAnimation(hideAnimation);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				shouldShowOrHide = true;
				sildeOritation = -1;
				isTheSameView = false;
				recycleVelocityTracker();
				break;
			}
			return true;
		}

		return super.onTouchEvent(ev);
	}

	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			postInvalidate();
			if (scroller.isFinished()) {
				if (mRemoveListener == null) {
					throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
				}
				itemView.scrollTo(0, 0);
			}
		}
	}

	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}

	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	private int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}

	public interface RemoveListener {
		public void removeItem(int position);
	}

}
