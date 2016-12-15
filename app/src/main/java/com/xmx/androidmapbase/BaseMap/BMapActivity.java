package com.xmx.androidmapbase.BaseMap;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.ActivityBase.BaseActivity;
import com.xmx.androidmapbase.Tools.Map.BMap.Activity.BaseMapActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_bmap)
public class BMapActivity extends BaseMapActivity {

    @Override
    protected void getMapView() {
        mMapView = getViewById(R.id.bmapView);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBMap.setTrafficEnabled(true);
    }

    @Override
    protected void whenResume() {

    }

    @Override
    protected void whenPause() {

    }

    @Override
    protected void whenDestroy() {

    }
}
