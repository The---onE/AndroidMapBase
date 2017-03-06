package com.xmx.androidmapbase.core.activity;

import android.os.Bundle;
import android.os.Handler;

import com.xmx.androidmapbase.core.Constants;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.base.activity.BaseSplashActivity;

public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, Constants.SPLASH_TIME);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}