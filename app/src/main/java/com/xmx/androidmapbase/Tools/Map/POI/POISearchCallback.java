package com.xmx.androidmapbase.Tools.Map.POI;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by The_onE on 2016/5/31.
 */
public abstract class POISearchCallback {

    public abstract void success(List<PoiItem> poiItems);

    public abstract void suggest(List<SuggestionCity> cities);

    public abstract void noData();

    public abstract void error(int code);
}
