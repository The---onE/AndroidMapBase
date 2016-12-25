package com.xmx.androidmapbase.BaseMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.avos.avoscloud.AVException;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.AMap.Activity.BaseLocationDirectionActivity;
import com.xmx.androidmapbase.Tools.Data.Callback.SelectCallback;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.CollectionView;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POI;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.CollectionManager;
import com.xmx.androidmapbase.Tools.Map.AMap.Route.BusResultListAdapter;
import com.xmx.androidmapbase.Tools.Map.AMap.Route.WalkRouteDetailActivity;
import com.xmx.androidmapbase.Tools.Map.AMap.Route.WalkRouteOverlay;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.AMapServicesUtil;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.AMapUtil;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_amap_route)
public class AMapRouteActivity extends BaseLocationDirectionActivity {

    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private WalkRouteOverlay mWalkRouteOverlay;
    private BusRouteResult mBusRouteResult;

    private Marker subMarker;
    private LatLng subLatLng;
    private Marker currentMarker;
    private LatLng currentLatLng;

    private CollectionView collectionView;

    @ViewInject(R.id.btn_route)
    private Button routeButton;

    @ViewInject(R.id.bottom_layout)
    private RelativeLayout bottomLayout;

    @ViewInject(R.id.firstline)
    private TextView routeTimeDesView;

    @ViewInject(R.id.secondline)
    private TextView routeDetailDesView;

    @ViewInject(R.id.bus_result_list)
    private ListView busResultList;

    @ViewInject(R.id.bus_result)
    private LinearLayout busResultLayout;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

    @Event(R.id.cancel_route)
    private void onCancelRouteClick(View view) {
        bottomLayout.setVisibility(View.GONE);
        mWalkRouteOverlay.removeFromMap();
    }

    @Event(R.id.btn_cancel_bus)
    private void onCancelBusClick(View view) {
        busResultLayout.setVisibility(View.GONE);
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
        final LatLonPoint start;
        if (subLatLng != null) {
            start = AMapServicesUtil.convertToLatLonPoint(subLatLng);
        } else {
            start = AMapServicesUtil.convertToLatLonPoint(mLocation);
        }
        final LatLonPoint end = AMapServicesUtil.convertToLatLonPoint(currentLatLng);

        String routeType[] = {"步行路线", "公交路线"};
        new AlertDialog.Builder(AMapRouteActivity.this)
                .setTitle("路线类型")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(routeType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                searchWalkRoute(start, end);
                                break;
                            case 1:
                                searchBusRoute(start, end);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }

    private void searchWalkRoute(LatLonPoint start, LatLonPoint end) {
        showToast("正在搜索");
        final RouteSearch.FromAndTo fromAndTo
                = new RouteSearch.FromAndTo(start, end);

        RouteSearch.WalkRouteQuery query
                = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }

    private void searchBusRoute(LatLonPoint start, LatLonPoint end) {
        showToast("正在搜索");
        final RouteSearch.FromAndTo fromAndTo
                = new RouteSearch.FromAndTo(start, end);

        RouteSearch.BusRouteQuery query
                = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BusDefault,
                mCity, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
        mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
    }

    @Override
    protected void getMapView() {
        mMapView = getViewById(R.id.map);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        collectionView = new CollectionView(this, mAMap);

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
        mAMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
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
                subMarker = mAMap.addMarker(m);
                subLatLng = latLng;
            }
        });
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Object o = marker.getObject();
                if (o != null) {
                    if (collectionView.isCollect(marker)) {
                        POI poi = (POI) o;
                        showToast(poi.getTitle());
                        return true;
                    }
                }
                return false;
            }
        });

        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (mWalkRouteOverlay != null) {
                        mWalkRouteOverlay.removeFromMap();
                    }
                    bottomLayout.setVisibility(View.GONE);
                    if (busRouteResult != null && busRouteResult.getPaths() != null) {
                        if (busRouteResult.getPaths().size() > 0) {
                            busResultLayout.setVisibility(View.VISIBLE);
                            mBusRouteResult = busRouteResult;
                            BusResultListAdapter mBusResultListAdapter
                                    = new BusResultListAdapter(getBaseContext(), mBusRouteResult);
                            busResultList.setAdapter(mBusResultListAdapter);
                        } else if (busRouteResult.getPaths() == null) {
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
                            bottomLayout.setVisibility(View.VISIBLE);
                            int dis = (int) walkPath.getDistance();
                            int dur = (int) walkPath.getDuration();
                            String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                            routeTimeDesView.setText(des);
                            routeDetailDesView.setVisibility(View.GONE);
                            bottomLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(AMapRouteActivity.this, WalkRouteDetailActivity.class);
                                    intent.putExtra("walk_path", walkPath);
                                    intent.putExtra("walk_result",
                                            mWalkRouteResult);
                                    startActivity(intent);
                                }
                            });
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
