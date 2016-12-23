package com.xmx.androidmapbase.Tools.Map.AMap.POI;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.SuggestionCity;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.AMap.Utils.ToastUtil;

import java.util.List;

/**
 * Created by The_onE on 2016/12/21.
 */

public class POIView {

    private Marker lastMarker;
    private POIOverlay poiOverlay;// poi图层

    private AMap mAMap;
    private Context mContext;

    public POIView(Context context, AMap map) {
        mContext = context;
        mAMap = map;
    }

    public void searchAndShowPOI(LatLng position, int radius,
                                 int page, int size,
                                 String keyword, final POIViewSearchCallback callback) {
        POIManager.getInstance().searchPOIQuery(position, 0,
                0, 0,
                keyword, "", "",
                new POISearchCallback() {
                    @Override
                    public void success(List<POI> poiItems) {
                        callback.success();
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
                        ToastUtil.showError(mContext, code);
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
    private void showSuggestCity(List<SuggestionCity> cities) {
        String information = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            information += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
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
        poiOverlay = new POIOverlay(mAMap, poiItems, mContext);
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
