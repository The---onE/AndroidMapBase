package com.xmx.androidmapbase.Tools.Utils;

import com.xmx.androidmapbase.Constants;
import com.xmx.androidmapbase.Tools.OperationLog.OperationLogEntityManager;

/**
 * Created by The_onE on 2016/11/7.
 */

public class ExceptionUtil {
    public static boolean filterException(Exception e) {
        if (e != null && Constants.EXCEPTION_DEBUG) {
            e.printStackTrace();
            OperationLogEntityManager.getInstance().addLog("" + e);
            return false;
        } else {
            return true;
        }
    }
}
