package com.xmx.androidmapbase.BaseMap;

import android.os.Bundle;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.BMap.Activity.BaseLocationActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_bmap)
public class BMapActivity extends BaseLocationActivity {

    private boolean locationFlag = false;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

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
        super.processLogic(savedInstanceState);
        mBMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //mBMap.setTrafficEnabled(true);
    }

    @Override
    protected void whenLocationChanged(BDLocation location) {
        if (!locationFlag) {
            focusLocation();
            locationFlag = true;
        }
    }

    @Override
    protected void whenLocationError(int errorCode) {
        showToast("定位失败:"+errorCode);
        switch (errorCode) {
            case BDLocation.TypeServerError:
                showToast("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                break;
            case BDLocation.TypeNetWorkException:
                showToast("网络不同导致定位失败，请检查网络是否通畅");
                break;
            case BDLocation.TypeCriteriaException:
                showToast("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                break;
        }
    }

    @Override
    protected void setLocationOption(LocationClientOption option) {
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
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
