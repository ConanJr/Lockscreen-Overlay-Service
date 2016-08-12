package com.example.nusaraya.overlaybutton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.nusaraya.overlaybutton.Service.LockScreenService;
import com.example.nusaraya.overlaybutton.Util.PreferenceUtil;

public class LaunchActivity extends Activity {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    private final Intent intent = new Intent();
    private Context mContext = null;
    Switch mSwitch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Intent intentPermission;
        mSwitch = (Switch) findViewById(R.id.switch_locksetting);

        mContext = this;
        PreferenceUtil.init(mContext);
        boolean lockState = PreferenceUtil.get(PreferenceUtil.ISLOCK);
        if (lockState) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                intentPermission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intentPermission, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                SwitchListener();
            }
        } else {
            SwitchListener();
        }
    }

    private void SwitchListener(){
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /**
                     * start lock screen service
                     */
                    PreferenceUtil.setBoolean(PreferenceUtil.ISLOCK, true);
                    intent.setClass(LaunchActivity.this, LockScreenService.class);
                    startService(intent);
                    finish();
                } else {
                    /**
                     * end lock screen service
                     */
                    PreferenceUtil.setBoolean(PreferenceUtil.ISLOCK, false);
                    intent.setClass(LaunchActivity.this, LockScreenService.class);
                    stopService(intent);
                    finish();
                }
            }
        });
    }
}
