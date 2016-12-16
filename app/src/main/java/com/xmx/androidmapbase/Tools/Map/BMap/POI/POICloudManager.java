package com.xmx.androidmapbase.Tools.Map.BMap.POI;

import com.xmx.androidmapbase.Tools.Data.Cloud.BaseCloudEntityManager;

/**
 * Created by The_onE on 2016/12/1.
 */

public class POICloudManager extends BaseCloudEntityManager<POI> {
    private static POICloudManager instance;

    public synchronized static POICloudManager getInstance() {
        if (null == instance) {
            instance = new POICloudManager();
        }
        return instance;
    }

    private POICloudManager() {
        tableName = "BaiduPOI";
        entityTemplate = new POI(null, null, null, null);
        //userField = "";
    }
}
