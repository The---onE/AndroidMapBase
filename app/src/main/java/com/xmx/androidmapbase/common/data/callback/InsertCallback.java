package com.xmx.androidmapbase.common.data.callback;

import com.avos.avoscloud.AVException;
import com.xmx.androidmapbase.common.user.UserData;

/**
 * Created by The_onE on 2016/5/29.
 */
public abstract class InsertCallback {

    public abstract void success(UserData user, String objectId);

    public abstract void syncError(int error);

    public abstract void syncError(AVException e);
}
