package com.xmx.androidmapbase;

import android.app.Activity;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.SDKInitializer;
import com.xmx.androidmapbase.Tools.CrashHandler;
import com.xmx.androidmapbase.Tools.Data.DataManager;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POIManager;
import com.xmx.androidmapbase.User.UserManager;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by The_onE on 2016/1/3.
 */
public class Application extends android.app.Application {

    private static Application instance;
    public static Application getInstance() {
        return instance;
    }

    private List<Activity> activityList = new LinkedList<>();

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        AVOSCloud.initialize(this, Constants.APP_ID, Constants.APP_KEY);

        if (Constants.EXCEPTION_DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }

        AVInstallation.getCurrentInstallation().saveInBackground();
        SDKInitializer.initialize(this);

        UserManager.getInstance().setContext(this);

        POIManager.getInstance().setContext(this);

        DataManager.getInstance().setContext(this);
    }
}
