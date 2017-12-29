package com.jald.reserve.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KWaringGoodsListAdapter;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KOutOfStockGoodsListResponseBean;
import com.jald.reserve.bean.http.response.KOutOfStockGoodsListResponseBean.KOutOfStockGoodsItem;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.k_activity_goods_warning)
public class KGoodsWarningListActivity extends Activity {

	@ViewInject(R.id.good_list)
	private ListView goodsList;
	private KWaringGoodsListAdapter adapter;
	private List<KOutOfStockGoodsItem> listData = new ArrayList<KOutOfStockGoodsListResponseBean.KOutOfStockGoodsItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		this.adapter = new KWaringGoodsListAdapter(this);
		this.adapter.setListData(listData);
		this.goodsList.setAdapter(adapter);
		this.loadOutOfStockGoodList();
	}

	private void loadOutOfStockGoodList() {
		DialogProvider.showProgressBar(this);
		JSONObject json = new JSONObject();
		Random random = new Random();
		json.put("random", random.nextInt(99999999));
		KHttpClient.singleInstance().postData(this, KHttpAdress.ITEM_WARNING, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(),
				new KHttpCallBack() {
					@Override
					public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
						DialogProvider.hideProgressBar();
						if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
							KOutOfStockGoodsListResponseBean response = JSON.parseObject(result.getContent(), KOutOfStockGoodsListResponseBean.class);
							listData = response.getList();
							adapter.setListData(listData);
							adapter.notifyDataSetChanged();
							if (listData.size() == 0) {
								Toast.makeText(KGoodsWarningListActivity.this, "暂无缺货商品", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}
}
