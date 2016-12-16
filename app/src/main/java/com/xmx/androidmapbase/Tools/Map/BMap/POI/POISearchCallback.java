package com.xmx.androidmapbase.Tools.Map.BMap.POI;

import com.baidu.mapapi.search.core.CityInfo;

import java.util.List;

/**
 * Created by The_onE on 2016/5/31.
 */
public abstract class POISearchCallback {

    public abstract void success(List<POI> poiItems);

    public abstract void suggest(List<CityInfo> cities);

    public abstract void noData();
}
