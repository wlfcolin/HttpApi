package me.andy5.http_api.api.impl;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.andy5.http_api.api.base.ApiCallRequest;
import me.andy5.http_api.api.base.ApiCallback;
import me.andy5.http_api.api.base.OkHttpRequest;
import me.andy5.http_api.api.base.RetrofitRequest;
import me.andy5.http_api.operation.base.Cancelable;
import me.andy5.http_api.util.OkHttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Api manager
 *
 * @author andy(Andy)
 * @datetime 2017-06-22 09:13 GMT+8
 * @email 411086563@qq.com
 */
public class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();

    // single instance
    private static ApiManager sInstance;
    // all cancelable requests
    private Map<Object, List<Cancelable>> mCancelableMap = new HashMap<>();
    // OkHttpClient map
    private Map<String, OkHttpClient> mOkHttpClientMap = new HashMap<>();

    // private constructor
    private ApiManager() {
    }

    /**
     * get single instance
     *
     * @return
     */
    public static ApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ApiManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * send Api request
     *
     * @param request request
     * @param <R>     response
     * @return
     */
    public <R> boolean sendApiRequest(ApiCallRequest<R> request) {
        return sendApiRequest(request, null);
    }

    /**
     * send Api request
     *
     * @param request request
     * @param tag     tag for cancel
     * @param <R>     response
     * @return
     */
    public <R> boolean sendApiRequest(ApiCallRequest<R> request, Object tag) {
        boolean handled = false;
        // OkHttp
        if (request instanceof OkHttpRequest) {
            handled = sendOkHttpRequest(request, tag);
            if (!handled) {
                if (request instanceof RetrofitRequest) {
                    handled = sendRetrofitRequest(request, tag);
                }
            }
        }
        // Retrofit
        else if (request instanceof RetrofitRequest) {
            handled = sendRetrofitRequest(request, tag);
            if (!handled) {
                if (request instanceof OkHttpRequest) {
                    handled = sendOkHttpRequest(request, tag);
                }
            }
        }
        if (handled) {
            // has been handled
        }
        return handled;
    }

    // send OkHttp request
    private <R> boolean sendOkHttpRequest(ApiCallRequest<R> request, Object tag) {
        final ApiCallback<R> callback = request.getApiCallback();
        OkHttpRequest okHttpRequest = (OkHttpRequest) request;
        Interceptor interceptor = okHttpRequest.getInterceptor();
        if (interceptor == null) {
            interceptor = new DefaultUserInterceptor();
        }
        String cacheKey = request.getCacheKey();
        OkHttpClient okHttpClient = mOkHttpClientMap.get(cacheKey);
        Log.w(TAG, "cacheKey:【" + cacheKey + "】okHttpClient:【" + okHttpClient + "】");
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    // connect timeout, ms
                    .connectTimeout(request.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    // read timeout, ms
                    .readTimeout(request.getReadTimeout(), TimeUnit.MILLISECONDS)
                    // user interceptor
                    .addInterceptor(interceptor)
                    // log interceptor
                    .addInterceptor(new LogInterceptor())
                    // build
                    .build();
            mOkHttpClientMap.put(cacheKey, okHttpClient);
        }
        Call call = okHttpRequest.getCall(okHttpClient);
        if (call != null) {
            ApiCall apiCall = new ApiCall(call);
            if (tag != null) {
                List<Cancelable> cancelables = mCancelableMap.get(tag);
                if (cancelables == null) {
                    cancelables = new ArrayList<>();
                    mCancelableMap.put(tag, cancelables);
                }
                cancelables.add(apiCall);
            }
            if (callback != null) {
                callback.onStarted();
            }
            apiCall.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    if (callback != null) {
                        callback.onFailed(e);
                    }
                    if (callback != null) {
                        callback.onCompleted();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null && response.body() != null) {
                        String body = response.body().string();
                        if (callback != null) {
                            // serialize by gson
                            Type type = getGenericTypeParameter(callback, 0);
                            if (type == null) {
                                onFailure(call, new IOException("can not get generic type!"));
                                return;
                            }
                            Gson gson = new Gson();
                            R result = gson.fromJson(body, type);
                            callback.onSucceed(result);
                        }
                        if (callback != null) {
                            callback.onCompleted();
                        }
                    } else {
                        onFailure(call, new IOException("response body is empty!"));
                    }
                }
            });
            return true;
        } else {
            // do not handle
            return false;
        }
    }

    // send Retrofit request
    private <R> boolean sendRetrofitRequest(final ApiCallRequest<R> request, Object tag) {
        final ApiCallback<R> callback = request.getApiCallback();
        RetrofitRequest retrofitRequest = (RetrofitRequest) request;
        Interceptor interceptor = retrofitRequest.getInterceptor();
        if (interceptor == null) {
            interceptor = new DefaultUserInterceptor();
        }
        String cacheKey = request.getCacheKey();
        OkHttpClient okHttpClient = mOkHttpClientMap.get(cacheKey);
        Log.w(TAG, "cacheKey:【" + cacheKey + "】okHttpClient:【" + okHttpClient + "】");
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    // connect timeout, ms
                    .connectTimeout(request.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    // read timeout, ms
                    .readTimeout(request.getReadTimeout(), TimeUnit.MILLISECONDS)
                    // user interceptor
                    .addInterceptor(interceptor)
                    // log interceptor
                    .addInterceptor(new LogInterceptor())
                    // build
                    .build();
            mOkHttpClientMap.put(cacheKey, okHttpClient);
        }
        Retrofit retrofit = new Retrofit.Builder()
                // http client
                .client(okHttpClient)
                // converter factory
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                // call adapter factory
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                // base url
                .baseUrl(request.getBaseUrl())
                //
                .build();
        Observable<R> observable = retrofitRequest.getObservable(retrofit);
        if (observable != null) {
            ApiSubscriber apiSubscriber = new ApiSubscriber(callback);
            if (tag != null) {
                List<Cancelable> cancelables = mCancelableMap.get(tag);
                if (cancelables == null) {
                    cancelables = new ArrayList<>();
                    mCancelableMap.put(tag, cancelables);
                }
                cancelables.add(apiSubscriber);
            }
            observable.subscribe(apiSubscriber);
            return true;
        } else {
            // do not handle
            return false;
        }
    }

    /**
     * cancel Api request, use for component exit, such as Activity/Fragment onDestroy
     *
     * @param tag
     * @return
     */
    public boolean cancelApiRequest(Object tag) {
        if (tag == null) {
            return false;
        }
        List<Cancelable> cancelables = mCancelableMap.get(tag);
        if (cancelables == null) {
            return false;
        }
        List<Cancelable> canceleds = new ArrayList<>();
        for (Cancelable cancelable : cancelables) {
            if (cancelable == null) {
                continue;
            }
            cancelable.cancel();
            canceleds.add(cancelable);
        }
        cancelables.removeAll(canceleds);
        return canceleds.size() > 0;
    }

    /**
     * cancel all Api request, use for app exit
     */
    public void cancelAllApiRequest() {
        Collection<List<Cancelable>> listCollection = mCancelableMap.values();
        Iterator<List<Cancelable>> listIterator = listCollection.iterator();
        while (listIterator.hasNext()) {
            List<Cancelable> cancelables = listIterator.next();
            if (cancelables == null) {
                continue;
            }
            List<Cancelable> canceleds = new ArrayList<>();
            for (Cancelable cancelable : cancelables) {
                if (cancelable == null) {
                    continue;
                }
                cancelable.cancel();
                canceleds.add(cancelable);
            }
            cancelables.removeAll(canceleds);
        }
    }

    /**
     * release
     */
    public void release() {
        cancelAllApiRequest();
        mOkHttpClientMap.clear();
        sInstance = null;
    }

    // get generic type
    private static Type getGenericTypeParameter(Object genericObj, int position) {
        Type type = null;
        Type[] types = genericObj.getClass().getGenericInterfaces();
        if (types != null && types.length > 0) {
            type = types[0];// only support the genericObj with one argument FIXME
        }
        if (type == null) {
            type = genericObj.getClass().getGenericSuperclass();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[position]);
        } else {
            return null;
        }
    }

    // Retrofit Api Subscriber
    private static class ApiSubscriber<T> extends Subscriber<T> implements Cancelable {

        private ApiCallback<T> mApiCallback;

        public ApiSubscriber(@NonNull ApiCallback<T> apiCallback) {
            mApiCallback = apiCallback;
        }

        @Override
        public void onStart() {
            // super.onStart();
            mApiCallback.onStarted();
        }

        @Override
        public void onCompleted() {
            mApiCallback.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            mApiCallback.onFailed(e);
            // onError and onCompleted are exclusive in Subscriber
            onCompleted();
        }

        @Override
        public void onNext(T t) {
            mApiCallback.onSucceed(t);
        }

        @Override
        public void cancel() {
            if (isUnsubscribed()) {
                return;
            }
            unsubscribe();
        }
    }

    // OkHttp Api Call
    private static class ApiCall implements Call, Cancelable {

        private Call mCall;

        public ApiCall(@NonNull Call call) {
            mCall = call;
        }

        @Override
        public Request request() {
            return mCall.request();
        }

        @Override
        public Response execute() throws IOException {
            return mCall.execute();
        }

        @Override
        public void enqueue(Callback responseCallback) {
            mCall.enqueue(responseCallback);
        }

        @Override
        public void cancel() {
            mCall.cancel();
        }

        @Override
        public boolean isExecuted() {
            return mCall.isExecuted();
        }

        @Override
        public boolean isCanceled() {
            return mCall.isCanceled();
        }

        @Override
        public Call clone() {
            return mCall.clone();
        }
    }

    private static String getUrlIdentify(String url) {
        String urlIdentify = url;
        if (url != null) {
            try {
                int start = url.lastIndexOf("/");
                String temp = url.substring(0, start);
                if (!TextUtils.isEmpty(temp)) {
                    start = temp.lastIndexOf("/");
                }
                int end = url.lastIndexOf("?");
                if (end < 0) {
                    end = url.length();
                }
                if (start > 0) {
                    urlIdentify = url.substring(start, end);
                }
            } catch (Exception e) {
            }
        }
        if (TextUtils.isEmpty(urlIdentify)) {
            urlIdentify = url;
        }
        return urlIdentify;
    }

    // log interceptor
    private static class LogInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                Request request = chain.request();
                String url = request.url().toString();

                long startTime = SystemClock.uptimeMillis();
                Response response = chain.proceed(chain.request());
                long finishTime = SystemClock.uptimeMillis();
                long totalTime = finishTime - startTime;

                String urlIdentify = getUrlIdentify(url);
                ResponseBody responseBody = response.body();
                String repBody = OkHttpUtil.bodyToString(responseBody);

                try {
                    Log.e(TAG, "返回数据(原始数据,url标识:" + urlIdentify + ")" + "\n{\n" + request.url().toString() + "\n" +
                            OkHttpUtil.bodyToString(request) + "\n" + repBody + "\n}");
                } catch (Exception e) {
                }

                Log.w(TAG, "请求" + url + "完成,用时:" + totalTime + "ms");

                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return chain.proceed(chain.request());
            }
        }
    }

    /**
     * private static String getFullLog(String msg) {
     * int maxLength = 3 * 1024;
     * String result = "";
     * if (msg.length() > maxLength) {
     * for (int i = 0; i < msg.length(); i += maxLength) {
     * if (i + maxLength < msg.length()) {
     * if (i == 0) {
     * result = msg.substring(i, i + maxLength);
     * } else {
     * result = result + "\n" + msg.substring(i, i + maxLength);
     * }
     * } else {
     * if (i == 0) {
     * result = msg.substring(i, msg.length());
     * } else {
     * result = result + "\n" + msg.substring(i, msg.length());
     * }
     * }
     * }
     * } else {
     * result = msg;
     * }
     * return result;
     * }
     */

    // default user interceptor
    private static class DefaultUserInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request());
        }
    }
}
