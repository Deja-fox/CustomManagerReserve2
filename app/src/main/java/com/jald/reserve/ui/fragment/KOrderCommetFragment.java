package com.jald.reserve.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jald.reserve.R;
import com.jald.reserve.util.ViewUtil;


public class KOrderCommetFragment extends Fragment {

    private View mRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        } else {
            mRoot = inflater.inflate(R.layout.k_order_commet_fragmt, container, false);

            return mRoot;
        }
    }
}
