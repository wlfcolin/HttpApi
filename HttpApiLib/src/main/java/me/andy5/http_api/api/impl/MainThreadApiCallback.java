package me.andy5.http_api.api.impl;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import me.andy5.http_api.api.base.ApiCallback;
import me.andy5.http_api.operation.base.OperateCallback;
import me.andy5.http_api.util.ThreadUtil;


/**
 * main thread api callback
 *
 * @author wlf(Andy)
 * @datetime 2016-04-25 10:14 GMT+8
 * @email 411086563@qq.com
 */
public class MainThreadApiCallback<R> implements ApiCallback<R> {

    private OperateCallback<R> mCallback;

    public MainThreadApiCallback(@NonNull OperateCallback<R> callback) {
        mCallback = callback;
    }

    @Override
    public void onStarted() {
        if (mCallback == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            mCallback.onStarted();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mCallback != null) {
                            mCallback.onStarted();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onSucceed(@NonNull final R result) {
        if (mCallback == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            mCallback.onSucceed(result);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mCallback != null) {
                            mCallback.onSucceed(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onFailed(@NonNull final Throwable e) {
        if (mCallback == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            mCallback.onFailed(e);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mCallback != null) {
                            mCallback.onFailed(e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onCanceled() {
        if (mCallback == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            mCallback.onCanceled();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mCallback != null) {
                            mCallback.onCanceled();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onCompleted() {
        if (mCallback == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            mCallback.onCompleted();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mCallback != null) {
                            mCallback.onCompleted();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
