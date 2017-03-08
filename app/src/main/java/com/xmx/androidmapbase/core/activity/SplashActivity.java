package com.xmx.androidmapbase.core.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.xmx.androidmapbase.core.Constants;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.base.activity.BaseSplashActivity;
import com.xmx.androidmapbase.utils.Timer;

public class SplashActivity extends BaseSplashActivity {

    Timer timer;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.stop();
                timer.execute();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        timer = new Timer() {
            @Override
            public void timer() {

                startMainActivity();
            }
        };
        timer.start(Constants.SPLASH_TIME, true);
    }
}