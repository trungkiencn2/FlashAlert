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

import com.skyfree.flashalert.receiver.IncomingCallAndSMS;
import com.skyfree.flashalert.utils.Utils;

/**
 * Created by KienBeu on 5/11/2018.
 */

public class ServiceStartFlash extends Service {

    private IncomingCallAndSMS mReceiverCallOrSMS;
    private IntentFilter mFilter;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharePre;
    private int mStartHour, mStartMinute, mEndHour, mEndMinute;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSharePre = this.getSharedPreferences(Utils.SETTING, MODE_PRIVATE);
        mEditor = mSharePre.edit();
        mStartHour = mSharePre.getInt(Utils.START_HOUR, 0);
        mStartMinute = mSharePre.getInt(Utils.START_MINUTE, 0);
        mEndHour = mSharePre.getInt(Utils.END_HOUR, 0);
        mStartMinute = mSharePre.getInt(Utils.END_MINUTE, 0);
        mReceiverCallOrSMS = new IncomingCallAndSMS();
        mFilter = new IntentFilter();
        mFilter.addAction(Utils.PHONE_STATE);
        mFilter.addAction(Utils.NEW_OUTGOING_CALL);
        mFilter.addAction(Utils.SMS_RECEIVED);
        if(mSharePre.getBoolean(Utils.FLASH_ALERT, false)){
            registReceiverForCallOrSMS();
        }
//        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return START_STICKY;
    }

    private void registReceiverForCallOrSMS() {
        registerReceiver(mReceiverCallOrSMS, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), ServiceStartFlash.class));
        if(mReceiverCallOrSMS != null){
            unregisterReceiver(mReceiverCallOrSMS);
            mReceiverCallOrSMS = null;
        }
    }

}
