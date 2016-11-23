package com.xmx.androidmapbase.BaseMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.ActivityBase.BaseActivity;
import com.xmx.androidmapbase.Tools.MapUtils.util.SensorEventHelper;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_map)
public class MapActivity extends BaseActivity implements LocationSource {

    private AMap mAMap;
    private AMapLocationClient mLocationClient; //定位器
    private OnLocationChangedListener mListener; //定位监听器

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255); //精度边缘颜色
    private static final int FILL_COLOR = Color.argb(64, 128, 192, 192); //精度填充颜色
    private boolean mFirstFix = false; //是否已将图标添加到地图
    private Marker mLocMarker; //定位点
    private Circle mCircle; //精度圆
    private SensorEventHelper mSensorHelper; //用于获取指南针方向
    public static final String LOCATION_MARKER_FLAG = "myLocation";
    private LatLng mLocation; //当前位置
    private static final float DEFAULT_SCALE = 15; //默认缩放比例

    @ViewInject(R.id.map)
    private MapView mMapView;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        if (mLocation != null) {
            focusLocation();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        //aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mAMap.setLocationSource(this);//设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);//设置为true表示可触发定位
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式

        mSensorHelper = new SensorEventHelper(this);
        mSensorHelper.registerSensorListener();
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
                            if (!mFirstFix) {
                                mFirstFix = true;
                                addCircle(mLocation, aMapLocation.getAccuracy());//添加定位精度圆
                                addMarker(mLocation);//添加定位图标
                                mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                                focusLocation();
                            } else {
                                mCircle.setCenter(mLocation); //精度中心为定位点
                                mCircle.setRadius(aMapLocation.getAccuracy()); //精度半径
                                mLocMarker.setPosition(mLocation); //定位点
                            }
                            //mListener.onLocationChanged(aMapLocation); //系统默认定位事件

                        } else {
                            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                            showToast(errText);
                            showLog("AMapErr", errText);
                        }
                    }
                }
            });
            //设置为高精度定位模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(locationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
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

    //添加精度圆
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
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
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
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
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mMapView.onPause();
        deactivate();
        mFirstFix = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
