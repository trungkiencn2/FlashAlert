package com.skyfree.flashalert.utils;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;

import com.noob.lumberjack.LogLevel;
import com.noob.noobcameraflash.managers.NoobCameraManager;

/**
 * Created by KienBeu on 5/10/2018.
 */

public class FlashLight {
    public static void flashOn(Context context) throws CameraAccessException {
        NoobCameraManager.getInstance().init(context, LogLevel.Verbose);
        NoobCameraManager.getInstance().turnOnFlash();
    }


    public static void flashOff() throws CameraAccessException {
        NoobCameraManager.getInstance().turnOffFlash();
    }
}
