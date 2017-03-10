package com.xmx.androidmapbase.common.map.amap.poi;

import com.xmx.androidmapbase.common.data.cloud.BaseCloudEntityManager;

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
        tableName = "CustomPOI";
        entityTemplate = new POI(null, null, null, null, null);
        //userField = "";
    }
}
