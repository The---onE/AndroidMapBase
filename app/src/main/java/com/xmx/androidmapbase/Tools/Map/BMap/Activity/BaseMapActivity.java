package com.xmx.androidmapbase.Tools.Map.BMap.Activity;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.xmx.androidmapbase.Tools.ActivityBase.BaseActivity;

/**
 * Created by The_onE on 2015/12/27.
 */
public abstract class BaseMapActivity extends BaseActivity {

    protected MapView mMapView;
    protected BaiduMap mBMap;
    public static final float DEFAULT_SCALE = 17; //默认缩放比例

    protected abstract void getMapView();
    protected abstract void whenResume();
    protected abstract void whenPause();
    protected abstract void whenDestroy();

    @Override
    protected void initView(Bundle savedInstanceState) {
        getMapView();
        mMapView.onCreate(this, savedInstanceState);
        if (mBMap == null) {
            mBMap = mMapView.getMap();
        }
    }

    protected void focusLocation(LatLng location) {
        focusLocation(location, DEFAULT_SCALE);
    }

    //聚焦定位点，缩放比越大放大程度越高，范围3-21
    protected void focusLocation(LatLng location, float scale) {
        MapStatusUpdate mapStatusUpdate
                = MapStatusUpdateFactory.zoomTo(scale);
        mBMap.setMapStatus(mapStatusUpdate);
        mapStatusUpdate
                = MapStatusUpdateFactory.newLatLng(location);
        mBMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        whenResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        whenPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        whenDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
