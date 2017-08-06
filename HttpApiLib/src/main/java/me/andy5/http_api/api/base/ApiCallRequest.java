package me.andy5.http_api.api.base;

/**
 * Api request, for child to extends
 *
 * @param <R> response
 * @author andy(Andy)
 * @datetime 2017-06-22 09:49 GMT+8
 * @email 411086563@qq.com
 */
public abstract class ApiCallRequest<R> implements CacheKey {

    // base url
    private String mBaseUrl;
    // request method
    private String mMethod;
    // connect timeout, ms
    private long mConnectTimeout;
    // read timeout, ms
    private long mReadTimeout;

    // callback
    private ApiCallback<R> mApiCallback;

    // setters

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public void setMethod(String method) {
        mMethod = method;
    }

    public void setConnectTimeout(long connectTimeout) {
        mConnectTimeout = connectTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        mReadTimeout = readTimeout;
    }

    public void setApiCallback(ApiCallback<R> apiCallback) {
        mApiCallback = apiCallback;
    }

    // getters

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public String getMethod() {
        return mMethod;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public ApiCallback<R> getApiCallback() {
        return mApiCallback;
    }

    @Override
    public String getCacheKey() {
        return mConnectTimeout + "#" + mReadTimeout;
    }
}
