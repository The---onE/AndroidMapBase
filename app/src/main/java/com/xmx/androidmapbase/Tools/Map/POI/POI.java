package com.xmx.androidmapbase.Tools.Map.POI;

import android.content.ContentValues;
import android.database.Cursor;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.xmx.androidmapbase.Tools.Data.SQL.ISQLEntity;

import java.util.Date;

/**
 * Created by The_onE on 2016/12/1.
 */

public class POI extends PoiItem implements ISQLEntity {

    public long mId = -1;
    public Date mTime;

    public POI(String id,
               LatLonPoint point,
               String title,
               String snippet) {
        super(id, point, title, snippet);
        mTime = new Date();
    }

    public POI(PoiItem o) {
        super(o.getPoiId(), o.getLatLonPoint(), o.getTitle(), o.getSnippet());
        mTime = new Date();
    }

    @Override
    public String tableFields() {
        return "ID integer not null primary key autoincrement, " +
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
        content.put("PoiId", getPoiId());
        content.put("Latitude", getLatLonPoint().getLatitude());
        content.put("Longitude", getLatLonPoint().getLongitude());
        content.put("Title", getTitle());
        content.put("Snippet", getSnippet());
        content.put("Time", mTime.getTime());
        return content;
    }

    @Override
    public POI convertToEntity(Cursor c) {
        long id = c.getLong(0);
        String PoiId = c.getString(1);
        double latitude = c.getDouble(2);
        double longitude = c.getDouble(3);
        String title = c.getString(4);
        String snippet = c.getString(5);
        Date time = new Date(c.getLong(6));

        POI entity = new POI(PoiId, new LatLonPoint(latitude, longitude), title, snippet);
        entity.mId = id;
        entity.mTime = time;

        return entity;
    }
}
