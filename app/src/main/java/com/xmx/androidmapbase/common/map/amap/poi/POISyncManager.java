package com.xmx.androidmapbase.common.map.amap.poi;

import com.xmx.androidmapbase.common.data.sync.BaseSyncEntityManager;

/**
 * Created by The_onE on 2016/12/1.
 */

public class POISyncManager extends BaseSyncEntityManager<POI> {
    private static POISyncManager instance;

    public synchronized static POISyncManager getInstance() {
        if (null == instance) {
            instance = new POISyncManager();
        }
        return instance;
    }

    private POISyncManager() {
        setTableName("CustomSyncPOI");
        setEntityTemplate(new POI(null, null, null, null, null));
        setUserField("User");
        //userField = "";
    }
}
