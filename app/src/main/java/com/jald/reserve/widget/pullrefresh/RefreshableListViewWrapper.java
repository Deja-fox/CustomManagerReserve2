package com.jald.reserve.widget.pullrefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.widget.pullrefresh.RefreshableListView.OnGesterListener;

public class RefreshableListViewWrapper extends LinearLayout implements OnGesterListener {

	public interface OnPullListener {
		void onRefresh();

		void onMore();
	}

	@SuppressWarnings("unused")
	private Context mContext;

	private final int START_PULL_DEVIATION = 80;
	private final int WHAT_FINISH_LOAD_MORE = 0x1;
	private final int WHAT_CLOSE_LOAD_MORE = 0x2;
	private final int WHAT_FINISH_REFRESH = 0x4;

	private Config mConfig;

	private RelativeLayout mFooterView;
	private TextView mFooterTextView;
	private ProgressBar mFooterLoadingView;
	private RefreshableListView mListView;

	private OnPullListener mOnPullDownListener;
	private float mMotionDownLastY;
	private boolean mIsFetchMoreing;
	private boolean mIsLoadMoreClosed = false;

	public RefreshableListViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initHeaderViewAndFooterViewAndListView(context);
	}

	public RefreshableListViewWrapper(Context context) {
		super(context);
		mContext = context;
		initHeaderViewAndFooterViewAndListView(context);
	}

	private void initHeaderViewAndFooterViewAndListView(Context context) {
		if (mConfig == null) {
			mConfig = new Config.Build(context).build();
		}
		setOrientation(LinearLayout.VERTICAL);
		mFooterView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer_v1, this, false);
		mFooterView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
		mFooterTextView = (TextView) mFooterView.findViewById(R.id.pulldown_footer_text);
		mFooterTextView.setText(mConfig.tipStrOfLoadMore);
		mFooterLoadingView = (ProgressBar) mFooterView.findViewById(R.id.pulldown_footer_loading);
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIsFetchMoreing) {
					mIsFetchMoreing = true;
					mFooterTextView.setText(mConfig.tipOfLoadingMore);
					mFooterLoadingView.setVisibility(View.VISIBLE);
					mOnPullDownListener.onMore();
				}
			}
		});

		mListView = new RefreshableListView(context);
		mListView.setOnScrollOverListener(this);
		mListView.setCacheColorHint(0);
		mListView.setHeaderDividersEnabled(mConfig.isEnabledHeaderDividers());
		mListView.setFooterDividersEnabled(mConfig.isEnabledFooterDividers());
		mListView.setOverScrollMode(mConfig.getListOverScrollMode());
		mListView.setDivider(mConfig.getDividerDrawable());
		mListView.setDividerHeight(mConfig.getDividerHeight());
		mListView.setSelector(mConfig.getSelectorDrawable());
		mListView.addFooterView(mFooterView);
		mListView.setBottomPosition(mConfig.getLoadMoreAutoTrigerIndex());
		addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mOnPullDownListener = new OnPullListener() {
			@Override
			public void onRefresh() {
			}

			@Override
			public void onMore() {
			}
		};
	}

	public void reset() {
		mIsLoadMoreClosed = false;
		mIsFetchMoreing = false;
		mFooterView.setVisibility(View.VISIBLE);
		mFooterTextView.setVisibility(View.VISIBLE);
		mFooterTextView.setText(mConfig.tipStrOfLoadMore);
		mFooterLoadingView.setVisibility(View.GONE);
	}

	public void notifyDidMore() {
		mUIHandler.sendEmptyMessage(WHAT_FINISH_LOAD_MORE);
	}

	public void notifyCloseLoadMore(boolean isHideFooter) {
		mUIHandler.obtainMessage(WHAT_CLOSE_LOAD_MORE, isHideFooter).sendToTarget();
	}

	public void notifyRefreshComplete() {
		mUIHandler.sendEmptyMessage(WHAT_FINISH_REFRESH);
	}

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_FINISH_REFRESH:
				mListView.onRefreshComplete();
				return;
			case WHAT_FINISH_LOAD_MORE:
				mIsFetchMoreing = false;
				mFooterTextView.setText(mConfig.tipStrOfLoadMore);
				mFooterLoadingView.setVisibility(View.GONE);
				break;
			case WHAT_CLOSE_LOAD_MORE:
				mIsLoadMoreClosed = true;
				mIsFetchMoreing = false;
				mFooterTextView.setText(mConfig.tipOfLoadMoreClosed);
				mFooterLoadingView.setVisibility(View.GONE);
				boolean shouldHideFooter = (Boolean) msg.obj;
				if (shouldHideFooter) {
					setHideFooter();
				}
				break;
			}
		}
	};

	public ListView getListView() {
		return mListView;
	}

	@Override
	public boolean onMotionDown(MotionEvent ev) {
		mMotionDownLastY = ev.getRawY();
		return false;
	}

	@Override
	public boolean onMotionMove(MotionEvent ev, int delta) {
		final int absMotionY = (int) Math.abs(ev.getRawY() - mMotionDownLastY);
		if (absMotionY < START_PULL_DEVIATION)
			return true;
		return false;
	}

	@Override
	public boolean onListViewBottomAndPullUp(int delta) {
		if (!mConfig.isEnableAutoFetchMore() || mIsFetchMoreing || mIsLoadMoreClosed)
			return false;
		if (isFillScreenItem()) {
			mIsFetchMoreing = true;
			mFooterTextView.setText(mConfig.getTipOfLoadingMore());
			mFooterLoadingView.setVisibility(View.VISIBLE);
			mOnPullDownListener.onMore();
			return true;
		}
		return false;
	}

	@Override
	public boolean onListViewTopAndPullDown(int delta) {
		return true;
	}

	@Override
	public boolean onMotionUp(MotionEvent ev) {
		@SuppressWarnings("unused")
		final int y = (int) ev.getRawY();
		if (mListView.canRefreashWhenReleaseFinger) {
			mListView.canRefreashWhenReleaseFinger = false;
			mOnPullDownListener.onRefresh();
		}
		return false;
	}

	public void setHideHeader() {
		mListView.shouldOpenHeaderViewWhenTouch = false;
	}

	public void setShowHeader() {
		mListView.shouldOpenHeaderViewWhenTouch = true;
	}

	private void setHideFooter() {
		mFooterView.setVisibility(View.GONE);
		mFooterTextView.setVisibility(View.GONE);
		mFooterLoadingView.setVisibility(View.GONE);
	}

	public void setConfig(Config mConfig) {
		this.mConfig = mConfig;
	}

	@SuppressWarnings("unused")
	private void performLoadingMore() {
		mFooterView.setVisibility(View.VISIBLE);
		mFooterTextView.setVisibility(View.VISIBLE);
		mFooterLoadingView.setVisibility(View.VISIBLE);
	}

	public void performRefresh() {
		mListView.performRefresh();
	}

	public void setOnPullDownListener(OnPullListener listener) {
		mOnPullDownListener = listener;
	}

	private boolean isFillScreenItem() {
		final int firstVisiblePositionInAdapter = mListView.getFirstVisiblePosition();
		final int lastVisiblePostionInAdapter = mListView.getLastVisiblePosition() - mListView.getFooterViewsCount();
		final int visibleItemCount = lastVisiblePostionInAdapter - firstVisiblePositionInAdapter + 1;
		final int totalItemCountInAdapter = mListView.getCount() - mListView.getFooterViewsCount();
		if (visibleItemCount < totalItemCountInAdapter)
			return true;
		else
			return false;
	}

	public static class Config {
		private String tipStrOfLoadMore;
		private String tipOfLoadingMore;
		private String tipOfLoadMoreClosed;
		private boolean isEnableAutoFetchMore;
		boolean isEnabledHeaderDividers;
		boolean isEnabledFooterDividers;
		private int dividerHeight;
		private Drawable dividerDrawable;
		private Drawable selectorDrawable;
		private int listOverScrollMode;
		private int loadingMoreAutoTrigerIndex;

		private Config(Build build) {
			this.tipStrOfLoadMore = build.tipStrOfLoadMore;
			this.tipOfLoadingMore = build.tipOfLoadingMore;
			this.tipOfLoadMoreClosed = build.tipOfLoadMoreClosed;
			this.isEnableAutoFetchMore = build.isEnableAutoFetchMore;
			this.dividerHeight = build.dividerHeight;
			this.dividerDrawable = build.dividerDrawable;
			this.selectorDrawable = build.selectorDrawable;
			this.isEnabledFooterDividers = build.isEnabledFooterDividers;
			this.isEnabledHeaderDividers = build.isEnabledHeaderDividers;
			this.listOverScrollMode = build.listOverScrollMode;
			this.loadingMoreAutoTrigerIndex = build.loadingMoreAutoTrigerIndex;
		}

		public String getTipOfLoadMoreClosed() {
			return tipOfLoadMoreClosed;
		}

		public int getLoadMoreAutoTrigerIndex() {
			return loadingMoreAutoTrigerIndex;
		}

		public Drawable getDividerDrawable() {
			return dividerDrawable;
		}

		public int getListOverScrollMode() {
			return listOverScrollMode;
		}

		public boolean isEnabledHeaderDividers() {
			return isEnabledHeaderDividers;
		}

		public boolean isEnabledFooterDividers() {
			return isEnabledFooterDividers;
		}

		public String getTipStrOfLoadMore() {
			return tipStrOfLoadMore;
		}

		public Drawable getSelectorDrawable() {
			return selectorDrawable;
		}

		public boolean isEnableAutoFetchMore() {
			return isEnableAutoFetchMore;
		}

		public String getTipOfLoadingMore() {
			return tipOfLoadingMore;
		}

		public int getDividerHeight() {
			return dividerHeight;
		}

		public static class Build {
			private Context context;
			private String tipStrOfLoadMore;
			private String tipOfLoadingMore;
			private String tipOfLoadMoreClosed;
			private boolean isEnableAutoFetchMore;
			private boolean isEnabledFooterDividers;
			private boolean isEnabledHeaderDividers;
			private int dividerHeight;
			private Drawable dividerDrawable;
			private Drawable selectorDrawable;
			private int listOverScrollMode;
			private int loadingMoreAutoTrigerIndex;

			public Build(Context context) {
				this.context = context;
				initDefaultValue();
			}

			private void initDefaultValue() {
				this.tipStrOfLoadMore = "点击或上拉加载更多";
				this.tipOfLoadingMore = "加载更多中...";
				this.tipOfLoadMoreClosed = "列表已到最后";
				this.isEnableAutoFetchMore = true;
				this.isEnabledFooterDividers = false;
				this.isEnabledHeaderDividers = true;
				this.listOverScrollMode = ListView.OVER_SCROLL_NEVER;
				this.loadingMoreAutoTrigerIndex = 0;
				this.dividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1.0, context.getResources()
						.getDisplayMetrics());
				this.dividerDrawable = new ColorDrawable(Color.parseColor("#80A0A0A0"));
				Drawable colorDrawable1 = new ColorDrawable(Color.parseColor("#E0E0E0"));
				colorDrawable1.setState(new int[] { android.R.attr.state_pressed });
				Drawable colorDrawable2 = new ColorDrawable(Color.parseColor("#00000000"));
				colorDrawable2.setState(new int[] { android.R.attr.state_empty });
				StateListDrawable sd = new StateListDrawable();
				sd.addState(new int[] { android.R.attr.state_pressed }, colorDrawable1);
				sd.addState(new int[] { -android.R.attr.state_pressed }, colorDrawable2);
				sd.addState(new int[] {}, colorDrawable2);
				this.selectorDrawable = sd;

			}

			public void setTipOfLoadMoreClosed(String tipOfLoadMoreClosed) {
				this.tipOfLoadMoreClosed = tipOfLoadMoreClosed;
			}

			public void setLoadingMoreAutoTrigerIndex(int loadingMoreAutoTrigerIndex) {
				if (loadingMoreAutoTrigerIndex < 0) {
					this.loadingMoreAutoTrigerIndex = 0;
				} else {
					this.loadingMoreAutoTrigerIndex = loadingMoreAutoTrigerIndex;
				}
			}

			public void setDividerDrawable(Drawable dividerDrawable) {
				this.dividerDrawable = dividerDrawable;
			}

			public void setListOverScrollMode(int listOverScrollMode) {
				this.listOverScrollMode = listOverScrollMode;
			}

			public void setEnabledHeaderDividers(boolean isEnabledHeaderDividers) {
				this.isEnabledHeaderDividers = isEnabledHeaderDividers;
			}

			public void setEnabledFooterDividers(boolean isEnabledFooterDividers) {
				this.isEnabledFooterDividers = isEnabledFooterDividers;
			}

			public void setSelectorDrawable(Drawable selectorDrawable) {
				this.selectorDrawable = selectorDrawable;
			}

			public void setTipStrOfLoadMore(String tipStrOfLoadMore) {
				this.tipStrOfLoadMore = tipStrOfLoadMore;
			}

			public void setEnableAutoFetchMore(boolean isEnableAutoFetchMore) {
				this.isEnableAutoFetchMore = isEnableAutoFetchMore;
			}

			public void setTipOfLoadingMore(String tipOfLoadingMore) {
				this.tipOfLoadingMore = tipOfLoadingMore;
			}

			public void setDividerHeight(int dividerHeight) {
				this.dividerHeight = dividerHeight;
			}

			public Config build() {
				return new Config(this);
			}
		}
	}

}
