package com.example.nusaraya.overlaybutton;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.nusaraya.overlaybutton.Service.OverlayShowingService;

/**
 * Created by nusaraya on 6/15/2016.
 */

public class Main extends Activity {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 4545;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent svc = new Intent(this, OverlayShowingService.class);
        startService(svc);
        finish();
    }

    @Override
    public void onBackPressed(){}
}
