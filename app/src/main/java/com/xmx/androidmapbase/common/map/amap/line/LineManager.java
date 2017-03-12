package com.xmx.androidmapbase.common.map.amap.line;

import com.xmx.androidmapbase.common.data.cloud.BaseCloudEntityManager;

/**
 * Created by The_onE on 2016/12/1.
 */

public class LineManager extends BaseCloudEntityManager<Line> {
    private static LineManager instance;

    public synchronized static LineManager getInstance() {
        if (null == instance) {
            instance = new LineManager();
        }
        return instance;
    }

    private LineManager() {
        tableName = "AMapLine";
        entityTemplate = new Line(null, null, null, 0, 0);
        //userField = "";
    }
}
