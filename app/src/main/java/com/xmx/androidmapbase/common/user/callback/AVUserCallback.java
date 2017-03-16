package com.xmx.androidmapbase.common.user.callback;

import com.avos.avoscloud.AVException;

/**
 * Created by The_onE on 2016/1/14.
 */
public abstract class AVUserCallback {

    public abstract void success();

    public abstract void error(AVException e);
}
