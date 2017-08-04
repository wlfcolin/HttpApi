# HttpApi
a wrap api manager of current popular http libs, such as okhttp, it makes then easy to use.


----------------------------------------------------------------------

**Quick start**
* 1. add in dependencies in your app module's build.gradle
``` java
compile 'me.andy5:HttpApi:0.2.0'
```

* 2. create a CallRequest extends BaseCallRequest
``` java
public class YourCallRequest extends BaseCallRequest<YourApiResponse> {
}
```

* 3. Override Call getCall(OkHttpClient okHttpClient) or Observable<R> getObservable(Retrofit retrofit) to implements your request
``` java
public class YourCallRequest extends BaseCallRequest<YourApiResponse> {

    // just select one to return non-null, getCall or getObservable, it will use getCall if both implements

    @Override
    public Call getCall(OkHttpClient okHttpClient) {
        return YourCall;
    }

    @Override
    public Observable<YourApiResponse> getObservable(Retrofit retrofit) {
        return YourObservable<YourApiResponse>;
    }
}
```


* 4. use ApiManager to send CallRequest
``` java
YourCallRequest callRequest = new YourCallRequest();
callRequest.setApiCallback(new SimpleApiCallback<YourApiResponse>() {

    @Override
    public void onSucceed(@NonNull YourApiResponse result) {
    }

    @Override
    public boolean canDoCallback() {
        return !getActivity().isDestroyed();
    }

    @Override
    public void onFailed(@NonNull Throwable e) {
    }
});
ApiManager.getInstance().sendApiRequest(request, this);

```


* 5. if your component is destroy or finish, cancel the request
``` java
@Override
protected void onDestroy() {
    super.onDestroy();
    ApiManager.getInstance().cancelApiRequest(this);
}
```

----------------------------------------------------------------------
**LICENSE**
```
Copyright 2017 wlfcolin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

