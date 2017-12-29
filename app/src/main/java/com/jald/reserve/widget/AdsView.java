package com.jald.reserve.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KSildeAdResponseBean.KAdsItemBean;
import com.open.imageloader.core.imageaware.ImageViewAware;

public class AdsView extends RelativeLayout {

	private Context ctx;
	private ViewGroup contentView;
	private LayoutInflater inflater;
	private RelativeLayout adsContainerLayer;
	private ImageView defaultAdImgViewLayer;
	private ViewPager viewPager;
	private LinearLayout dotContainer;
	private List<View> dotList;
	private Handler mHandler;
	private List<KAdsItemBean> adsList = new ArrayList<KAdsItemBean>();

	private boolean isUserScroll = false;

	public AdsView(Context context) {
		super(context);
		this.ctx = context;
		inflater = LayoutInflater.from(ctx);
		initAdsView();
	}

	public AdsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		inflater = LayoutInflater.from(ctx);
		initAdsView();
	}

	public AdsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.ctx = context;
		inflater = LayoutInflater.from(ctx);
		initAdsView();
	}

	@SuppressLint("InflateParams")
	private final void initAdsView() {
		this.removeAllViews();
		mHandler = new Handler(ctx.getMainLooper());
		contentView = (ViewGroup) this.inflater.inflate(R.layout.ads_view_layout, null);
		this.addView(contentView);
		adsContainerLayer = (RelativeLayout) contentView.findViewById(R.id.ads_container);
		defaultAdImgViewLayer = (ImageView) contentView.findViewById(R.id.default_ad_image);
		viewPager = (ViewPager) contentView.findViewById(R.id.adsViewPager);
		viewPager.setOnPageChangeListener(new OnAdsPageChangeListener());
		viewPager.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isUserScroll = true;
					break;
				case MotionEvent.ACTION_MOVE:
					isUserScroll = true;
					break;
				case MotionEvent.ACTION_UP:
					isUserScroll = false;
				default:
					break;
				}
				return false;
			}
		});
		dotContainer = (LinearLayout) contentView.findViewById(R.id.ads_view_dot_container);
	}

	private class OnAdsPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			changeIndicator(position);
		}

		@Override
		public void onPageScrolled(int position, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void setAdsList(List<KAdsItemBean> list) {
		this.adsList = list;
		if (list == null || list.size() == 0) {
			defaultAdImgViewLayer.setVisibility(View.VISIBLE);
			adsContainerLayer.setVisibility(View.GONE);
			return;
		}
		if (list.size() == 1) {
			dotContainer.setVisibility(View.GONE);
		} else {
			dotContainer.setVisibility(View.VISIBLE);
		}
		defaultAdImgViewLayer.setVisibility(View.GONE);
		adsContainerLayer.setVisibility(View.VISIBLE);
		showIndicator(list.size(), 0);
		List<ImageView> viewList = new ArrayList<ImageView>();
		AdsAdapter adapter = new AdsAdapter(ctx);
		for (int i = 0; i < list.size(); ++i) {
			ImageView imageView = new ImageView(this.ctx);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageResource(R.drawable.image_loading);
			ImageViewAware aware = new ImageViewAware(imageView);
			KBaseApplication.getInstance().getImageLoader().displayImage(list.get(i).getAd_img_url(), aware);
			viewList.add(imageView);
		}
		adapter.setViewLsit(viewList);
		this.viewPager.setAdapter(adapter);
		viewPager.getAdapter().notifyDataSetChanged();
		startAutoChange();

	}

	private Runnable run = new Runnable() {
		@Override
		public void run() {
			if (!isUserScroll) {
				int cur = viewPager.getCurrentItem();
				AdsAdapter adp = (AdsAdapter) viewPager.getAdapter();
				cur = (cur + 1) % adp.getViewLsit().size();
				viewPager.setCurrentItem(cur, true);
				changeIndicator(cur);
			}
			mHandler.removeCallbacks(this);
			startAutoChange();

		}
	};

	public void startAutoChange() {
		this.mHandler.postDelayed(run, 5000);
	}

	public void pauseAutoChange() {
		mHandler.removeCallbacks(run);
	}

	@SuppressLint("InflateParams")
	public void showIndicator(int count, int whichSelected) {
		dotContainer.removeAllViews();
		dotList = new ArrayList<View>();
		for (int i = 0; i < count; ++i) {
			View dotWraper = this.inflater.inflate(R.layout.ads_dot, null);
			View dot = dotWraper.findViewById(R.id.ads_view_dot);
			dotList.add(dot);
			if (whichSelected == i) {
				dot.findViewById(R.id.ads_view_dot).setBackgroundResource(R.drawable.home_img_ratio_selected);
			}
			dotContainer.addView(dotWraper);
		}
	}

	public void changeIndicator(int index) {
		for (int i = 0; i < this.dotList.size(); ++i) {
			this.dotList.get(i).setBackgroundResource(R.drawable.home_img_ratio);
		}
		this.dotList.get(index).setBackgroundResource(R.drawable.home_img_ratio_selected);
	}

	public List<KAdsItemBean> getAdsList() {
		return adsList;
	}

	class AdsAdapter extends PagerAdapter {

		@SuppressWarnings("unused")
		private Context cxt;
		private List<ImageView> viewLsit = new ArrayList<ImageView>();

		public AdsAdapter(Context context) {
			this.cxt = context;
		}

		@Override
		public int getCount() {
			return this.viewLsit.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(this.viewLsit.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			ImageView img = this.viewLsit.get(position);
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			container.addView(img);
			return this.viewLsit.get(position);
		}

		public List<ImageView> getViewLsit() {
			return viewLsit;
		}

		public void setViewLsit(List<ImageView> viewLsit) {
			this.viewLsit = viewLsit;
		}

	}

}
