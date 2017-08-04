package me.andy5.http_api.util;

import android.os.Looper;

/**
 * main thread util
 *
 * @author wlf(Andy)
 * @datetime 2016-04-18 15:32 GMT+8
 * @email 411086563@qq.com
 */
public class ThreadUtil {

    /**
     * whether or not main thread
     *
     * @return true means main thread
     */
    public static final boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
