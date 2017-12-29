package com.jald.reserve.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KFinaningProductListAdapter;

public class KFinancingActivity extends KBaseActivity {

    private static final int REQUEST_CODE_ACCOUNT_CHARGE = 11;

    private ScrollView scrollContainer;
    private View btnAccountCharge;
    private View btnMyBankCardManager;
    private ListView financingProductList;

    private KFinaningProductListAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_financing_info);
        this.scrollContainer = (ScrollView) findViewById(R.id.scroll_container);
        this.financingProductList = (ListView) findViewById(R.id.financing_product_list);
        this.productAdapter = new KFinaningProductListAdapter(this);
        this.financingProductList.setAdapter(productAdapter);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.smoothScrollTo(0, 0);
            }
        });
    }


}
