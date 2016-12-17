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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Data.Callback.DelCallback;
import com.xmx.androidmapbase.Tools.Map.AMap.Activity.BaseLocationDirectionActivity;
import com.xmx.androidmapbase.Tools.Data.Callback.InsertCallback;
import com.xmx.androidmapbase.Tools.Data.Callback.SelectCallback;
import com.xmx.androidmapbase.Tools.Data.DataConstants;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POI;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POICloudManager;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.AMapServicesUtil;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.AMapUtil;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.ToastUtil;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POIConstants;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POIManager;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POIOverlay;
import com.xmx.androidmapbase.Tools.Map.AMap.POI.POISearchCallback;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ContentView(R.layout.activity_amap_poi)
public class AMapPOIActivity extends BaseLocationDirectionActivity {

    private Marker lastMarker;
    private POIOverlay poiOverlay;// poi图层

    private Marker currentMarker;
    private LatLng currentLatLng;
    private List<Marker> collectMarkers = new ArrayList<>();
    private Marker currentCollect;

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
        POIManager.getInstance().searchPOIQuery(position, 0,
                0, 0,
                keyword, "", "",
                new POISearchCallback() {
                    @Override
                    public void success(List<POI> poiItems) {
                        showPOI(poiItems);
                    }

                    @Override
                    public void suggest(List<SuggestionCity> cities) {
                        showSuggestCity(cities);
                    }

                    @Override
                    public void noData() {
                        showToast(R.string.no_result);
                    }

                    @Override
                    public void error(int code) {
                        ToastUtil.showError(getBaseContext(), code);
                    }
                });
    }

    @Event(value = R.id.btn_search, type = View.OnLongClickListener.class)
    private boolean onSearchLongClick(View view) {
        String keyword = mSearchText.getText().toString().trim();
        String[] info = keyword.split(" ");
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);

        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 1000) {
                    if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                            && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                        String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                        showToast("地址:" + address);
                        List<PoiItem> list = regeocodeResult.getRegeocodeAddress().getPois();
                        if (list != null) {
                            List<POI> poiList = new ArrayList<>();
                            for (PoiItem poiItem : list) {
                                poiList.add(new POI(poiItem));
                            }
                            showPOI(poiList);
                        }
                    } else {
                        showToast(R.string.no_result);
                    }
                } else {
                    ToastUtil.showError(getBaseContext(), i);
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                if (i == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                            && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                        LatLonPoint position = address.getLatLonPoint();
                        LatLng latLng = AMapUtil.convertToLatLng(position);

                        whetherToShowDetailInfo(false);
                        if (lastMarker != null) {
                            resetLastMarker();
                        }
                        setCurrentPosition(latLng);
                        focusLocation(latLng);
                    } else {
                        showToast(R.string.no_result);
                    }
                } else {
                    ToastUtil.showError(getBaseContext(), i);
                }
            }
        });
        if (keyword.equals("") || info.length < 2) {
            LatLng position = mLocation;
            if (currentLatLng != null) {
                position = currentLatLng;
            }

            LatLonPoint point = AMapServicesUtil.convertToLatLonPoint(position);
            RegeocodeQuery query = new RegeocodeQuery(point, 200, GeocodeSearch.AMAP);
            geocodeSearch.getFromLocationAsyn(query);
        } else {
            GeocodeQuery query = new GeocodeQuery(info[1], info[0]);
            geocodeSearch.getFromLocationNameAsyn(query);
        }

        return true;
    }

    @Event(R.id.btn_cancel)
    private void onCancelClick(View view) {
        mSearchText.setText("");
        if (poiOverlay != null) {
            poiOverlay.removeAllFromMap();
        }
        whetherToShowDetailInfo(false);
    }

    @Event(R.id.btn_collect)
    private void onCollectClick(View view) {
        if (currentMarker == null || currentLatLng == null) {
            return;
        }
        if (currentCollect != null) {
            AlertDialog.Builder builder = new AlertDialog
                    .Builder(AMapPOIActivity.this);
            builder.setMessage("要删除该收藏吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    POI poi = (POI) currentCollect.getObject();
                    String id = poi.mCloudId;
                    POICloudManager.getInstance().deleteFromCloud(id,
                            new DelCallback() {
                                @Override
                                public void success(AVObject user) {
                                    showToast("删除成功");
                                    currentCollect.remove();
                                    currentCollect = null;
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
                                    showToast(R.string.delete_failure);
                                    filterException(e);
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
            new AlertDialog.Builder(AMapPOIActivity.this)
                    .setTitle("添加收藏")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(edit)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String title = edit.getText().toString();
                            final POI poi = new POI(UUID.randomUUID().toString(),
                                    new LatLonPoint(currentLatLng.latitude, currentLatLng.longitude),
                                    title, "");
//                        POISQLManager.getInstance().insertData(poi);
//                        addCollectMarker(poi);
//                        showToast("收藏成功");
                            POICloudManager.getInstance().insertToCloud(poi, new InsertCallback() {
                                @Override
                                public void success(AVObject user, String objectId) {
                                    poi.mCloudId = objectId;
                                    addCollectMarker(poi);
                                    showToast("收藏成功");
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
                whetherToShowDetailInfo(false);
                if (lastMarker != null) {
                    resetLastMarker();
                }

                setCurrentPosition(latLng);
            }
        });
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                setCurrentPosition(marker.getPosition());
                Object o = marker.getObject();
                if (o != null) {
                    if (collectMarkers.contains(marker)) {
                        POI poi = (POI) o;
                        showToast(poi.getTitle());
                        currentCollect = marker;
                        return true;
                    }
                    whetherToShowDetailInfo(true);
                    try {
                        POI mCurrentPoi = (POI) marker.getObject();
                        if (lastMarker == null) {
                            lastMarker = marker;
                        } else {
                            // 将之前被点击的marker置为原来的状态
                            resetLastMarker();
                            lastMarker = marker;
                        }
                        Marker detailMarker = marker;
                        detailMarker.setIcon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.decodeResource(
                                        getResources(),
                                        R.drawable.poi_marker_pressed)));

                        setPoiItemDisplayContent(mCurrentPoi);
                    } catch (Exception e) {
                        filterException(e);
                    }
                } else {
                    whetherToShowDetailInfo(false);
                    if (lastMarker != null) {
                        resetLastMarker();
                    }
                }
                return true;
            }
        });
//        mAMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//
//            }
//        });
//
//        mAMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);

        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式

//        List<POI> poiList = POISQLManager.getInstance().selectAll();
//        for (POI poi : poiList) {
//            addCollectMarker(poi);
//        }
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

    private void showPOI(List<POI> poiItems) {
        //清除POI信息显示
        whetherToShowDetailInfo(false);
        //并还原点击marker样式
        if (lastMarker != null) {
            resetLastMarker();
        }
        //清理之前搜索结果的marker
        if (poiOverlay != null) {
            poiOverlay.removeAllFromMap();
        }
        whetherToShowDetailInfo(false);
        poiOverlay = new POIOverlay(mAMap, poiItems, getBaseContext());
        poiOverlay.addAllToMap();
        //poiOverlay.zoomToSpan();
        if (currentLatLng != null) {
            focusLocation(currentLatLng, 13.5f);
        } else {
            focusLocation(13.5f);
        }
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

    // 将之前被点击的marker置为原来的状态
    private void resetLastMarker() {
        int index = poiOverlay.getPoiIndex(lastMarker);
        if (index < POIConstants.MARKERS.length) {
            lastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            POIConstants.MARKERS[index])));
        } else {
            lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        lastMarker = null;

    }

    private void setPoiItemDisplayContent(final POI mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
        } else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String information = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            information += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        showToast(information);
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
        currentMarker = mAMap.addMarker(m);
        currentLatLng = latLng;

        currentCollect = null;

        collectButton.setVisibility(View.VISIBLE);
        cancelCollectButton.setVisibility(View.VISIBLE);
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