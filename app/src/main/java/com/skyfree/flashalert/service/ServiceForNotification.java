package com.skyfree.flashalert.service;

//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.service.notification.NotificationListenerService;
//import android.service.notification.StatusBarNotification;
//import android.support.annotation.RequiresApi;
//import android.support.v4.content.LocalBroadcastManager;
//
//import com.skyfree.flashalert.activity.NotificationList;
//import com.skyfree.flashalert.db.Database;
//import com.skyfree.flashalert.db.Pack;
//import com.skyfree.flashalert.model.Notification;
//import com.skyfree.flashalert.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import com.skyfree.flashalert.db.Database;
import com.skyfree.flashalert.db.Pack;
import com.skyfree.flashalert.receiver.IncomingCallAndSMS;

import java.util.ArrayList;

/**
 * Created by KienBeu on 5/16/2018.
 */

public class ServiceForNotification extends NotificationListenerService {

    private Context context;
    private boolean check = true;
    private static int ID_NOTIFICATION = 001;
    private ArrayList<Pack> mListPack;
    private Database mDb;
    private IncomingCallAndSMS mReceiverCallOrSMS;
    private IntentFilter mFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mReceiverCallOrSMS = new IncomingCallAndSMS();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        mDb = new Database(context);
        mListPack = new ArrayList<>();
        mListPack.addAll(mDb.getListPack());

        String pack = sbn.getPackageName();
        if(mListPack.size() > 0){
            String pk = mListPack.get(0).getPack();
        }
        Toast.makeText(context, "" + pack, Toast.LENGTH_SHORT).show();
        for(int i = 0; i<mListPack.size(); i++){
            if(pack.equals(mListPack.get(i).getPack())){
                Intent inten = new Intent("notify");
                sendBroadcast(inten);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

}
