package com.jald.reserve.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KViolationRecordQueryRequestBean;
import com.jald.reserve.bean.http.response.KViolationPaymentQueryResponseBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;
import com.jald.reserve.bean.normal.KTrafficFineContextBean;
import com.jald.reserve.bean.normal.KTrafficPostInfoFrom;
import com.jald.reserve.ui.fragment.KViolationOrderSubmitFragment;
import com.jald.reserve.ui.fragment.KViolationOrderSubmitFragment.KViolationOrderSubmitFragmentListener;
import com.jald.reserve.ui.fragment.KViolationPostAddressFragment;
import com.jald.reserve.ui.fragment.KViolationPostAddressFragment.KViolationPostAddressFragmentListener;
import com.jald.reserve.ui.fragment.KViolationQueryFragment;
import com.jald.reserve.ui.fragment.KViolationQueryFragment.KViolationQueryFragmentListener;
import com.jald.reserve.ui.fragment.KViolationRecordDetailFragment;
import com.jald.reserve.ui.fragment.KViolationRecordListFragment;
import com.jald.reserve.ui.fragment.KViolationRecordListFragment.ViolationRecordListFragmentListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.k_activity_traffic_fine)
public class KTrafficFineActivity extends KBaseActivity {

	@ViewInject(R.id.title)
	private TextView txtTitle;

	private KViolationQueryFragment violationQueryFragment;
	private KViolationRecordListFragment violationRecordListFragment;
	private KViolationRecordDetailFragment violationRecordDetailFragment;
	private KViolationPostAddressFragment violationPostAddressFragment;
	private KViolationOrderSubmitFragment violationOrderSubmitFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		this.violationQueryFragment = new KViolationQueryFragment();
		this.violationRecordListFragment = new KViolationRecordListFragment();
		this.violationRecordDetailFragment = new KViolationRecordDetailFragment();
		this.violationPostAddressFragment = new KViolationPostAddressFragment();
		this.violationOrderSubmitFragment = new KViolationOrderSubmitFragment();
		loadViolationQueryFragment();
	}

	void loadViolationQueryFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, violationQueryFragment, "violationQueryFragment");
		transaction.commitAllowingStateLoss();
		violationQueryFragment.setOnRequestChangeTitleListener(new KViolationQueryFragment.OnRequestChangeTitleListener() {
			@Override
			public void requestChangeTitle(String title) {
				changeTitle(title);
			}
		});
		violationQueryFragment.setListener(new KViolationQueryFragmentListener() {
			@Override
			public void onViolationQueryReturn(KViolationRecordQueryRequestBean queryCondition, KViolationRecordQueryResponseBean queryResponse) {
				Bundle args = new Bundle();
				args.putSerializable(KViolationRecordListFragment.ARG_KEY_QUERY_CONDITION, queryCondition);
				args.putSerializable(KViolationRecordListFragment.ARG_KEY_VIOLATION_QUERY_RESPONSE_BEAN, queryResponse);
				violationRecordListFragment.setArguments(args);
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
				transaction.replace(R.id.container, violationRecordListFragment, "KViolationRecordListFragment");
				transaction.addToBackStack("violationRecordListFragment");
				transaction.commitAllowingStateLoss();
				violationRecordListFragment.setOnRequestChangeTitleListener(new KViolationRecordListFragment.OnRequestChangeTitleListener() {
					@Override
					public void requestChangeTitle(String title) {
						changeTitle(title);
					}
				});
				violationRecordListFragment.setListener(new ViolationRecordListFragmentListener() {
					@Override
					public void onViolationRecordClicked(KViolationRecordBean record) {
						FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
						Bundle args = new Bundle();
						args.putSerializable(KViolationRecordDetailFragment.ARG_KEY_VIOLATION_RECORD_BEAN, record);
						violationRecordDetailFragment.setArguments(args);
						transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						transaction.replace(R.id.container, violationRecordDetailFragment, "KViolationRecordDetailFragment");
						transaction.addToBackStack(null);
						transaction.commitAllowingStateLoss();
						violationRecordDetailFragment
								.setOnRequestChangeTitleListener(new KViolationRecordDetailFragment.OnRequestChangeTitleListener() {
									@Override
									public void requestChangeTitle(String title) {
										changeTitle(title);
									}
								});
					}

					@Override
					public void onNextStepClicked(KViolationRecordQueryRequestBean queryCondition, ArrayList<KViolationRecordBean> selectedRecordList) {
						loadPostAddressFragment(queryCondition, selectedRecordList);
					}
				});
			}
		});
	}

	private void loadPostAddressFragment(KViolationRecordQueryRequestBean queryCondition, ArrayList<KViolationRecordBean> selectedRecordList) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		Bundle args = new Bundle();
		args.putSerializable(KViolationPostAddressFragment.ARG_KEY_QUERY_CONDITION, queryCondition);
		args.putSerializable(KViolationPostAddressFragment.ARG_KEY_SELECTED_VIOLATION_RECORD, selectedRecordList);
		violationPostAddressFragment.setArguments(args);
		transaction.replace(R.id.container, violationPostAddressFragment, "KViolationPostAddressFragment");
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
		violationPostAddressFragment.setListener(new KViolationPostAddressFragmentListener() {
			@Override
			public void onPageShow() {
				changeTitle("邮寄信息");
			}

			@Override
			public void onPageHide() {
				changeTitle("违章记录");
			}

			@Override
			public void onNextStepClick(KViolationRecordQueryRequestBean queryCondition, ArrayList<KViolationRecordBean> selectedRecordList,
					KTrafficPostInfoFrom postInfo, KViolationPaymentQueryResponseBean paymentInfoBean) {
				KTrafficFineContextBean contextBean = new KTrafficFineContextBean();
				contextBean.setPaymentInfoBean(paymentInfoBean);
				contextBean.setPostInfo(postInfo);
				contextBean.setQueryCondition(queryCondition);
				contextBean.setSelectedRecordList(selectedRecordList);
				loadViolationOrderSubmitFragment(contextBean);
			}
		});
	}

	private void loadViolationOrderSubmitFragment(KTrafficFineContextBean contextBean) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		Bundle args = new Bundle();
		args.putSerializable(KViolationOrderSubmitFragment.ARG_KEY_TRAFFIC_FINE_CONTEXT_BEAN, contextBean);
		violationOrderSubmitFragment.setArguments(args);
		transaction.replace(R.id.container, violationOrderSubmitFragment, "KViolationOrderSubmitFragment");
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
		violationOrderSubmitFragment.setListener(new KViolationOrderSubmitFragmentListener() {
			@Override
			public void onSubmitSuccess() {
				getSupportFragmentManager().popBackStackImmediate("violationRecordListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}

			@Override
			public void onPageShow() {
				changeTitle("订单支付");
			}

			@Override
			public void onPageHide() {
				changeTitle("邮寄信息");
			}
		});
	}

	public void changeTitle(String title) {
		this.txtTitle.setText(title);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
