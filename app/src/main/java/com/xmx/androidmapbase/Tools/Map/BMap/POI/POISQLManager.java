package com.xmx.androidmapbase.Tools.Map.BMap.POI;

import com.xmx.androidmapbase.Tools.Data.SQL.BaseSQLEntityManager;

/**
 * Created by The_onE on 2016/12/1.
 */

public class POISQLManager extends BaseSQLEntityManager<POI> {
    private static POISQLManager instance;

    public synchronized static POISQLManager getInstance() {
        if (null == instance) {
            instance = new POISQLManager();
        }
        return instance;
    }

    private POISQLManager() {
        tableName = "BaiduPOI";
        entityTemplate = new POI(null, null, null, null);
        openDatabase();
    }
}
