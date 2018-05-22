package com.skyfree.flashalert.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.skyfree.flashalert.R;
import com.skyfree.flashalert.utils.FlashLight;
import com.skyfree.flashalert.utils.Utils;

import java.util.Calendar;

/**
 * Created by KienBeu on 5/10/2018.
 */

public class FlashServices extends Service {

    private SharedPreferences mSharePre;

    FlashLight flashLight = new FlashLight();

    public FlashServices() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        mSharePre = this.getSharedPreferences(Utils.SETTING, MODE_PRIVATE);
        try {
            FlashLight.flashOn(getApplicationContext());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        try {
            FlashLight.flashOff();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}