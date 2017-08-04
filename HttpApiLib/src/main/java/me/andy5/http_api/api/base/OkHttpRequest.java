package me.andy5.http_api.api.base;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * OkHttp Request
 *
 * @author andy(Andy)
 * @datetime 2017-06-22 09:26 GMT+8
 * @email 411086563@qq.com
 */
public interface OkHttpRequest extends InterceptorRequest {

    /**
     * get OkHttp Call
     *
     * @param okHttpClient
     * @return OkHttp Call
     */
    Call getCall(OkHttpClient okHttpClient);
}
