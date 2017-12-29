package com.jald.reserve.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KViolationRecordListAdapter;
import com.jald.reserve.bean.http.request.KViolationRecordQueryRequestBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class KViolationRecordListFragment extends Fragment {

	public interface ViolationRecordListFragmentListener {
		public void onViolationRecordClicked(KViolationRecordBean record);

		public void onNextStepClicked(KViolationRecordQueryRequestBean queryCondition, ArrayList<KViolationRecordBean> selectedRecordList);
	}

	public interface OnRequestChangeTitleListener {
		public void requestChangeTitle(String title);
	}

	public static final String ARG_KEY_QUERY_CONDITION = "KeyQueryCondition";
	public static final String ARG_KEY_VIOLATION_QUERY_RESPONSE_BEAN = "KeyViolationQueryResponseBean";

	private View mRoot;
	private KViolationRecordQueryRequestBean queryCondition;
	private KViolationRecordQueryResponseBean violationQueryInfo;

	private ArrayList<KViolationRecordBean> selectedRecordList = new ArrayList<KViolationRecordBean>();

	@ViewInject(R.id.vioation_record_list)
	private ListView recordList;

	@ViewInject(R.id.btn_next_step)
	private Button btnNextStep;

	private KViolationRecordListAdapter adapter;

	private ViolationRecordListFragmentListener listener;
	private OnRequestChangeTitleListener onRequestChangeTitleListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getArguments() != null) {
			this.queryCondition = (KViolationRecordQueryRequestBean) getArguments().getSerializable(ARG_KEY_QUERY_CONDITION);
			this.violationQueryInfo = (KViolationRecordQueryResponseBean) getArguments().getSerializable(ARG_KEY_VIOLATION_QUERY_RESPONSE_BEAN);
		}
		this.mRoot = inflater.inflate(R.layout.k_fragment_violation_record_list, container, false);
		x.view().inject(this, mRoot);
		this.adapter = new KViolationRecordListAdapter(getActivity());
		this.adapter.setViolationRecordList(violationQueryInfo.getList());
		recordList.setAdapter(adapter);
		recordList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listener != null) {
					KViolationRecordBean record = violationQueryInfo.getList().get(position);
					listener.onViolationRecordClicked(record);
				}
			}
		});
		return this.mRoot;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (onRequestChangeTitleListener != null) {
			onRequestChangeTitleListener.requestChangeTitle("违章记录");
		}
	}

	@Event(R.id.btn_next_step)
	private void onNextStepClick(View v) {
		this.selectedRecordList.clear();
		if (violationQueryInfo.getList() != null) {
			for (int i = 0; i < violationQueryInfo.getList().size(); ++i) {
				KViolationRecordBean item = violationQueryInfo.getList().get(i);
				if (item.isSelected()) {
					this.selectedRecordList.add(item);
				}
			}
		}
		if (this.selectedRecordList.size() == 0) {
			Toast.makeText(getActivity(), "请选择至少一个要缴费的违章记录", Toast.LENGTH_SHORT).show();
		} else {
			if (listener != null) {
				this.listener.onNextStepClicked(queryCondition, selectedRecordList);
			}
		}
	}

	public ViolationRecordListFragmentListener getListener() {
		return listener;
	}

	public void setListener(ViolationRecordListFragmentListener listener) {
		this.listener = listener;
	}

	public void setOnRequestChangeTitleListener(OnRequestChangeTitleListener onRequestChangeTitleListener) {
		this.onRequestChangeTitleListener = onRequestChangeTitleListener;
	}
}
