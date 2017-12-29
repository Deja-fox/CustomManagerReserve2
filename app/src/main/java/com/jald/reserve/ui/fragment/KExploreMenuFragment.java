package com.jald.reserve.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.ui.KHistoryOrderActivity;
import com.jald.reserve.ui.KHtmlActivity;
import com.jald.reserve.ui.KLoginPageActivity;
import com.jald.reserve.ui.KWaitToPayOrderActivity;
import com.jald.reserve.util.ViewUtil;

public class KExploreMenuFragment extends Fragment {

    public static final String TAG = KExploreMenuFragment.class.getSimpleName();

	private View mRoot;
	private ListView menuListView;
	private MenuAdapter menuAdapter;
	private List<MenuItem> menuListData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRoot != null) {
			ViewUtil.detachFromParent(this.mRoot);
			return mRoot;
		}
		mRoot = inflater.inflate(R.layout.k_fragment_explore_menu, container, false);
		return mRoot;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.menuListView = (ListView) mRoot.findViewById(R.id.explore_menu_list);
		// MenuItem item1 = new MenuItem("烟草订货", R.drawable.icon_store);
		MenuItem item1 = new MenuItem("待支付订单", R.drawable.icon_wait_pay_order);
		MenuItem item2 = new MenuItem("历史订单", R.drawable.icon_history_order);
		this.menuListData = new ArrayList<KExploreMenuFragment.MenuItem>();
		this.menuListData.add(item1);
		this.menuListData.add(item2);
		this.menuAdapter = new MenuAdapter(getActivity());
		this.menuAdapter.setMenuList(menuListData);
		this.menuListView.setAdapter(menuAdapter);
		this.menuListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!checkLogin()) {
					Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
					return;
				}
				String title = menuListData.get(position).title;
				if (title.equals("待支付订单")) {
					Intent intent = new Intent(getActivity(), KWaitToPayOrderActivity.class);
					startActivity(intent);
				} else if (title.equals("历史订单")) {
					Intent intent = new Intent(getActivity(), KHistoryOrderActivity.class);
					startActivity(intent);
				} else if (title.equals("烟草订货")) {
					KUserInfoStub userLoginInfo = KBaseApplication.getInstance().getUserInfoStub();
					if (userLoginInfo.getLice_id() == null || userLoginInfo.getLice_id().trim().equals("")) {
						Toast.makeText(getActivity(), "您不是烟草专卖商户,无法使用本功能", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent(getActivity(), KHtmlActivity.class);
					String url = KHttpAdress.TOBOCCO_URL;
					url = url.replace("{#1}", userLoginInfo.getToken());
					Log.e(TAG,"令牌URL:" + url);
					intent.putExtra(KHtmlActivity.INTENT_KEY_CONTENT_URL, url);
					intent.putExtra(KHtmlActivity.INTENT_KEY_TITLE, "烟草订货");
					startActivity(intent);
				}
			}
		});
	}

	private boolean checkLogin() {
		if (KBaseApplication.getInstance().getUserInfoStub() == null) {
			Intent intent = new Intent(getActivity(), KLoginPageActivity.class);
			getActivity().startActivity(intent);
			return false;
		}
		return true;
	}

	class MenuItem {
		public String title;
		public int iconRes;

		public MenuItem(String title, int iconRes) {
			this.title = title;
			this.iconRes = iconRes;
		}
	}

	class MenuAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<MenuItem> menuList = new ArrayList<KExploreMenuFragment.MenuItem>();

		public MenuAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return this.menuList.size();
		}

		@Override
		public Object getItem(int position) {
			return this.menuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.k_listview_explore_menu_item, parent, false);
			}
			MenuItem item = this.menuList.get(position);
			ImageView icon = (ImageView) convertView.findViewById(R.id.menu_icon);
			TextView title = (TextView) convertView.findViewById(R.id.menu_title);
			icon.setImageResource(item.iconRes);
			title.setText(item.title);
			return convertView;
		}

		public void setMenuList(List<MenuItem> menuList) {
			this.menuList = menuList;
		}

	}

}
