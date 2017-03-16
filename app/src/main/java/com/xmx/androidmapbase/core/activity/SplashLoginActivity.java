package com.xmx.androidmapbase.core.activity;

import android.os.Bundle;
import android.os.Handler;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.common.user.UserData;
import com.xmx.androidmapbase.core.Constants;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.base.activity.BaseSplashActivity;
import com.xmx.androidmapbase.common.user.callback.AutoLoginCallback;
import com.xmx.androidmapbase.common.user.UserConstants;
import com.xmx.androidmapbase.common.user.UserManager;
import com.xmx.androidmapbase.utils.ExceptionUtil;

public class SplashLoginActivity extends BaseSplashActivity {
    boolean loginFlag = false;
    boolean timeFlag = false;
    boolean notLoginFlag = false;
    boolean startFlag = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash_login);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timeFlag = true;
                if (!startFlag) {
                    if (loginFlag) {
                        startMainActivity();
                    }
                    if (notLoginFlag) {
                        startLoginActivity();
                    }
                }
            }
        }, Constants.SPLASH_TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!startFlag) {
                    if (loginFlag) {
                        startMainActivity();
                    } else {
                        startLoginActivity();
                    }
                }
            }
        }, Constants.LONGEST_SPLASH_TIME);


        UserManager.getInstance().autoLogin(new AutoLoginCallback() {
            @Override
            public void success(UserData user) {
                loginFlag = true;
                if (timeFlag) {
                    startMainActivity();
                }
            }

            @Override
            public void error(AVException e) {
                showToast(R.string.network_error);
                ExceptionUtil.normalException(e, getBaseContext());
                notLoginFlag = true;
                if (timeFlag) {
                    startLoginActivity();
                }
            }

            @Override
            public void error(int error) {
                switch (error) {
                    case UserConstants.NOT_LOGGED_IN:
                        notLoginFlag = true;
                        if (timeFlag) {
                            startLoginActivity();
                        }
                        break;
                    case UserConstants.USERNAME_ERROR:
                        notLoginFlag = true;
                        if (timeFlag) {
                            startLoginActivity();
                        }
                        break;
                    case UserConstants.CHECKSUM_ERROR:
                        notLoginFlag = true;
                        if (timeFlag) {
                            startLoginActivity();
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}