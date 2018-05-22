package com.skyfree.flashalert.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.skyfree.flashalert.R;
import com.skyfree.flashalert.adapter.AppAdapter;
import com.skyfree.flashalert.db.Database;
import com.skyfree.flashalert.db.Pack;
import com.skyfree.flashalert.service.FlashServices;

import java.util.ArrayList;
import java.util.List;

public class NotificationList extends AppCompatActivity implements View.OnClickListener {

    private PackageManager packageManager;
    private ArrayList<ApplicationInfo> appList;
    private ArrayList<Pack> mListPackage;
    private AppAdapter listAdapter;
    private ImageView mImgBackBoost;
    private ListView mLvApp;
    private Database mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        initView();
        askForPermission();
        addEvent();
    }

    private void addEvent() {
        mListPackage = new ArrayList<>();
        packageManager = getPackageManager();
        appList = new ArrayList<>();
        new LoadApplications().execute();
        mDb = new Database(getApplicationContext());
        mListPackage.addAll(mDb.getListPack());
    }

    private void initView() {
        mLvApp = (ListView) findViewById(R.id.lv_app);
        mImgBackBoost = (ImageView) findViewById(R.id.img_back);
        mImgBackBoost.setOnClickListener(this);
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appList;
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            boolean weHaveNotificationListenerPermission = false;
            for (String service : NotificationManagerCompat.getEnabledListenerPackages(this)) {
                if (service.equals(getPackageName()))
                    weHaveNotificationListenerPermission = true;
            }
            if (!weHaveNotificationListenerPermission) {        //ask for permission
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            appList.addAll(checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA)));
            listAdapter = new AppAdapter(getApplicationContext(), packageManager, appList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mLvApp.setAdapter(listAdapter);
            super.onPostExecute(aVoid);
        }
    }
}
