package com.xmx.androidmapbase.BaseMap;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Data.Callback.DelCallback;
import com.xmx.androidmapbase.Tools.Data.Callback.InsertCallback;
import com.xmx.androidmapbase.Tools.Data.Callback.SelectCallback;
import com.xmx.androidmapbase.Tools.Map.BMap.Activity.BaseLocationDirectionActivity;
import com.xmx.androidmapbase.Tools.Map.BMap.POI.CollectionManager;
import com.xmx.androidmapbase.Tools.Map.BMap.POI.CollectionView;
import com.xmx.androidmapbase.Tools.Map.BMap.POI.POI;
import com.xmx.androidmapbase.Tools.Map.BMap.POI.POIManager;
import com.xmx.androidmapbase.Tools.Map.BMap.POI.POIView;
import com.xmx.androidmapbase.Tools.Map.BMap.POI.POIViewSearchCallback;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.UUID;

@ContentView(R.layout.activity_bmap_route)
public class BMapRouteActivity extends BaseLocationDirectionActivity {

    private Marker subMarker;
    private LatLng subLatLng;
    private Marker currentMarker;
    private LatLng currentLatLng;

    private CollectionView collectionView;

    @ViewInject(R.id.btn_route)
    private Button routeButton;

    // 路线相关控件

    @ViewInject(R.id.btn_location)
    private Button locationButton;

    @ViewInject(R.id.bottom_layout)
    private RelativeLayout bottomLayout;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

    // 路线相关点击事件

    @Override
    protected void getMapView() {
        mMapView = getViewById(R.id.bmapView);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        collectionView = new CollectionView(this, mBMap);
    }

    @Override
    protected void setListener() {
        mBMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setCurrentPosition(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                setCurrentPosition(mapPoi.getPosition());
                showToast(mapPoi.getName());
                return true;
            }
        });

        mBMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                setCurrentPosition(marker.getPosition());
                String title = marker.getTitle();
                if (title != null && !title.equals("")) {
                    if (collectionView.isCollect(marker)) {
                        showToast(marker.getTitle());
                        return true;
                    }
                    if (title.equals(LOCATION_TITLE)) {
                        return true;
                    }
                }
                return false;
            }
        });

        mBMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (subMarker != null) {
                    subMarker.remove();
                    subMarker = null;
                    subLatLng = null;
                    return;
                }
                MarkerOptions m = new MarkerOptions()
                        .position(
                                new LatLng(latLng.latitude, latLng.longitude))
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(
                                        getResources(),
                                        R.drawable.point3)))
                        .anchor(0.5f, 0.5f);
                subMarker = (Marker) mBMap.addOverlay(m);
                subLatLng = latLng;
            }
        });

        // 路线查询事件
    }

    @Override
    protected void whenFirstLocation(BDLocation bdLocation) {
        focusLocation();
    }

    @Override
    protected void whenNewLocation(BDLocation bdLocation) {

    }

    @Override
    protected void whenLocationError(int errorCode) {
        showToast("定位失败:" + errorCode);
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
        int span = 1000;
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
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);

//        List<POI> poiList = POISQLManager.getInstance().selectAll();
//        for (POI poi : poiList) {
//            addCollectMarker(poi);
//        }
        CollectionManager.getInstance().selectAll(new SelectCallback<POI>() {
            @Override
            public void success(List<POI> poiList) {
                for (POI poi : poiList) {
                    collectionView.addCollection(poi);
                }
            }

            @Override
            public void syncError(int error) {
                CollectionManager.defaultError(error, getBaseContext());
            }

            @Override
            public void syncError(AVException e) {
                showToast(R.string.sync_failure);
                filterException(e);
            }
        });
    }

    private void setCurrentPosition(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
        }
        MarkerOptions m = new MarkerOptions()
                .position(
                        new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.point6)))
                .anchor(0.5f, 0.5f);
        currentMarker = (Marker) mBMap.addOverlay(m);
        currentLatLng = latLng;

        routeButton.setVisibility(View.VISIBLE);
    }

//    private void setPoiItemDisplayContent(final POI mCurrentPoi) {
//        mPoiName.setText(mCurrentPoi.name);
//        mPoiAddress.setText(mCurrentPoi.address);
//    }

    private void focusPOISearch() {
        if (currentLatLng != null) {
            focusLocation(currentLatLng, 14);
        } else {
            focusLocation(14);
        }
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
