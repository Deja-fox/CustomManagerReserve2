package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jald.reserve.util.KAppManager;

public class KBaseToolBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KAppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KAppManager.getAppManager().finishActivity(this);
    }
}
