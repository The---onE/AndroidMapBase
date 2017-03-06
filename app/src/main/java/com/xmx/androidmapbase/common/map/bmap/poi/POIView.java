package com.xmx.androidmapbase.common.map.bmap.poi;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.xmx.androidmapbase.R;

import java.util.List;

/**
 * Created by The_onE on 2016/12/21.
 */

public class POIView {

    private Marker lastMarker;
    private POIOverlay poiOverlay;// poi图层

    private BaiduMap mBMap;
    private Context mContext;

    public POIView(Context context, BaiduMap map) {
        mContext = context;
        mBMap = map;
    }

    public void searchAndShowPOI(LatLng position, int radius,
                                 int page, int size,
                                 String keyword, final POIViewSearchCallback callback) {
        POIManager.getInstance().searchPOIQuery(position, radius,
                page, size, keyword,
                new POISearchCallback() {
                    @Override
                    public void success(List<POI> poiItems) {
                        callback.success();
                        showPOI(poiItems);
                    }

                    @Override
                    public void suggest(List<CityInfo> cities) {
                        showSuggestCity(cities);
                    }

                    @Override
                    public void noData() {
                        showToast(R.string.no_result);
                    }
                });
    }

    public void resetMarker() {
        if (lastMarker != null) {
            resetLastMarker();
        }
    }

    public void resetMarker(Marker marker) {
        if (lastMarker == null) {
            lastMarker = marker;
        } else {
            // 将之前被点击的marker置为原来的状态
            resetLastMarker();
            lastMarker = marker;
        }
    }

    public void removeFromMap() {
        if (poiOverlay != null) {
            poiOverlay.removeAllFromMap();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    public void showSuggestCity(List<CityInfo> cities) {
        String information = "推荐城市\n";
        for (CityInfo city : cities) {
            information += "城市名称:" + city.city + "城市区号:" + "\n";
        }
        showToast(information);
    }

    public void showPOI(List<POI> poiItems) {
        //并还原点击marker样式
        if (lastMarker != null) {
            resetLastMarker();
        }
        //清理之前搜索结果的marker
        if (poiOverlay != null) {
            poiOverlay.removeAllFromMap();
        }
        poiOverlay = new POIOverlay(mBMap, poiItems, mContext);
        poiOverlay.addAllToMap();
        //poiOverlay.zoomToSpan();
    }

    // 将之前被点击的marker置为原来的状态
    private void resetLastMarker() {
        int index = poiOverlay.getPoiIndex(lastMarker);
        if (index < POIConstants.MARKERS.length) {
            lastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            mContext.getResources(),
                            POIConstants.MARKERS[index])));
        } else {
            lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marker_other_highlight)));
        }
        lastMarker = null;
    }

    protected void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }
}
