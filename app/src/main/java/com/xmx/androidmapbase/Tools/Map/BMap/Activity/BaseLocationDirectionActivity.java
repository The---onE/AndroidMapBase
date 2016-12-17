package com.xmx.androidmapbase.Tools.Map.BMap.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.BMap.Utils.SensorEventHelper;

import org.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_amap)
public abstract class BaseLocationDirectionActivity extends BaseLocationActivity {

    //protected int strokeColor = Color.argb(180, 3, 145, 255); //精度边缘颜色
    protected int fillColor = Color.argb(64, 128, 192, 192); //精度填充颜色
    protected final static String LOCATION_TITLE = "myLocation";

    private boolean mFirstFix = false; //是否已将图标添加到地图
    private Marker mLocMarker; //定位点
    private Circle mCircle; //精度圆
    private SensorEventHelper mSensorHelper; //用于获取指南针方向

    protected abstract void whenFirstLocation(BDLocation bdLocation);

    protected abstract void whenNewLocation(BDLocation bdLocation);

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);

        mSensorHelper = new SensorEventHelper(this);
        mSensorHelper.registerSensorListener();
    }

    @Override
    protected void whenLocationChanged(BDLocation bdLocation) {
        if (!mFirstFix) {
            mFirstFix = true;
            addCircle(mLocation, (int) bdLocation.getRadius());//添加定位精度圆
            addMarker(mLocation);//添加定位图标
            mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
            whenFirstLocation(bdLocation);
        } else {
            mCircle.setCenter(mLocation); //精度中心为定位点
            mCircle.setRadius((int) bdLocation.getRadius()); //精度半径
            mLocMarker.setPosition(mLocation); //定位点
            whenNewLocation(bdLocation);
        }
    }

    //添加精度圆
    private void addCircle(LatLng latlng, int radius) {
        CircleOptions options = new CircleOptions();
        //options.strokeWidth(1f);
        options.fillColor(fillColor);
        //options.strokeColor(strokeColor);
        options.center(latlng);
        options.radius(radius);
        mCircle = (Circle) mBMap.addOverlay(options);
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
        mLocMarker = (Marker) mBMap.addOverlay(options);
        mLocMarker.setTitle(LOCATION_TITLE);
    }
}
