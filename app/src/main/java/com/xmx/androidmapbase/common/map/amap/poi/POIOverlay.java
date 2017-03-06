package com.xmx.androidmapbase.common.map.amap.poi;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.common.map.amap.utils.BaseOverlay;

import java.util.List;

/**
 * Created by The_onE on 2016/11/28.
 */

public class POIOverlay extends BaseOverlay {
    private List<POI> mPOIs;

    public POIOverlay(AMap amap, List<POI> POIs, Context context) {
        super(amap, context);
        mPOIs = POIs;
    }

    public void addAllToMap() {
        for (int i = 0; i < mPOIs.size(); i++) {
            POI item = mPOIs.get(i);
            MarkerOptions markerOptions = getMarkerOptions(item, getBitmapDescriptor(i));
            Marker marker = addToMap(markerOptions);
            marker.setObject(item);
        }
    }

    private MarkerOptions getMarkerOptions(PoiItem poi, BitmapDescriptor bitmap) {
        return new MarkerOptions()
                .position(
                        new LatLng(poi.getLatLonPoint()
                                .getLatitude(), poi
                                .getLatLonPoint().getLongitude()))
                .title(poi.getTitle())
                .snippet(poi.getSnippet())
                .icon(bitmap);
    }

    /**
     * 从marker中得到poi在list的位置。
     *
     * @param marker 一个标记的对象。
     * @return 返回该marker对应的poi在list的位置。
     * @since V2.1.0
     */
    public int getPoiIndex(Marker marker) {
        for (int i = 0; i < mMarkers.size(); i++) {
            if (mMarkers.get(i).equals(marker)) {
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
    public POI getPOI(int index) {
        if (index < 0 || index >= mPOIs.size()) {
            return null;
        }
        return mPOIs.get(index);
    }

    protected BitmapDescriptor getBitmapDescriptor(int arg0) {
        if (arg0 < POIConstants.MARKERS.length) {
            return BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(),
                            POIConstants.MARKERS[arg0]));
        } else {
            return BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.marker_other_highlight));
        }
    }
}
