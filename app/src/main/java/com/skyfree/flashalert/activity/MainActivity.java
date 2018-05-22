package com.skyfree.flashalert.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfree.flashalert.R;
import com.skyfree.flashalert.service.FlashServices;
import com.skyfree.flashalert.service.ServiceCheckBattery;
import com.skyfree.flashalert.service.ServiceStartFlash;
import com.skyfree.flashalert.utils.Utils;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener, TimePickerDialog.OnTimeSetListener {

    private Switch mSwFlashAlerts, mSwIncomingCall, mSwIncomingSms, mSwEnableFlashAlert, mSwNormalMode,
            mSwVibrateMode, mSwSilentMode, mSwDisableWhenBattery, mSwDoNotDisturb, mSwDndStart, mSwDndEnd;
    private SeekBar mSbOnLength, mSbOffLength, mSbBlinkTime;
    private Button mBtnTestStart, mBtnTestStop;
    private TextView mTvOnLength, mTvOffLength, mTvBlinkTime, mTvHourMinuteStart, mTvHourMinuteEnd;

    private boolean mCheckFlashAlerts, mCheckIncomingCall, mCheckSms, mCheckEnableFlashAlert, mCheckNormalMode,
            mCheckVibrateMode, mCheckSilentMode, mCheckDisableWhenBattery, mCheckDoNotDisturb, mCheckDndStart, mCheckDndEnd;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharePre;

    private TimePickerDialog mTdpStart;
    private TimePickerDialog mTdpEnd;
    private Calendar mCaNow = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        addEvent();
    }

    private void initView() {
        mTvHourMinuteStart = (TextView) findViewById(R.id.tv_hour_minute_start);
        mTvHourMinuteEnd = (TextView) findViewById(R.id.tv_hour_minute_end);
        mSwFlashAlerts = (Switch) findViewById(R.id.sw_flash_alerts);
        mSwIncomingCall = (Switch) findViewById(R.id.sw_incoming_call);
        mSwIncomingSms = (Switch) findViewById(R.id.sw_incoming_sms);
        mSwEnableFlashAlert = (Switch) findViewById(R.id.sw_enable_flash);
        mSwNormalMode = (Switch) findViewById(R.id.sw_normal_mode);
        mSwVibrateMode = (Switch) findViewById(R.id.sw_vibrate_mode);
        mSwSilentMode = (Switch) findViewById(R.id.sw_silent_mode);
        mSwDisableWhenBattery = (Switch) findViewById(R.id.sw_disable_when_battery_below);
        mSwDoNotDisturb = (Switch) findViewById(R.id.sw_do_not_disturb);
        mSwDndStart = (Switch) findViewById(R.id.sw_dnd_start);
        mSwDndEnd = (Switch) findViewById(R.id.sw_dnd_end);
        mSbOnLength = (SeekBar) findViewById(R.id.sb_onlength);
        mSbOffLength = (SeekBar) findViewById(R.id.sb_off_length);
        mSbBlinkTime = (SeekBar) findViewById(R.id.sb_blink_time);
        mBtnTestStart = (Button) findViewById(R.id.btn_test_start);
        mBtnTestStop = (Button) findViewById(R.id.btn_test_stop);
        mTvOnLength = (TextView) findViewById(R.id.tv_on_length);
        mTvOffLength = (TextView) findViewById(R.id.tv_off_length);
        mTvBlinkTime = (TextView) findViewById(R.id.tv_blink_time);

    }

    private void addEvent() {

        mSharePre = this.getSharedPreferences(Utils.SETTING, MODE_PRIVATE);
        mEditor = mSharePre.edit();
        mSwFlashAlerts.setChecked(mSharePre.getBoolean(Utils.FLASH_ALERT, false));
        mSwIncomingCall.setChecked(mSharePre.getBoolean(Utils.INCOMING_CALL, false));
        mSwIncomingSms.setChecked(mSharePre.getBoolean(Utils.INCOMING_SMS, false));
        mSwEnableFlashAlert.setChecked(mSharePre.getBoolean(Utils.ENABLE_FLASH, false));
        mSwNormalMode.setChecked(mSharePre.getBoolean(Utils.NORMAL_MODE, false));
        mSwVibrateMode.setChecked(mSharePre.getBoolean(Utils.VIBRATE_MODE, false));
        mSwSilentMode.setChecked(mSharePre.getBoolean(Utils.SILENT_MODE, false));
        mSwDisableWhenBattery.setChecked(mSharePre.getBoolean(Utils.DISABLE_WHEN_BATTERY_BELOW, false));
        mSwDndStart.setChecked(mSharePre.getBoolean(Utils.DND_START, false));
        mSwDndEnd.setChecked(mSharePre.getBoolean(Utils.DND_END, false));
        mSwDoNotDisturb.setChecked(mSharePre.getBoolean(Utils.DO_NOT_DISTURB, false));
        mTvOnLength.setText(mSharePre.getInt(Utils.ON_LENGTH, 500) + "ms");
        mTvOffLength.setText(mSharePre.getInt(Utils.OFF_LENGTH, 500) + "ms");
        mTvBlinkTime.setText(mSharePre.getInt(Utils.BLINK_TIME, 5) + getString(R.string.time));
        mSbOnLength.setProgress(mSharePre.getInt(Utils.ON_LENGTH, 500));
        mSbOffLength.setProgress(mSharePre.getInt(Utils.OFF_LENGTH, 500));
        mSbBlinkTime.setProgress(mSharePre.getInt(Utils.BLINK_TIME, 5));
        mTvHourMinuteStart.setText(mSharePre.getString(Utils.START_TIME_STR, ""));
        mTvHourMinuteEnd.setText(mSharePre.getString(Utils.END_TIME_STR, ""));

        mBtnTestStart.setOnClickListener(this);
        mBtnTestStop.setOnClickListener(this);
        mSwFlashAlerts.setOnCheckedChangeListener(this);
        mSwIncomingCall.setOnCheckedChangeListener(this);
        mSwIncomingSms.setOnCheckedChangeListener(this);
        mSwEnableFlashAlert.setOnCheckedChangeListener(this);
        mSwNormalMode.setOnCheckedChangeListener(this);
        mSwVibrateMode.setOnCheckedChangeListener(this);
        mSwSilentMode.setOnCheckedChangeListener(this);
        mSwDisableWhenBattery.setOnCheckedChangeListener(this);
        mSwDoNotDisturb.setOnCheckedChangeListener(this);
        mSwDndStart.setOnCheckedChangeListener(this);
        mSwDndEnd.setOnCheckedChangeListener(this);
        mSbOnLength.setOnSeekBarChangeListener(this);
        mSbOffLength.setOnSeekBarChangeListener(this);
        mSbBlinkTime.setOnSeekBarChangeListener(this);
    }

    private void addPermissionReadPhoneState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, Utils.PERMISSION_READ_PHONE_STATE);
            }
        }
    }

    private void addPermissionSmsReceived() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, Utils.PERMISSION_INCOMING_SMS);
            }
        }
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

    private void addAutoStartup() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utils.PERMISSION_READ_PHONE_STATE:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mSwIncomingCall.setChecked(false);
                }
                break;

            case Utils.PERMISSION_INCOMING_SMS:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mSwIncomingSms.setChecked(false);
                }
                break;

            case Utils.PERMISSION_NOTIFICATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    mSwEnableFlashAlert.setChecked(false);
                }
                break;
        }
    }

    private int mOnLength, mOffLength, mBlinkTime;
    private int mCountBlinkTime = 0;
    private Handler mHanler = new Handler();

    private Runnable mRunnableOnAndOff = new Runnable() {
        @Override
        public void run() {
            startService(new Intent(getApplicationContext(), FlashServices.class));
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mCountBlinkTime += 1;
            if(mCountBlinkTime > mBlinkTime){
                mCountBlinkTime = 0;
                mHanler.removeCallbacks(mRunnable);
                stopService(new Intent(getApplicationContext(), FlashServices.class));
            }else {
                startService(new Intent(getApplicationContext(), FlashServices.class));
                new CountDownTimer(mOnLength, 1){

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        stopService(new Intent(getApplicationContext(), FlashServices.class));
                    }
                }.start();
                mHanler.postDelayed(mRunnable, mOffLength + mOnLength);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_start:
                stopService(new Intent(this, FlashServices.class));
                mHanler.removeCallbacks(mRunnable);
                mCountBlinkTime = 0;
                mOnLength = mSharePre.getInt(Utils.ON_LENGTH, 500);
                mOffLength = mSharePre.getInt(Utils.OFF_LENGTH, 500);
                mBlinkTime = mSharePre.getInt(Utils.BLINK_TIME, 5);
                mHanler.postDelayed(mRunnable, mOffLength + mOnLength);
                break;
            case R.id.btn_test_stop:
                mCountBlinkTime = 0;
                stopService(new Intent(this, FlashServices.class));
                mHanler.removeCallbacks(mRunnable);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, FlashServices.class));
        stopService(new Intent(this, ServiceCheckBattery.class));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_flash_alerts:
                if (isChecked) {
                    if (mSharePre.getInt(Utils.ON_LENGTH, 500) == 0) {
                        Toast.makeText(this, getString(R.string.flash_time_error), Toast.LENGTH_SHORT).show();
                        mSwFlashAlerts.setChecked(false);
                    } else {
                        addAutoStartup();
                        mEditor.putInt(Utils.ON_LENGTH, mSbOnLength.getProgress());
                        mEditor.putInt(Utils.OFF_LENGTH, mSbOffLength.getProgress());
                        mEditor.putInt(Utils.BLINK_TIME, mSbBlinkTime.getProgress());
                        stopService(new Intent(this, ServiceStartFlash.class));
                        startService(new Intent(this, ServiceStartFlash.class));
//                        if(mSwDisableWhenBattery.isChecked()){
//                            startService(new Intent(getApplicationContext(), ServiceCheckBattery.class));
//                        }
                        mEditor.putBoolean(Utils.FLASH_ALERT, true);
                    }
                } else {
                    mEditor.putBoolean(Utils.FLASH_ALERT, false);
                    stopService(new Intent(this, ServiceStartFlash.class));
                    stopService(new Intent(this, ServiceCheckBattery.class));
                }
                mEditor.apply();
                break;
            case R.id.sw_incoming_call:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    addPermissionReadPhoneState();
                    mEditor.putBoolean(Utils.INCOMING_CALL, true);
                } else {
                    mSwNormalMode.setChecked(false);
                    mSwVibrateMode.setChecked(false);
                    mSwSilentMode.setChecked(false);
                    mEditor.putBoolean(Utils.INCOMING_CALL, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_incoming_sms:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    addPermissionSmsReceived();
                    mEditor.putBoolean(Utils.INCOMING_SMS, true);
                } else {
                    mEditor.putBoolean(Utils.INCOMING_SMS, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_enable_flash:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    mEditor.putBoolean(Utils.ENABLE_FLASH, true);
                    startActivity(new Intent(this, NotificationList.class));
                } else {
                    mEditor.putBoolean(Utils.ENABLE_FLASH, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_normal_mode:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    mSwVibrateMode.setChecked(false);
                    mSwSilentMode.setChecked(false);
                    mEditor.putBoolean(Utils.NORMAL_MODE, true);
                    mEditor.putString(Utils.TYPE_CALL, Utils.NORMAL_CALL);
                } else {
                    mEditor.putString(Utils.TYPE_CALL, Utils.NORMAL_CALL);
                    mEditor.putBoolean(Utils.NORMAL_MODE, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_vibrate_mode:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    mSwNormalMode.setChecked(false);
                    mSwSilentMode.setChecked(false);
                    mEditor.putBoolean(Utils.VIBRATE_MODE, true);
                    mEditor.putString(Utils.TYPE_CALL, Utils.VIBRATE_CALL);
                } else {
                    mEditor.putString(Utils.TYPE_CALL, Utils.NORMAL_CALL);
                    mEditor.putBoolean(Utils.VIBRATE_MODE, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_silent_mode:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    mSwNormalMode.setChecked(false);
                    mSwVibrateMode.setChecked(false);
                    mEditor.putBoolean(Utils.SILENT_MODE, true);
                    mEditor.putString(Utils.TYPE_CALL, Utils.SILENT_CALL);
                } else {
                    mEditor.putString(Utils.TYPE_CALL, Utils.NORMAL_CALL);
                    mEditor.putBoolean(Utils.SILENT_MODE, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_disable_when_battery_below:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    AlertDialog.Builder dialogBuiler = new AlertDialog.Builder(this);
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_battery_below, null);
                    dialogBuiler.setView(dialogView);

                    final TextView mTvBatteryBelow = (TextView) dialogView.findViewById(R.id.tv_battery_below);
                    TextView mTvCancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
                    TextView mTvSave = (TextView) dialogView.findViewById(R.id.tv_save);
                    final SeekBar mSbBattery = (SeekBar) dialogView.findViewById(R.id.sb_battery_below);

                    final AlertDialog alertStartDialog = dialogBuiler.create();
                    alertStartDialog.show();

                    mSbBattery.setProgress(mSharePre.getInt(Utils.DISABLE_WHEN_BATTERY_BELOW_PERCENT, 0));
                    mTvBatteryBelow.setText(mSharePre.getInt(Utils.DISABLE_WHEN_BATTERY_BELOW_PERCENT, 0) + "%");

                    mSbBattery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            mTvBatteryBelow.setText(progress + "%");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    mTvSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEditor.putBoolean(Utils.DISABLE_WHEN_BATTERY_BELOW, true);
                            mEditor.putInt(Utils.DISABLE_WHEN_BATTERY_BELOW_PERCENT, mSbBattery.getProgress());
                            mEditor.apply();
                            alertStartDialog.cancel();
                        }
                    });

                    mTvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertStartDialog.cancel();
                            mEditor.putBoolean(Utils.DISABLE_WHEN_BATTERY_BELOW, false);
                            mEditor.apply();
                            mSwDisableWhenBattery.setChecked(false);
                        }
                    });
                } else {
                    stopService(new Intent(getApplicationContext(), ServiceCheckBattery.class));
                    mEditor.putBoolean(Utils.DISABLE_WHEN_BATTERY_BELOW, false);
                    mEditor.apply();
                }
                mEditor.apply();
                break;
            case R.id.sw_do_not_disturb:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    if (mSwDndStart.isChecked() && mSwDndEnd.isChecked()) {
                        mEditor.putBoolean(Utils.DO_NOT_DISTURB, true);
                    } else {
                        Toast.makeText(this, getString(R.string.please_reinstall_time), Toast.LENGTH_SHORT).show();
                        mSwDoNotDisturb.setChecked(false);
                        mEditor.putBoolean(Utils.DO_NOT_DISTURB, false);
                    }
                } else {
                    mEditor.putBoolean(Utils.DO_NOT_DISTURB, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_dnd_start:
                mSwFlashAlerts.setChecked(false);
                if (isChecked) {
                    Utils.TYPE = Utils.ON;
                    mTdpStart = TimePickerDialog.newInstance(this, mCaNow.get(Calendar.HOUR), mCaNow.get(Calendar.MINUTE), true);
                    mTdpStart.setVersion(TimePickerDialog.Version.VERSION_2);
                    mTdpStart.show(getFragmentManager(), Utils.TIME_PICKER_START);
                    mEditor.putBoolean(Utils.DND_START, true);
                } else {
                    mEditor.putBoolean(Utils.DND_START, false);
                }
                mEditor.apply();
                break;
            case R.id.sw_dnd_end:
                if (isChecked) {
                    Utils.TYPE = Utils.OFF;
                    mTdpEnd = TimePickerDialog.newInstance(this, mCaNow.get(Calendar.HOUR), mCaNow.get(Calendar.MINUTE), true);
                    mTdpEnd.setTitle(getString(R.string.dnd_end));
                    mTdpEnd.show(getFragmentManager(), Utils.TIME_PICKER_END);
                    mEditor.putBoolean(Utils.DND_END, true);
                } else {
                    mEditor.putBoolean(Utils.DND_END, false);
                }
                mEditor.apply();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTdpStart = (TimePickerDialog) getFragmentManager().findFragmentByTag(Utils.TIME_PICKER_START);
        if (mTdpStart != null) mTdpStart.setOnTimeSetListener(this);
        mTdpEnd = (TimePickerDialog) getFragmentManager().findFragmentByTag(Utils.TIME_PICKER_END);
        if (mTdpEnd != null) mTdpEnd.setOnTimeSetListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_onlength:
                mTvOnLength.setText(progress + "ms");
                mEditor.putInt(Utils.ON_LENGTH, progress);
                mEditor.apply();
                break;
            case R.id.sb_off_length:
                mTvOffLength.setText(progress + "ms");
                mEditor.putInt(Utils.OFF_LENGTH, progress);
                mEditor.apply();
                break;
            case R.id.sb_blink_time:
                mTvBlinkTime.setText(progress + getString(R.string.time));
                mEditor.putInt(Utils.BLINK_TIME, progress);
                mEditor.apply();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (Utils.TYPE.equals(Utils.ON)) {
            mTvHourMinuteStart.setText(hourOfDay + "h - " + minute + "m");
            mEditor.putInt(Utils.START_HOUR, hourOfDay);
            mEditor.putInt(Utils.START_MINUTE, minute);
            mEditor.putString(Utils.START_TIME_STR, mTvHourMinuteStart.getText().toString());
            mEditor.apply();
        } else if (Utils.TYPE.equals(Utils.OFF)) {
            mTvHourMinuteEnd.setText(hourOfDay + "h - " + minute + "m");
            mEditor.putInt(Utils.END_HOUR, hourOfDay);
            mEditor.putInt(Utils.END_MINUTE, minute);
            mEditor.putString(Utils.END_TIME_STR, mTvHourMinuteEnd.getText().toString());
            mEditor.apply();
        }
    }
}
