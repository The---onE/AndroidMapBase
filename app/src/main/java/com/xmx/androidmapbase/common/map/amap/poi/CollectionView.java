package com.xmx.androidmapbase.common.map.amap.poi;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xmx.androidmapbase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2016/12/24.
 */

public class CollectionView {

    private List<Marker> collectMarkers = new ArrayList<>();

    private AMap mAMap;
    private Context mContext;

    public CollectionView(Context context, AMap map) {
        mContext = context;
        mAMap = map;
    }

    public void addCollection(POI poi) {
        addCollection(poi, R.drawable.collection);
    }

    public void addCollection(POI poi, int iconId) {
        addCollection(poi, iconId, 0.5f, 1f);
    }

    public void addCollection(POI poi, int iconId, float anchorX, float anchorY) {
        MarkerOptions m = new MarkerOptions()
                .position(new LatLng(poi.getLatLonPoint().getLatitude(),
                        poi.getLatLonPoint().getLongitude()))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                mContext.getResources(),
                                iconId)))
                .anchor(anchorX, anchorY);
        Marker marker = mAMap.addMarker(m);
        marker.setObject(poi);
        collectMarkers.add(marker);
    }

    public boolean isCollect(Marker marker) {
        return collectMarkers.contains(marker);
    }
}
