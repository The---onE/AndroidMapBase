package com.xmx.androidmapbase.Tools.Map.POI;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

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


    private PoiSearch.Query query;// Poi查询条件类
    private static final int DEFAULT_SEARCH_SIZE = 20;
    private static final int DEFAULT_SEARCH_RADIUS = 5000;

    /**
     * 开始进行poi搜索
     */
    public void searchPOIQuery(LatLng location, int radius,
                                 int page, int size,
                                 String keyword, String type, String area,
                                 final POISearchCallback callback) {
        if (size <= 0) {
            size = DEFAULT_SEARCH_SIZE;
        }
        if (radius <= 0) {
            radius = DEFAULT_SEARCH_RADIUS;
        }
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyword, type, area);
        query.setPageSize(size);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);// 设置查第一页

        PoiSearch poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int poiCode) {
                if (poiCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                        if (poiResult.getQuery().equals(query)) {// 是否是同一条
                            List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                            List<SuggestionCity> suggestionCities = poiResult
                                    .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                            if (poiItems != null && poiItems.size() > 0) {
                                callback.success(poiItems);
                            } else if (suggestionCities != null
                                    && suggestionCities.size() > 0) {
                                callback.suggest(suggestionCities);
                            } else {
                                callback.noData();
                            }
                        }
                    } else {
                        callback.noData();
                    }
                } else {
                    callback.error(poiCode);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poiSearch.setBound(new PoiSearch.SearchBound(
                new LatLonPoint(location.latitude, location.longitude),
                radius, true));//
        // 设置搜索区域为以当前位置为圆心，其周围范围
        poiSearch.searchPOIAsyn();// 异步搜索
    }
}
