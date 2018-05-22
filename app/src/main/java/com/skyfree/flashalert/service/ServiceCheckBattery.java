package com.skyfree.flashalert.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.skyfree.flashalert.utils.Utils;

/**
 * Created by KienBeu on 5/16/2018.
 */

public class ServiceCheckBattery extends Service {

    private int level, mBatteryBelow;
    private SharedPreferences mSharePre;
    private SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSharePre = this.getSharedPreferences(Utils.SETTING, MODE_PRIVATE);
        mEditor = mSharePre.edit();
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(mBatInfoReceiver != null){
            unregisterReceiver(mBatInfoReceiver);
            mBatInfoReceiver = null;
        }
        super.onDestroy();
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mBatteryBelow = mSharePre.getInt(Utils.DISABLE_WHEN_BATTERY_BELOW_PERCENT, 10);
            if (level <= mBatteryBelow) {
                Toast.makeText(ctxt, "Turn off", Toast.LENGTH_SHORT).show();
                mEditor.putBoolean(Utils.PIN_YEU, true);
                stopService(new Intent(getApplicationContext(), ServiceStartFlash.class));
            } else {
                mEditor.putBoolean(Utils.PIN_YEU, false);
            }
            Toast.makeText(ctxt, "" + level, Toast.LENGTH_SHORT).show();
            mEditor.apply();
        }
    };
}