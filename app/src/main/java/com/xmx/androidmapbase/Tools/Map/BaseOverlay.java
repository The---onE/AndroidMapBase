package com.xmx.androidmapbase.Tools.Map;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by The_onE on 2016/11/29.
 */

public class BaseOverlay {
    protected AMap mAMap;
    protected ArrayList<Marker> mMarkers = new ArrayList<>();
    protected Context mContext;

    public BaseOverlay(AMap amap, Context context) {
        mAMap = amap;
        mContext = context;
    }

    public Marker addToMap(MarkerOptions marker) {
        Marker m = mAMap.addMarker(marker);
        mMarkers.add(m);
        return m;
    }

    public void removeAllFromMap() {
        for (Marker mark : mMarkers) {
            mark.remove();
        }
    }
}
