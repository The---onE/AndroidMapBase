package com.xmx.androidmapbase.common.map.amap.line;

import android.content.ContentValues;
import android.database.Cursor;

import com.amap.api.maps.model.LatLng;
import com.avos.avoscloud.AVObject;
import com.xmx.androidmapbase.common.data.cloud.ICloudEntity;
import com.xmx.androidmapbase.common.data.sql.ISQLEntity;
import com.xmx.androidmapbase.common.data.sync.ISyncEntity;

import java.util.Date;

/**
 * Created by The_onE on 2017/3/11.
 */

public class Line implements ISQLEntity, ICloudEntity, ISyncEntity {

    public long mId = -1;
    public String mCloudId = null;
    public LatLng mStart;
    public LatLng mEnd;
    public String mTitle;
    public int mColor;
    public double mWidth;
    public Date mTime;

    public Line(LatLng start, LatLng end, String title, int color, double width) {
        mStart = start;
        mEnd = end;
        mTitle = title;
        mColor = color;
        mWidth = width;

        mTime = new Date();
    }

    @Override
    public String tableFields() {
        return "ID integer not null primary key autoincrement, " + // 0
                "CLOUD_ID text, " + // 1
                "StartLatitude real not null, " + // 2
                "StartLongitude real not null, " + // 3
                "EndLatitude real not null, " + // 4
                "EndLongitude real not null, " + // 5
                "Title text, " + // 6
                "Color integer not null default(-16777216), " + // 7
                "Width real, " + // 8
                "Time integer not null default(0)"; // 9
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
        content.put("StartLatitude", mStart.latitude);
        content.put("StartLongitude", mStart.longitude);
        content.put("EndLatitude", mEnd.latitude);
        content.put("EndLongitude", mEnd.longitude);
        content.put("Title", mTitle);
        content.put("Color", mColor);
        content.put("Width", mWidth);
        content.put("Time", mTime.getTime());
        return content;
    }

    @Override
    public Line convertToEntity(Cursor c) {
        long id = c.getLong(0);
        String cloudId = c.getString(1);
        double sLatitude = c.getDouble(2);
        double sLongitude = c.getDouble(3);
        double eLatitude = c.getDouble(4);
        double eLongitude = c.getDouble(5);
        String title = c.getString(6);
        int color = c.getInt(7);
        double width = c.getDouble(8);
        Date time = new Date(c.getLong(9));

        Line line = new Line(new LatLng(sLatitude, sLongitude),
                new LatLng(eLatitude, eLongitude),
                title, color, width);
        line.mId = id;
        line.mCloudId = cloudId;
        line.mTime = time;

        return line;
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
        object.put("startLatitude", mStart.latitude);
        object.put("startLongitude", mStart.longitude);
        object.put("endLatitude", mEnd.latitude);
        object.put("endLongitude", mEnd.longitude);
        object.put("title", mTitle);
        object.put("color", mColor);
        object.put("width", mWidth);
        object.put("time", mTime);

        return object;
    }

    @Override
    public Line convertToEntity(AVObject object) {
        long id = object.getLong("Id");
        String cloudId = object.getObjectId();
        double sLatitude = object.getDouble("startLatitude");
        double sLongitude = object.getDouble("startLongitude");
        double eLatitude = object.getDouble("endLatitude");
        double eLongitude = object.getDouble("endLongitude");
        String title = object.getString("title");
        int color = object.getInt("color");
        double width = object.getDouble("width");
        Date time = object.getDate("time");

        Line line = new Line(new LatLng(sLatitude, sLongitude),
                new LatLng(eLatitude, eLongitude),
                title, color, width);
        line.mId = id;
        line.mCloudId = cloudId;
        line.mTime = time;

        return line;
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
