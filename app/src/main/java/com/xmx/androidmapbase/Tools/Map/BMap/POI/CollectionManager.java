package com.xmx.androidmapbase.Tools.Map.BMap.POI;

import com.xmx.androidmapbase.Tools.Data.Cloud.BaseCloudEntityManager;

/**
 * Created by The_onE on 2016/12/1.
 */

public class CollectionManager extends BaseCloudEntityManager<POI> {
    private static CollectionManager instance;

    public synchronized static CollectionManager getInstance() {
        if (null == instance) {
            instance = new CollectionManager();
        }
        return instance;
    }

    private CollectionManager() {
        tableName = "BaiduPOI";
        entityTemplate = new POI(null, null, null, null);
        //userField = "";
    }
}
