package com.xmx.androidmapbase.Tools.POI;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.xmx.androidmapbase.R;
import com.xmx.androidmapbase.Tools.Map.BaseOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2016/11/28.
 */

public class POIOverlay extends BaseOverlay {
    private List<PoiItem> mPOIs;

    public POIOverlay(AMap amap, List<PoiItem> POIs, Context context) {
        super(amap, context);
        mPOIs = POIs;
    }

    public void addAllToMap() {
        for (int i = 0; i < mPOIs.size(); i++) {
            Marker marker = addToMap(getMarkerOptions(i));
            PoiItem item = mPOIs.get(i);
            marker.setObject(item);
        }
    }

    private LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < mPOIs.size(); i++) {
            b.include(new LatLng(mPOIs.get(i).getLatLonPoint().getLatitude(),
                    mPOIs.get(i).getLatLonPoint().getLongitude()));
        }
        return b.build();
    }

    private MarkerOptions getMarkerOptions(int index) {
        return new MarkerOptions()
                .position(
                        new LatLng(mPOIs.get(index).getLatLonPoint()
                                .getLatitude(), mPOIs.get(index)
                                .getLatLonPoint().getLongitude()))
                .title(getTitle(index))
                .snippet(getSnippet(index))
                .icon(getBitmapDescriptor(index));
    }

    protected String getTitle(int index) {
        return mPOIs.get(index).getTitle();
    }

    protected String getSnippet(int index) {
        return mPOIs.get(index).getSnippet();
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
    public PoiItem getPoiItem(int index) {
        if (index < 0 || index >= mPOIs.size()) {
            return null;
        }
        return mPOIs.get(index);
    }

    protected BitmapDescriptor getBitmapDescriptor(int arg0) {
        if (arg0 < POIConstants.MARKERS.length) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(),
                            POIConstants.MARKERS[arg0]));
            return icon;
        } else {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.marker_other_highlight));
            return icon;
        }
    }
}
