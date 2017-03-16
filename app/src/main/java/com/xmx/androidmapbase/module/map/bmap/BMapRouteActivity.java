package com.xmx.androidmapbase.module.map.bmap;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.common.data.callback.SelectCallback;
import com.xmx.androidmapbase.common.map.bmap.activity.BaseLocationDirectionActivity;
import com.xmx.androidmapbase.common.map.bmap.poi.CollectionManager;
import com.xmx.androidmapbase.common.map.bmap.poi.CollectionView;
import com.xmx.androidmapbase.common.map.bmap.poi.POI;
import com.xmx.androidmapbase.common.map.bmap.route.BusResultListAdapter;
import com.xmx.androidmapbase.common.map.bmap.route.OverlayManager;
import com.xmx.androidmapbase.common.map.bmap.route.WalkRouteDetailActivity;
import com.xmx.androidmapbase.common.map.bmap.route.WalkingRouteOverlay;
import com.xmx.androidmapbase.common.map.bmap.utils.BMapUtil;
import com.xmx.androidmapbase.utils.ExceptionUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

@ContentView(R.layout.activity_bmap_route)
public class BMapRouteActivity extends BaseLocationDirectionActivity {

    private Marker subMarker;
    private LatLng subLatLng;
    private Marker currentMarker;
    private LatLng currentLatLng;

    // 路线相关变量
    RoutePlanSearch mSearch;
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    boolean useDefaultIcon = false;
    OverlayManager routeOverlay = null;
    MassTransitRouteResult mBusRouteResult;

    WalkingRouteResult nowResultWalk = null;

    private CollectionView collectionView;

    @ViewInject(R.id.btn_route)
    private Button routeButton;

    // 路线相关控件
    @ViewInject(R.id.firstline)
    private TextView routeTimeDesView;

    @ViewInject(R.id.secondline)
    private TextView routeDetailDesView;

    @ViewInject(R.id.btn_location)
    private Button locationButton;

    @ViewInject(R.id.bottom_layout)
    private RelativeLayout bottomLayout;

    @ViewInject(R.id.bus_result)
    private RelativeLayout busResultLayout;

    @ViewInject(R.id.bus_result_list)
    private ListView busResultList;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

    // 路线相关点击事件
    @Event(R.id.cancel_route)
    private void onCancelRouteClick(View view) {
        bottomLayout.setVisibility(View.GONE);
        routeOverlay.removeFromMap();
    }

//    @Event(R.id.btn_cancel_bus)
//    private void onCancelBusClick(View view) {
//        busResultLayout.setVisibility(View.GONE);
//    }

    @Event(R.id.btn_route)
    private void onRouteClick(View view) {
        if (mLocation == null) {
            showToast("定位中，稍后再试...");
            return;
        }
        if (currentLatLng == null) {
            showToast("终点未设置");
        }
        final LatLng start;
        if (subLatLng != null) {
            start = subLatLng;
        } else {
            start = mLocation;
        }
        final LatLng end = currentLatLng;

        String routeType[] = {"步行路线", "公交路线"};
        new AlertDialog.Builder(BMapRouteActivity.this)
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

    private void searchWalkRoute(LatLng start, LatLng end) {
        showToast("正在搜索");
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);
        mSearch.walkingSearch(new WalkingRoutePlanOption()
                .from(stNode)
                .to(enNode));
    }

    private void searchBusRoute(LatLng start, LatLng end) {
        showToast("正在搜索");
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);

        mSearch.masstransitSearch(new MassTransitRoutePlanOption().
                from(stNode).
                to(enNode));
    }

    @Override
    protected void getMapView() {
        mMapView = getViewById(R.id.bmapView);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        collectionView = new CollectionView(this, mBMap);

        Button cancelBusRouteButton = new Button(this);
        cancelBusRouteButton.setText("取消");
        cancelBusRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busResultLayout.setVisibility(View.GONE);
            }
        });
        busResultList.addFooterView(cancelBusRouteButton);
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
                } else if (routeOverlay instanceof WalkingRouteOverlay) {
                    WalkingRouteOverlay overlay = (WalkingRouteOverlay) routeOverlay;
                    WalkingRouteLine.WalkingStep step = overlay.getWalkingStep(marker);
                    if (step != null) {
                        String instructions = step.getInstructions();
                        instructions = instructions.replaceAll("<b>", "【");
                        instructions = instructions.replaceAll("</b>", "】");
                        showToast(instructions);
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
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(final WalkingRouteResult walkingRouteResult) {
                if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    showToast("抱歉，未找到结果");
                } else if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // walkingRouteResult.getSuggestAddrInfo()
                    return;
                } else if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    if (routeOverlay != null) {
                        routeOverlay.removeFromMap();
                    }
                    nodeIndex = -1;
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);

                    if (walkingRouteResult.getRouteLines().size() >= 1) {
                        // 直接显示
                        route = walkingRouteResult.getRouteLines().get(0);
                        WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBMap) {
                            @Override
                            public BitmapDescriptor getStartMarker() {
                                if (useDefaultIcon) {
                                    return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
                                }
                                return null;
                            }

                            @Override
                            public BitmapDescriptor getTerminalMarker() {
                                if (useDefaultIcon) {
                                    return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
                                }
                                return null;
                            }
                        };
                        // mBMap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        overlay.setData(walkingRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        bottomLayout.setVisibility(View.VISIBLE);
                        int dis = route.getDistance();
                        int dur = route.getDuration();
                        String des = BMapUtil.getFriendlyTime(dur) + "(" + BMapUtil.getFriendlyLength(dis) + ")";
                        routeTimeDesView.setText(des);
                        routeDetailDesView.setVisibility(View.GONE);
                        bottomLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BMapRouteActivity.this, WalkRouteDetailActivity.class);
                                intent.putExtra("walk_path", walkingRouteResult.getRouteLines().get(0));
                                intent.putExtra("walk_result",
                                        walkingRouteResult);
                                startActivity(intent);
                            }
                        });
                    } else {
                        showToast(R.string.no_result);
                    }
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
                if (massTransitRouteResult == null || massTransitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    showToast("抱歉，未找到结果");
                } else if (massTransitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // walkingRouteResult.getSuggestAddrInfo()
                    return;
                } else if (massTransitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    if (routeOverlay != null) {
                        routeOverlay.removeFromMap();
                    }
                    nodeIndex = -1;
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);

                    if (massTransitRouteResult.getRouteLines().size() >= 1) {
                        bottomLayout.setVisibility(View.GONE);
                        busResultLayout.setVisibility(View.VISIBLE);
                        mBusRouteResult = massTransitRouteResult;
                        BusResultListAdapter mBusResultListAdapter
                                = new BusResultListAdapter(getBaseContext(), mBusRouteResult);
                        busResultList.setAdapter(mBusResultListAdapter);
                    } else {
                        showToast(R.string.no_result);
                    }
                }
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
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
                ExceptionUtil.normalException(e, getBaseContext());
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
