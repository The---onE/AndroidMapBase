package com.xmx.androidmapbase.common.map.amap.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.common.map.amap.utils.SensorEventHelper;

import org.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_amap)
public abstract class BaseLocationDirectionActivity extends BaseLocationActivity {

    protected int strokeColor = Color.argb(180, 3, 145, 255); //精度边缘颜色
    protected int fillColor = Color.argb(64, 128, 192, 192); //精度填充颜色
    protected String markerFlag = "myLocation";

    private boolean mFirstFix = false; //是否已将图标添加到地图
    private Marker mLocMarker; //定位点
    private Circle mCircle; //精度圆
    private SensorEventHelper mSensorHelper; //用于获取指南针方向

    protected abstract void whenFirstLocation(AMapLocation aMapLocation);
    protected abstract void whenNewLocation(AMapLocation aMapLocation);

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);

        mSensorHelper = new SensorEventHelper(this);
        mSensorHelper.registerSensorListener();
    }

    @Override
    protected void whenLocationChanged(AMapLocation aMapLocation) {
        if (!mFirstFix) {
            mFirstFix = true;
            addCircle(mLocation, aMapLocation.getAccuracy());//添加定位精度圆
            addMarker(mLocation);//添加定位图标
            mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
            whenFirstLocation(aMapLocation);
        } else {
            mCircle.setCenter(mLocation); //精度中心为定位点
            mCircle.setRadius(aMapLocation.getAccuracy()); //精度半径
            mLocMarker.setPosition(mLocation); //定位点
            whenNewLocation(aMapLocation);
        }
    }

    //添加精度圆
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(fillColor);
        options.strokeColor(strokeColor);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    //添加定位点
    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = mAMap.addMarker(options);
        mLocMarker.setTitle(markerFlag);
    }
}
