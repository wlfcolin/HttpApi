package me.andy5.http_api.api.base;

import okhttp3.Interceptor;

/**
 * Request interceptor
 *
 * @author andy(Andy)
 * @datetime 2017-07-07 12:24 GMT+8
 * @email 411086563@qq.com
 */
public interface InterceptorRequest {

    /**
     * 获取拦截器
     *
     * @return
     */
    Interceptor getInterceptor();
}
