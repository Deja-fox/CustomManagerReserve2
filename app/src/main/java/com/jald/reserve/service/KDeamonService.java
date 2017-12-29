package com.jald.reserve.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class KDeamonService extends Service {

    public static final String ACTION_START_SERVICE = "com.jald.daifupay.intent.StartDeamonService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(ACTION_START_SERVICE);
        startService(intent);
        super.onDestroy();
    }

}
