package com.xmx.androidmapbase.Tools.Map.BMap.POI;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2016/2/21.
 */
public class POIManager {
    private static POIManager instance;

    public synchronized static POIManager getInstance() {
        if (null == instance) {
            instance = new POIManager();
        }
        return instance;
    }

    private Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }

    private static final int DEFAULT_SEARCH_SIZE = 20;
    private static final int DEFAULT_SEARCH_RADIUS = 2500;

    /**
     * 开始进行poi搜索
     */
    public void searchPOIQuery(LatLng location, int radius,
                               int page, int size,
                               String keyword,
                               final POISearchCallback callback) {
        if (size <= 0) {
            size = DEFAULT_SEARCH_SIZE;
        }
        if (radius <= 0) {
            radius = DEFAULT_SEARCH_RADIUS;
        }

        final PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                List<PoiInfo> res = poiResult.getAllPoi();
                List<CityInfo> suggestionCities = poiResult
                        .getSuggestCityList();
                if (res != null) {
                    List<POI> poiList = convertPOIList(res);
                    if (poiList != null && poiList.size() > 0) {
                        callback.success(poiList);
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        callback.suggest(suggestionCities);
                    } else {
                        callback.noData();
                    }
                } else {
                    if (suggestionCities != null && suggestionCities.size() > 0) {
                        callback.suggest(suggestionCities);
                    } else {
                        callback.noData();
                    }
                }

                poiSearch.destroy();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

        poiSearch.searchNearby(new PoiNearbySearchOption()
                .location(location)
                .radius(radius)
                .keyword(keyword)
                .pageNum(page)
                .pageCapacity(size)
        );
    }

    private List<POI> convertPOIList(List<PoiInfo> list) {
        List<POI> poiList = new ArrayList<>();
        for (PoiInfo poiItem : list) {
            poiList.add(new POI(poiItem));
        }
        return poiList;
    }
}
