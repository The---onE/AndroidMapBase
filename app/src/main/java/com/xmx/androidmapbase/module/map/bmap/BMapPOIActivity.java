package com.xmx.androidmapbase.module.map.bmap;

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
import com.xmx.androidmapbase.common.data.callback.DelCallback;
import com.xmx.androidmapbase.common.data.callback.InsertCallback;
import com.xmx.androidmapbase.common.data.callback.SelectCallback;
import com.xmx.androidmapbase.common.map.bmap.activity.BaseLocationDirectionActivity;
import com.xmx.androidmapbase.common.map.bmap.poi.CollectionManager;
import com.xmx.androidmapbase.common.map.bmap.poi.CollectionView;
import com.xmx.androidmapbase.common.map.bmap.poi.POI;
import com.xmx.androidmapbase.common.map.bmap.poi.POIManager;
import com.xmx.androidmapbase.common.map.bmap.poi.POIView;
import com.xmx.androidmapbase.common.map.bmap.poi.POIViewSearchCallback;
import com.xmx.androidmapbase.common.user.UserData;
import com.xmx.androidmapbase.utils.ExceptionUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.UUID;

@ContentView(R.layout.activity_bmap_poi)
public class BMapPOIActivity extends BaseLocationDirectionActivity {

    private Marker currentMarker;
    private LatLng currentLatLng;
    private Marker currentCollect;

    private POIView poiView;
    private CollectionView collectionView;

    @ViewInject(R.id.poi_name)
    private TextView mPoiName;

    @ViewInject(R.id.poi_address)
    private TextView mPoiAddress;

    @ViewInject(R.id.input_edittext)
    private EditText mSearchText;

    @ViewInject(R.id.btn_location)
    private Button locationButton;

    @ViewInject(R.id.btn_collect)
    private Button collectButton;

    @ViewInject(R.id.btn_collect_cancel)
    private Button cancelCollectButton;

    @ViewInject(R.id.poi_detail)
    private RelativeLayout mPoiDetail;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

    @Event(R.id.btn_search)
    private void onSearchClick(View view) {
        String keyword = mSearchText.getText().toString().trim();
        LatLng position = mLocation;
        if (currentLatLng != null) {
            position = currentLatLng;
        }
        poiView.searchAndShowPOI(position, 0,
                0, 0, keyword, new POIViewSearchCallback() {
                    @Override
                    public void success() {
                        whetherToShowDetailInfo(false);
                        focusPOISearch();
                    }
                });
    }

