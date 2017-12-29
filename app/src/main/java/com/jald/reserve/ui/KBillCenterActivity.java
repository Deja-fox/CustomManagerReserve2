package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jald.reserve.R;
import com.jald.reserve.ui.fragment.KConvenientChargeTransBillListFragment;
import com.jald.reserve.ui.fragment.KNanYueLoanTransBillListFragment;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


public class KBillCenterActivity extends KBaseActivity {

	@ViewInject(R.id.tab_nanyue_detail)
	private LinearLayout tabNanYueDetail;
	@ViewInject(R.id.tab_bianmin_detail)
	private LinearLayout tabBianMinDetail;

	@ViewInject(R.id.tab_nanyue_strip)
	private ImageView tabNanYueStrip;
	@ViewInject(R.id.tab_bianmin_strip)
	private ImageView tabBianMinStrip;

	private KNanYueLoanTransBillListFragment nanYueLoanBillFragment;
	private KConvenientChargeTransBillListFragment convenientChargeBillFragment;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.k_activity_billcenter);

		x.view().inject(this);
		this.convenientChargeBillFragment = new KConvenientChargeTransBillListFragment();
		this.nanYueLoanBillFragment = new KNanYueLoanTransBillListFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, this.nanYueLoanBillFragment, "nanYueLoanBillFragment");
		transaction.commitAllowingStateLoss();
	}

	@Event(R.id.tab_nanyue_detail)
	private void onNanYueClick(View v) {
		tabNanYueStrip.setVisibility(View.VISIBLE);
		tabBianMinStrip.setVisibility(View.INVISIBLE);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, this.nanYueLoanBillFragment, "nanYueLoanBillFragment");
		transaction.commitAllowingStateLoss();
	}

	@Event(R.id.tab_bianmin_detail)
	private void onBianMinClick(View v) {
		tabNanYueStrip.setVisibility(View.INVISIBLE);
		tabBianMinStrip.setVisibility(View.VISIBLE);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, this.convenientChargeBillFragment, "convenientChargeBillFragment");
		transaction.commitAllowingStateLoss();
	}

}
