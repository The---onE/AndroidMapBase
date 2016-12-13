package com.xmx.androidmapbase.BaseMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.ActivityBase.BaseLocationDirectionActivity;
import com.xmx.androidmapbase.Tools.Data.Callback.InsertCallback;
import com.xmx.androidmapbase.Tools.Data.Callback.SelectCallback;
import com.xmx.androidmapbase.Tools.Data.DataConstants;
import com.xmx.androidmapbase.Tools.Map.POI.POI;
import com.xmx.androidmapbase.Tools.Map.POI.POICloudManager;
import com.xmx.androidmapbase.Tools.Map.POI.POIConstants;
import com.xmx.androidmapbase.Tools.Map.POI.POIManager;
import com.xmx.androidmapbase.Tools.Map.POI.POIOverlay;
import com.xmx.androidmapbase.Tools.Map.POI.POISearchCallback;
import com.xmx.androidmapbase.Tools.Map.Route.WalkRouteOverlay;
import com.xmx.androidmapbase.Tools.Map.Utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ContentView(R.layout.activity_map_route)
public class MapRouteActivity extends BaseLocationDirectionActivity {

    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private WalkRouteOverlay mWalkRouteOverlay;

    private Marker currentMarker;
    private LatLng currentLatLng;
    private List<Marker> collectMarkers = new ArrayList<>();

    @ViewInject(R.id.btn_route)
    private Button routeButton;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

    @Event(R.id.btn_route)
    private void onRouteClick(View view) {
        if (mLocation == null) {
            showToast("定位中，稍后再试...");
            return;
        }
        if (currentLatLng == null) {
            showToast("终点未设置");
        }

        showToast("正在搜索");
        final RouteSearch.FromAndTo fromAndTo
                = new RouteSearch.FromAndTo(
                new LatLonPoint(mLocation.latitude, mLocation.longitude),
                new LatLonPoint(currentLatLng.latitude, currentLatLng.longitude));

        RouteSearch.WalkRouteQuery query
                = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }

    @Override
    protected void getMapView() {
        mMapView = getViewById(R.id.map);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        strokeColor = Color.argb(180, 3, 145, 255);
        fillColor = Color.argb(64, 128, 192, 192);
        markerFlag = "myLocation";
        //aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
    }

    @Override
    protected void setListener() {
        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
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
                currentMarker = mAMap.addMarker(m);
                currentLatLng = latLng;

                routeButton.setVisibility(View.VISIBLE);
            }
        });
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Object o = marker.getObject();
                if (o != null) {
                    if (collectMarkers.contains(marker)) {
                        POI poi = (POI) o;
                        showToast(poi.getTitle());
                        return true;
                    }
                }
                return true;
            }
        });

        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                        if (walkRouteResult.getPaths().size() > 0) {
                            if (mWalkRouteOverlay != null) {
                                mWalkRouteOverlay.removeFromMap();
                            }
                            mWalkRouteResult = walkRouteResult;
                            final WalkPath walkPath = mWalkRouteResult.getPaths()
                                    .get(0);
                            mWalkRouteOverlay = new WalkRouteOverlay(
                                    getBaseContext(), mAMap, walkPath,
                                    mWalkRouteResult.getStartPos(),
                                    mWalkRouteResult.getTargetPos());
                            mWalkRouteOverlay.addToMap();
                            mWalkRouteOverlay.zoomToSpan();
//                            mBottomLayout.setVisibility(View.VISIBLE);
//                            int dis = (int) walkPath.getDistance();
//                            int dur = (int) walkPath.getDuration();
//                            String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
//                            mRotueTimeDes.setText(des);
//                            mRouteDetailDes.setVisibility(View.GONE);
//                            mBottomLayout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(mContext,
//                                            WalkRouteDetailActivity.class);
//                                    intent.putExtra("walk_path", walkPath);
//                                    intent.putExtra("walk_result",
//                                            mWalkRouteResult);
//                                    startActivity(intent);
//                                }
//                            });
                        } else if (walkRouteResult.getPaths() == null) {
                            showToast(R.string.no_result);
                        }
                    } else {
                        showToast(R.string.no_result);
                    }
                } else {
                    ToastUtil.showError(getBaseContext(), i);
                }
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);

        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式

        POICloudManager.getInstance().selectAll(new SelectCallback<POI>() {
            @Override
            public void success(List<POI> poiList) {
                for (POI poi : poiList) {
                    addCollectMarker(poi);
                }
            }

            @Override
            public void syncError(int error) {
                switch (error) {
                    case DataConstants.NOT_INIT:
                        showToast(R.string.failure);
                        break;
                    case DataConstants.NOT_LOGGED_IN:
                        showToast(R.string.not_loggedin);
                        break;
                    case DataConstants.USERNAME_ERROR:
                        showToast(R.string.username_error);
                        break;
                    case DataConstants.CHECKSUM_ERROR:
                        showToast(R.string.not_loggedin);
                        break;
                }
            }

            @Override
            public void syncError(AVException e) {
                showToast(R.string.sync_failure);
                filterException(e);
            }
        });
    }

    private void addCollectMarker(POI poi) {
        MarkerOptions m = new MarkerOptions()
                .position(new LatLng(poi.getLatLonPoint().getLatitude(),
                        poi.getLatLonPoint().getLongitude()))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.point5)))
                .anchor(0.5f, 0.5f);
        Marker marker = mAMap.addMarker(m);
        marker.setObject(poi);
        collectMarkers.add(marker);

    }

    @Override
    protected void setLocationOption(AMapLocationClientOption locationOption) {
        //设置为高精度定位模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    @Override
    protected void whenFirstLocation(AMapLocation aMapLocation) {
        focusLocation();
    }

    @Override
    protected void whenNewLocation(AMapLocation aMapLocation) {
        //mListener.onLocationChanged(aMapLocation); //系统默认定位事件
    }

    @Override
    protected void whenLocationError(int errorCode, String errorInfo) {
        String errText = "定位失败," + errorCode + ": " + errorInfo;
        showToast(errText);
        showLog("AMapErr", errText);
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
