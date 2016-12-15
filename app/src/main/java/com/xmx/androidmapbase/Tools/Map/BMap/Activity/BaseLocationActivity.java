package com.xmx.androidmapbase.Tools.Map.BMap.Activity;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;

public abstract class BaseLocationActivity extends BaseMapActivity {

    protected LatLng mLocation; //当前位置
    protected String mCity; //当前城市
    protected String mCityCode; //当前城市

    protected LocationClient mLocationClient = null;
    protected boolean backgroundLocation = false; //是否后台继续定位

    private boolean initFlag = false;

    protected abstract void whenLocationChanged(BDLocation bdLocation);
    protected abstract void whenLocationError(int errorCode);
    protected abstract void setLocationOption(LocationClientOption option);

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null) {
                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                            || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation
                            || bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                        mLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                        mCity = bdLocation.getCity();
                        mCityCode = bdLocation.getCityCode();
                        whenLocationChanged(bdLocation);
                    } else {
                        whenLocationError(bdLocation.getLocType());
                    }
                }
            }
        });    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        setLocationOption(option);
        //设置定位参数
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!backgroundLocation) {
            startLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!backgroundLocation) {
            stopLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void focusLocation() {
        if (mLocation != null) {
            focusLocation(mLocation, DEFAULT_SCALE);
        }
    }

    protected void focusLocation(float scale) {
        if (mLocation != null) {
            focusLocation(mLocation, scale);
        }
    }

    protected boolean startLocation() {
        if (initFlag && mLocationClient != null) {
            mLocationClient.start();
            return true;
        } else {
            initFlag = true;
            return false;
        }
    }

    protected boolean stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            return true;
        } else {
            return false;
        }
    }
}
