package com.xmx.androidmapbase.common.map.bmap.poi;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.xmx.androidmapbase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2016/12/24.
 */

public class CollectionView {
    private List<Marker> collectMarkers = new ArrayList<>();

    private BaiduMap mBMap;
    private Context mContext;

    public CollectionView(Context context, BaiduMap map) {
        mContext = context;
        mBMap = map;
    }

    public void addCollection(POI poi) {
        MarkerOptions m = new MarkerOptions()
                .position(new LatLng(poi.location.latitude,
                        poi.location.longitude))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                mContext.getResources(),
                                R.drawable.point5)))
                .anchor(0.5f, 0.5f)
                .title(poi.name);
        Marker marker = (Marker) mBMap.addOverlay(m);

        Bundle info = new Bundle();
        info.putString("id", poi.mCloudId);
        marker.setExtraInfo(info);

        collectMarkers.add(marker);
    }

    public boolean isCollect(Marker marker) {
        return collectMarkers.contains(marker);
    }
}
