package com.skyfree.flashalert.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.skyfree.flashalert.service.FlashServices;
import com.skyfree.flashalert.service.ServiceStartFlash;
import com.skyfree.flashalert.service.ServiceCheckBattery;
import com.skyfree.flashalert.utils.Utils;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by KienBeu on 5/10/2018.
 */

public class IncomingCallAndSMS extends BroadcastReceiver {

    private Context mContext;
    private Intent mIntent;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharePre;
    private boolean mDoNotDisturb = false;
    private int mStartHour, mStartMinute, mEndHour, mEndMinute, mDistanceTime;
    private Calendar mCaNow = Calendar.getInstance();
    private Calendar mCaStartTest = Calendar.getInstance();
    private Calendar mCaEndTest = Calendar.getInstance();

    private int mOnLength, mOffLength, mBlinkTime;
    private int mCountBlinkTime = 0;
    private Handler mHanler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mCountBlinkTime += 1;
            if (mCountBlinkTime > mBlinkTime) {
                mCountBlinkTime = 0;
                mHanler.removeCallbacks(mRunnable);
                mContext.stopService(new Intent(mContext, FlashServices.class));
            } else {
                mContext.startService(new Intent(mContext, FlashServices.class));
                new CountDownTimer(mOnLength, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        mContext.stopService(new Intent(mContext, FlashServices.class));
                    }
                }.start();
                mHanler.postDelayed(mRunnable, mOffLength + mOnLength);
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        mSharePre = context.getSharedPreferences(Utils.SETTING, MODE_PRIVATE);
        mEditor = mSharePre.edit();
        mDoNotDisturb = mSharePre.getBoolean(Utils.DO_NOT_DISTURB, false);
        mStartHour = mSharePre.getInt(Utils.START_HOUR, 0);
        mStartMinute = mSharePre.getInt(Utils.START_MINUTE, 0);
        mEndHour = mSharePre.getInt(Utils.END_HOUR, 0);
        mEndMinute = mSharePre.getInt(Utils.END_MINUTE, 0);
        mOnLength = mSharePre.getInt(Utils.ON_LENGTH, 500);
        mOffLength = mSharePre.getInt(Utils.OFF_LENGTH, 500);
        mBlinkTime = mSharePre.getInt(Utils.BLINK_TIME, 5);
        doFlash(context, intent);
    }

    private void doFlash(Context context, Intent intent) {
        if (intent.getAction().equals(Utils.PHONE_STATE) || intent.getAction().equals(Utils.NEW_OUTGOING_CALL)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int events = PhoneStateListener.LISTEN_CALL_STATE;
            tm.listen(phoneStateListener, events);
        } else if (intent.getAction().equals(Utils.SMS_RECEIVED)) {
            mContext.stopService(new Intent(mContext, FlashServices.class));
            mContext.stopService(new Intent(mContext, ServiceStartFlash.class));
            flash();
        } else if(intent.getAction().equals("notify")){
            mContext.stopService(new Intent(mContext, FlashServices.class));
            mContext.stopService(new Intent(mContext, ServiceStartFlash.class));
            flash();
        }
    }

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mContext.stopService(new Intent(mContext, FlashServices.class));
                    mContext.stopService(new Intent(mContext, ServiceStartFlash.class));
                    mCountBlinkTime = 0;
                    mContext.stopService(new Intent(mContext, ServiceCheckBattery.class));
                    mHanler.removeCallbacks(mRunnable);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    mContext.stopService(new Intent(mContext, FlashServices.class));
                    AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    switch (audio.getRingerMode()) {
                        case AudioManager.RINGER_MODE_NORMAL:
                            if (mSharePre.getString(Utils.TYPE_CALL, Utils.NORMAL_CALL).equals(Utils.NORMAL_CALL)) {
                                flash();
                            }
                            break;
                        case AudioManager.RINGER_MODE_SILENT:
                            if (mSharePre.getString(Utils.TYPE_CALL, Utils.SILENT_CALL).equals(Utils.SILENT_CALL)) {
                                flash();
                            }
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            if (mSharePre.getString(Utils.TYPE_CALL, Utils.VIBRATE_CALL).equals(Utils.VIBRATE_CALL)) {
                                flash();
                            }
                            break;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    mContext.stopService(new Intent(mContext, FlashServices.class));
                    mContext.stopService(new Intent(mContext, ServiceStartFlash.class));
                    mCountBlinkTime = 0;
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private void flash() {
        if(!mSharePre.getBoolean(Utils.PIN_YEU, false)){
            if (mDoNotDisturb) {
                if (mStartHour > mEndHour) {
                    reverse();
                } else if (mStartHour == mEndHour) {
                    if (mStartMinute > mEndMinute) {
                        reverse();
                    } else if (mStartMinute == mEndMinute) {
                        mContext.startService(new Intent(mContext, FlashServices.class));
                    } else {
                        notReverse();
                    }
                } else {
                    notReverse();
                }
            } else {
                mHanler.postDelayed(mRunnable, mOffLength + mOnLength);
            }
        }
    }

    private void reverse() {
        mCaStartTest.set(Calendar.HOUR_OF_DAY, mEndHour);
        mCaStartTest.set(Calendar.MINUTE, mEndMinute);
        mCaEndTest.set(Calendar.HOUR_OF_DAY, mStartHour);
        mCaEndTest.set(Calendar.MINUTE, mStartMinute);
        if (mCaEndTest.getTimeInMillis() - mCaNow.getTimeInMillis() > 0
                && mCaEndTest.getTimeInMillis() - mCaNow.getTimeInMillis()
                < mCaEndTest.getTimeInMillis() - mCaStartTest.getTimeInMillis()) {
            mContext.startService(new Intent(mContext, FlashServices.class));
        }
    }

    private void notReverse() {
        mCaStartTest.set(Calendar.HOUR_OF_DAY, mStartHour);
        mCaStartTest.set(Calendar.MINUTE, mStartMinute);
        mCaEndTest.set(Calendar.HOUR_OF_DAY, mEndHour);
        mCaEndTest.set(Calendar.MINUTE, mEndMinute);
        if (mCaEndTest.getTimeInMillis() - mCaNow.getTimeInMillis() < 0
                || mCaEndTest.getTimeInMillis() - mCaNow.getTimeInMillis()
                > mCaEndTest.getTimeInMillis() - mCaStartTest.getTimeInMillis()) {
            mContext.startService(new Intent(mContext, FlashServices.class));
        }
    }
}


