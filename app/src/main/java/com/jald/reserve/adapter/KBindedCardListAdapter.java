package com.jald.reserve.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean.KBankCardItemBean;
import com.jald.reserve.util.StringUtil;

public class KBindedCardListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<KBankCardItemBean> cardList = new ArrayList<KBankCardItemBean>();

	public KBindedCardListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return cardList.size();
	}

	@Override
	public Object getItem(int position) {
		return cardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		public ImageView imgBankLogo;
		public TextView txtBankName;
		public TextView txtAccountNo;
		public TextView txtIsDefault;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_binded_card_listview_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.imgBankLogo = (ImageView) convertView.findViewById(R.id.bank_logo);
			holder.txtAccountNo = (TextView) convertView.findViewById(R.id.account_no);
			holder.txtBankName = (TextView) convertView.findViewById(R.id.bank_name);
			holder.txtIsDefault = (TextView) convertView.findViewById(R.id.is_default);
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		KBankCardItemBean item = (KBankCardItemBean) getItem(position);
		String logResName = item.getBank_id();
		logResName = "bank_" + logResName;
		int logoResId = context.getResources().getIdentifier(logResName, "drawable", context.getPackageName());
		holder.imgBankLogo.setImageResource(logoResId);
		holder.txtAccountNo.setText(StringUtil.formatDebitCardAccount(item.getDebit_accont_no()));
		holder.txtBankName.setText(item.getBank_name());
		String isDefault = item.getIs_default().trim();
		if (isDefault.equals("1")) {
			holder.txtIsDefault.setVisibility(View.VISIBLE);
		} else {
			holder.txtIsDefault.setVisibility(View.GONE);
		}
		return convertView;
	}

	public void setCardList(List<KBankCardItemBean> cardList) {
		this.cardList = cardList;
	}

}