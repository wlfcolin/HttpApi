package me.andy5.http_api.api.base;

import com.google.gson.Gson;

import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.Interceptor;

/**
 * OkHttp Engine
 *
 * @author andy(Andy)
 * @datetime 2017-07-07 12:24 GMT+8
 * @email 411086563@qq.com
 */
public interface OkHttpEngine {

    void setInterceptor(Interceptor interceptor);

    void setDns(Dns dns);

    void setCookieJar(CookieJar cookieJar);

    void setGson(Gson gson);

    Interceptor getInterceptor();

    Dns getDns();

    CookieJar getCookieJar();

    Gson getGson();
}
