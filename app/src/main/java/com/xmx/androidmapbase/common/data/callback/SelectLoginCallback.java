package com.xmx.androidmapbase.common.data.callback;

import com.avos.avoscloud.AVException;
import com.xmx.androidmapbase.common.data.cloud.ICloudEntity;
import com.xmx.androidmapbase.common.user.UserData;

import java.util.List;

/**
 * Created by The_onE on 2016/5/29.
 */
public abstract class SelectLoginCallback<Entity extends ICloudEntity> {

    public abstract void success(UserData user, List<Entity> entities);

    public abstract void syncError(int error);

    public abstract void syncError(AVException e);
}
