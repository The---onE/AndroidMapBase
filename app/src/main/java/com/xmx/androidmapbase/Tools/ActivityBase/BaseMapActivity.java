package com.xmx.androidmapbase.Tools.ActivityBase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.xmx.androidmapbase.Application;
import com.xmx.androidmapbase.Constants;
import com.xmx.androidmapbase.Tools.OperationLog.OperationLogEntityManager;

import org.xutils.x;

/**
 * Created by The_onE on 2015/12/27.
 */
public abstract class BaseMapActivity extends BaseActivity {

    protected AMap mAMap;
    protected MapView mMapView;

    protected LatLng mLocation; //当前位置
    public static final float DEFAULT_SCALE = 15; //默认缩放比例

    protected abstract void getMapView();
    protected abstract void whenResume();
    protected abstract void whenPause();
    protected abstract void whenDestroy();

    @Override
    protected void initView(Bundle savedInstanceState) {
        getMapView();
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
    }

    protected void focusLocation() {
        focusLocation(mLocation, DEFAULT_SCALE);
    }

    protected void focusLocation(float scale) {
        focusLocation(mLocation, scale);
    }

    //聚焦定位点，缩放比越大放大程度越高
    protected void focusLocation(LatLng location, float scale) {
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, scale));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
