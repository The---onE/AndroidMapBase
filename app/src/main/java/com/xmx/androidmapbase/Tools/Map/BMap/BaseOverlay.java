package com.xmx.androidmapbase.Tools.Map.BMap;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by The_onE on 2016/11/29.
 */

public class BaseOverlay {
    protected BaiduMap mBMap;
    protected ArrayList<Marker> mMarkers = new ArrayList<>();
    protected Context mContext;

    public BaseOverlay(BaiduMap bmap, Context context) {
        mBMap = bmap;
        mContext = context;
    }

    public Marker addToMap(MarkerOptions marker) {
        Marker m = (Marker) mBMap.addOverlay(marker);
        mMarkers.add(m);
        return m;
    }

    public void removeAllFromMap() {
        for (Marker mark : mMarkers) {
            mark.remove();
        }
    }
}
