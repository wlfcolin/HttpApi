package me.andy5.http_api.api.impl;

import me.andy5.http_api.api.base.ApiCallRequest;
import me.andy5.http_api.api.base.OkHttpRequest;
import me.andy5.http_api.api.base.RetrofitRequest;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * base CallRequest
 *
 * @author andy(Andy)
 * @datetime 2017-08-04 14:04 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseCallRequest<R> extends ApiCallRequest<R> implements OkHttpRequest, RetrofitRequest<R> {

    public BaseCallRequest() {
    }

    @Override
    public Interceptor getInterceptor() {
        return null;
    }

    @Override
    public Call getCall(OkHttpClient okHttpClient) {
        return null;
    }

    @Override
    public Observable<R> getObservable(Retrofit retrofit) {
        return null;
    }
}
