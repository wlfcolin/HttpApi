package me.andy5.http_api.operation.base;

import android.support.annotation.NonNull;

/**
 * operate callback
 *
 * @author wlf(Andy)
 * @datetime 2016-04-25 09:59 GMT+8
 * @email 411086563@qq.com
 */
public interface OperateCallback<R> {

    // make sure the order is: onStarted----onSucceed/onFailed/onCanceled----onCompleted

    /**
     * started
     */
    void onStarted();

    /**
     * success
     *
     * @param result result
     */
    void onSucceed(@NonNull R result);

    /**
     * failure
     *
     * @param e exception
     */
    void onFailed(@NonNull Throwable e);

    /**
     * canceled
     */
    void onCanceled();

    /**
     * completed
     */
    void onCompleted();
}
