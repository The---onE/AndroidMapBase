package com.xmx.androidmapbase.common.map.amap.poi;

import android.content.ContentValues;
import android.database.Cursor;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.common.data.cloud.ICloudEntity;
import com.xmx.androidmapbase.common.data.sql.ISQLEntity;
import com.xmx.androidmapbase.common.data.sync.ISyncEntity;

import java.util.Date;

/**
 * Created by The_onE on 2016/12/1.
 */

public class POI extends PoiItem implements ISQLEntity, ICloudEntity, ISyncEntity {

    public long mId = -1;
    public String mCloudId = null;
    public Date mTime;
    public String mType;
    public String mTitle;
    public String mContent;

    public POI(String id,
               LatLonPoint point,
               String title,
               String snippet,
               String type) {
        super(id, point, title, snippet);
        mTime = new Date();
        mType = type;
    }

    public POI(PoiItem o) {
        super(o.getPoiId(), o.getLatLonPoint(), o.getTitle(), o.getSnippet());
        mTime = new Date();
    }

    @Override
    public String tableFields() {
        return "ID integer not null primary key autoincrement, " +
                "CLOUD_ID text, " +
                "PoiId text, " +
                "Latitude real not null, " +
                "Longitude real not null, " +
                "Title text, " +
                "Snippet text, " +
                "type text, " +
                "Time integer not null default(0)";
    }

    @Override
    public ContentValues getContent() {
        ContentValues content = new ContentValues();
        if (mId > 0) {
            content.put("ID", mId);
        }
        if (mCloudId != null) {
            content.put("CLOUD_ID", mCloudId);
        }
        content.put("PoiId", getPoiId());
        content.put("Latitude", getLatLonPoint().getLatitude());
        content.put("Longitude", getLatLonPoint().getLongitude());
        content.put("Title", mTitle != null ? mTitle : getTitle());
        content.put("Snippet", mContent!= null ? mContent : getSnippet());
        content.put("Type", mType);
        content.put("Time", mTime.getTime());
        return content;
    }

    @Override
    public POI convertToEntity(Cursor c) {
        long id = c.getLong(0);
        String cloudId = c.getString(1);
        String PoiId = c.getString(2);
        double latitude = c.getDouble(3);
        double longitude = c.getDouble(4);
        String title = c.getString(5);
        String snippet = c.getString(6);
        String type = c.getString(7);
        Date time = new Date(c.getLong(8));

        POI entity = new POI(PoiId, new LatLonPoint(latitude, longitude), title, snippet, type);
        entity.mId = id;
        entity.mCloudId = cloudId;
        entity.mTime = time;

        return entity;
    }

    @Override
    public AVObject getContent(String tableName) {
        AVObject object = new AVObject(tableName);
        if (mCloudId != null) {
            object.setObjectId(mCloudId);
        }
        if (mId > 0) {
            object.put("Id", mId);
        }
        object.put("PoiId", getPoiId());
        object.put("Latitude", getLatLonPoint().getLatitude());
        object.put("Longitude", getLatLonPoint().getLongitude());
        object.put("Title", mTitle != null ? mTitle : getTitle());
        object.put("Snippet", mContent!= null ? mContent : getSnippet());
        object.put("Type", mType);
        object.put("Time", mTime);

        return object;
    }

    @Override
    public POI convertToEntity(AVObject object) {
        long id = object.getLong("Id");
        String cloudId = object.getObjectId();
        String PoiId = object.getString("PoiId");
        double latitude = object.getDouble("Latitude");
        double longitude = object.getDouble("Longitude");
        String title = object.getString("Title");
        String snippet = object.getString("Snippet");
        String type = object.getString("Type");
        Date time = object.getDate("Time");

        POI entity = new POI(PoiId, new LatLonPoint(latitude, longitude), title, snippet, type);
        entity.mId = id;
        entity.mCloudId = cloudId;
        entity.mTime = time;

        return entity;
    }

    @Override
    public String getCloudId() {
        return mCloudId;
    }

    @Override
    public void setCloudId(String id) {
        mCloudId = id;
    }
}
