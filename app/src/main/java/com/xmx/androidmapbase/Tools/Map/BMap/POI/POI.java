package com.xmx.androidmapbase.Tools.Map.BMap.POI;

import android.content.ContentValues;
import android.database.Cursor;

import com.avos.avoscloud.AVObject;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.xmx.androidmapbase.Tools.Data.Cloud.ICloudEntity;
import com.xmx.androidmapbase.Tools.Data.SQL.ISQLEntity;
import com.xmx.androidmapbase.Tools.Data.Sync.ISyncEntity;

import java.util.Date;

/**
 * Created by The_onE on 2016/12/1.
 */

public class POI extends PoiInfo implements ISQLEntity, ICloudEntity, ISyncEntity {

    public long mId = -1;
    public String mCloudId = null;
    public Date mTime;

    public POI(String id,
               LatLng point,
               String title,
               String snippet) {
        uid = id;
        location = point;
        name = title;
        address = snippet;
        mTime = new Date();
    }

    public POI(PoiInfo o) {
        name = o.name;
        uid = o.uid;
        address = o.address;
        city = o.city;
        phoneNum = o.phoneNum;
        postCode = o.postCode;
        type = o.type;
        location = o.location;
        hasCaterDetails = o.hasCaterDetails;
        isPano = o.isPano;

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
        content.put("PoiId", uid);
        content.put("Latitude", location.latitude);
        content.put("Longitude", location.longitude);
        content.put("Title", name);
        content.put("Snippet", address);
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
        Date time = new Date(c.getLong(7));

        POI entity = new POI(PoiId, new LatLng(latitude, longitude), title, snippet);
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
        object.put("PoiId", uid);
        object.put("Latitude", location.latitude);
        object.put("Longitude", location.longitude);
        object.put("Title", name);
        object.put("Snippet", address);
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
        Date time = object.getDate("Time");

        POI entity = new POI(PoiId, new LatLng(latitude, longitude), title, snippet);
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
