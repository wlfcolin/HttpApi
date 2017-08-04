package me.andy5.http_api.api.base;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Retrofit Request
 *
 * @param <R> response
 * @author andy(Andy)
 * @datetime 2017-06-22 09:26 GMT+8
 * @email 411086563@qq.com
 */
public interface RetrofitRequest<R> extends InterceptorRequest {

    /**
     * get Retrofit Observable
     *
     * @param retrofit
     * @return Retrofit Observable
     */
    Observable<R> getObservable(Retrofit retrofit);
}
