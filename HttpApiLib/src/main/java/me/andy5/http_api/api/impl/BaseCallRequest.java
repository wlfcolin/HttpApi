package me.andy5.http_api.api.impl;

import com.google.gson.Gson;

import me.andy5.http_api.api.base.ApiCallRequest;
import me.andy5.http_api.api.base.OkHttpRequest;
import me.andy5.http_api.api.base.RetrofitRequest;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Dns;
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

    private Interceptor mInterceptor;
    private Dns mDns;
    private CookieJar mCookieJar;
    private Gson mGson;

    public BaseCallRequest() {
    }

    @Override
    public void setInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
    }

    @Override
    public void setDns(Dns dns) {
        mDns = dns;
    }

    @Override
    public void setCookieJar(CookieJar cookieJar) {
        mCookieJar = cookieJar;
    }

    @Override
    public void setGson(Gson gson) {
        mGson = gson;
    }

    @Override
    public Dns getDns() {
        return mDns;
    }

    @Override
    public CookieJar getCookieJar() {
        return mCookieJar;
    }

    @Override
    public Interceptor getInterceptor() {
        return mInterceptor;
    }

    @Override
    public Call getCall(OkHttpClient okHttpClient) {
        return null;
    }

    @Override
    public Observable<R> getObservable(Retrofit retrofit) {
        return null;
    }

    @Override
    public Gson getGson() {
        return mGson;
    }
}
