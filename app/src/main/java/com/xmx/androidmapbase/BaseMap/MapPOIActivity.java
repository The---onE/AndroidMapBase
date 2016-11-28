package com.xmx.androidmapbase.BaseMap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.ActivityBase.BaseLocationDirectionActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_map_poi)
public class MapPOIActivity extends BaseLocationDirectionActivity {

    private PoiSearch.Query query;// Poi查询条件类
    private Marker lastMarker;
    private MyPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    int currentPage;
    private static final int SEARCH_RADIUS = 5000;
    private static final int SEARCH_SIZE = 20;

    @ViewInject(R.id.poi_name)
    private TextView mPoiName;

    @ViewInject(R.id.poi_address)
    private TextView mPoiAddress;

    @ViewInject(R.id.input_edittext)
    private EditText mSearchText;

    @ViewInject(R.id.btn_location)
    Button locationButton;

    @ViewInject(R.id.poi_detail)
    private RelativeLayout mPoiDetail;

    @Event(R.id.btn_location)
    private void onLocationClick(View view) {
        focusLocation();
    }

    @Event(R.id.btn_search)
    private void onSearchClick(View view) {
        doSearchQuery();
    }

    @Event(R.id.btn_cancel)
    private void onCancelClick(View view) {
        mSearchText.setText("");
        if (poiOverlay != null) {
            poiOverlay.removeFromMap();
            poiOverlay = null;
        }
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
            }
        });
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getObject() != null) {
                    whetherToShowDetailInfo(true);
                    try {
                        PoiItem mCurrentPoi = (PoiItem) marker.getObject();
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
                        // TODO: handle exception
                    }
                } else {
                    whetherToShowDetailInfo(false);
                    resetLastMarker();
                }
                return true;
            }
        });
        mAMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });

        mAMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);

        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        String keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(SEARCH_SIZE);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (mLocation != null) {
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                @Override
                public void onPoiSearched(PoiResult poiResult, int poiCode) {
                    if (poiCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                            if (poiResult.getQuery().equals(query)) {// 是否是同一条
                                poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                                List<SuggestionCity> suggestionCities = poiResult
                                        .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                                if (poiItems != null && poiItems.size() > 0) {
                                    //清除POI信息显示
                                    whetherToShowDetailInfo(false);
                                    //并还原点击marker样式
                                    if (lastMarker != null) {
                                        resetLastMarker();
                                    }
                                    //清理之前搜索结果的marker
                                    if (poiOverlay != null) {
                                        poiOverlay.removeFromMap();
                                    }
                                    poiOverlay = new MyPoiOverlay(mAMap, poiItems);
                                    poiOverlay.addToMap();
                                    //poiOverlay.zoomToSpan();
                                    focusLocation();

                                } else if (suggestionCities != null
                                        && suggestionCities.size() > 0) {
                                    showSuggestCity(suggestionCities);
                                } else {
                                    showToast(R.string.no_result);
                                }
                            }
                        } else {
                            showToast(R.string.no_result);
                        }
                    }
                }

                @Override
                public void onPoiItemSearched(PoiItem poiItem, int i) {

                }
            });
            poiSearch.setBound(new PoiSearch.SearchBound(
                    new LatLonPoint(mLocation.latitude, mLocation.longitude),
                    SEARCH_RADIUS, true));//
            // 设置搜索区域为以当前位置为圆心，其周围范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    // 将之前被点击的marker置为原来的状态
    private void resetLastMarker() {
        int index = poiOverlay.getPoiIndex(lastMarker);
        if (index < 10) {
            lastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        lastMarker = null;

    }

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet() + mCurrentPoi.getDistance());
    }

    private int[] markers = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
            locationButton.setVisibility(View.GONE);
        } else {
            mPoiDetail.setVisibility(View.GONE);
            locationButton.setVisibility(View.VISIBLE);
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

    /**
     * 自定义PoiOverlay
     */
    private class MyPoiOverlay {
        private AMap mAMap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public MyPoiOverlay(AMap amap, List<PoiItem> pois) {
            mAMap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         *
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mAMap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         *
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mAMap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         *
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            } else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
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