    @Event(value = R.id.btn_search, type = View.OnLongClickListener.class)
    private boolean onSearchLongClick(View view) {
        String keyword = mSearchText.getText().toString().trim();
        String[] info = keyword.split(" ");
        final GeoCoder mGeoCoder = GeoCoder.newInstance();

        // 设置查询结果监听者
        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                LatLng location = geoCodeResult.getLocation();
                if (location != null) {
                    whetherToShowDetailInfo(false);
                    poiView.resetMarker();
                    setCurrentPosition(location);
                    focusLocation(location);
                } else {
                    showToast(R.string.no_result);
                }
                mGeoCoder.destroy();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                String address = reverseGeoCodeResult.getAddress();
                String circle = reverseGeoCodeResult.getBusinessCircle();
                showToast("地址:" + address + "\n商圈:" + circle);
                List<PoiInfo> list = reverseGeoCodeResult.getPoiList();
                if (list != null) {
                    List<POI> poiList = POIManager.convertPOIList(list);
                    //清除POI信息显示
                    whetherToShowDetailInfo(false);
                    focusPOISearch();
                    poiView.showPOI(poiList);
                }
                mGeoCoder.destroy();
            }
        });

        if (keyword.equals("") || info.length < 2) {
            LatLng position = mLocation;
            if (currentLatLng != null) {
                position = currentLatLng;
            }
            // 反地理编码请求参数对象
            ReverseGeoCodeOption mReverseGeoCodeOption = new ReverseGeoCodeOption();
            // 设置请求参数
            mReverseGeoCodeOption.location(position);
            // 发起反地理编码请求(经纬度->地址信息)
            mGeoCoder.reverseGeoCode(mReverseGeoCodeOption);
        } else {
            GeoCodeOption mGeoCodeOption = new GeoCodeOption();
            mGeoCodeOption.city(info[0]);
            mGeoCodeOption.address(info[1]);
            mGeoCoder.geocode(mGeoCodeOption);
        }
        return true;
    }

    @Event(R.id.btn_cancel)
    private void onCancelClick(View view) {
        mSearchText.setText("");
        poiView.removeFromMap();
        whetherToShowDetailInfo(false);
    }

    @Event(R.id.btn_collect)
    private void onCollectClick(View view) {
        if (currentMarker == null || currentLatLng == null) {
            return;
        }
        if (currentCollect != null) {
            AlertDialog.Builder builder = new AlertDialog
                    .Builder(BMapPOIActivity.this);
            builder.setMessage("要删除该收藏吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String id = currentCollect.getExtraInfo().getString("id");
                    CollectionManager.getInstance().deleteFromCloud(id,
                            new DelCallback() {
                                @Override
                                public void success(UserData user) {
                                    showToast("删除成功");
                                    currentCollect.remove();
                                    currentCollect = null;
                                }

                                @Override
                                public void syncError(int error) {
                                    CollectionManager.defaultError(error, getBaseContext());
                                }

                                @Override
                                public void syncError(AVException e) {
                                    showToast(R.string.delete_failure);
                                    ExceptionUtil.normalException(e, getBaseContext());
                                }
                            });
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else {
            final EditText edit = new EditText(this);
            edit.setTextColor(Color.BLACK);
            edit.setTextSize(24);
            new AlertDialog.Builder(BMapPOIActivity.this)
                    .setTitle("添加收藏")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(edit)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String title = edit.getText().toString();
                            final POI poi = new POI(UUID.randomUUID().toString(),
                                    new LatLng(currentLatLng.latitude, currentLatLng.longitude),
                                    title, "");
//                        POISQLManager.getInstance().insertData(poi);
//                        addCollectMarker(poi);
//                        showToast("收藏成功");
                            CollectionManager.getInstance().insertToCloud(poi, new InsertCallback() {
                                @Override
                                public void success(UserData user, String objectId) {
                                    poi.mCloudId = objectId;
                                    collectionView.addCollection(poi);
                                    showToast("收藏成功");
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
                    })
                    .setNegativeButton("取消", null).show();
        }
    }

    @Event(R.id.btn_collect_cancel)
    private void onCancelCollectClick(View view) {
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
            currentLatLng = null;
        }
        cancelCollectButton.setVisibility(View.GONE);
        collectButton.setVisibility(View.GONE);
    }

    @Event(R.id.poi_detail)
    private void onDetailClick(View view) {
        //打开地点详情
    }

    @Override
    protected void getMapView() {
        mMapView = getViewById(R.id.bmapView);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        poiView = new POIView(this, mBMap);
        collectionView = new CollectionView(this, mBMap);
        //aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
    }

    @Override
    protected void setListener() {
        mBMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                whetherToShowDetailInfo(false);
                poiView.resetMarker();

                setCurrentPosition(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                showToast(mapPoi.getName());
                LatLng latLng = mapPoi.getPosition();
                whetherToShowDetailInfo(false);
                poiView.resetMarker();

                setCurrentPosition(latLng);
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
                        currentCollect = marker;
                        return true;
                    }
                    if (title.equals(LOCATION_TITLE)) {
                        return true;
                    }
                    whetherToShowDetailInfo(true);
                    try {
                        poiView.resetMarker(marker);
                        Marker detailMarker = marker;
                        detailMarker.setIcon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(
                                        getResources(),
                                        R.drawable.poi_marker_pressed)));

                        setPoiItemDisplayContent(marker.getTitle());
                    } catch (Exception e) {
                        ExceptionUtil.normalException(e, getBaseContext());
                    }
                } else {
                    whetherToShowDetailInfo(false);
                    poiView.resetMarker();
                }
                return false;
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

        currentCollect = null;

        collectButton.setVisibility(View.VISIBLE);
        cancelCollectButton.setVisibility(View.VISIBLE);
    }

//    private void setPoiItemDisplayContent(final POI mCurrentPoi) {
//        mPoiName.setText(mCurrentPoi.name);
//        mPoiAddress.setText(mCurrentPoi.address);
//    }

    private void setPoiItemDisplayContent(String title) {
        mPoiName.setText(title);
    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
        } else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }

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
