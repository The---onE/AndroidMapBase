package com.xmx.androidmapbase.common.map.amap.activity;

import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;

public abstract class BaseLocationActivity extends BaseMapActivity implements LocationSource {

    protected LatLng mLocation; //当前位置
    protected String mCity; //当前城市
    protected String mCityCode; //当前城市

    protected boolean backgroundLocation = false; //是否后台继续定位

    private AMapLocationClient mLocationClient; //定位器
    private OnLocationChangedListener mListener; //定位监听器
    private boolean initFlag = false;

    protected abstract void whenLocationChanged(AMapLocation aMapLocation);
    protected abstract void whenLocationError(int errorCode, String errorInfo);
    protected abstract void setLocationOption(AMapLocationClientOption locationOption);

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mAMap.setLocationSource(this);//设置定位监听
        mAMap.setMyLocationEnabled(true);//设置为true表示可触发定位
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (mListener != null && aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            mLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                            mCity = aMapLocation.getCity();
                            mCityCode = aMapLocation.getCityCode();
                            whenLocationChanged(aMapLocation);
                        } else {
                            whenLocationError(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
                        }
                    }
                }
            });
            setLocationOption(locationOption);
            //设置定位参数
            mLocationClient.setLocationOption(locationOption);
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
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
        deactivate();
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
            mLocationClient.startLocation();
            return true;
        } else {
            initFlag = true;
            return false;
        }
    }

    protected boolean stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            return true;
        } else {
            return false;
        }
    }
}
