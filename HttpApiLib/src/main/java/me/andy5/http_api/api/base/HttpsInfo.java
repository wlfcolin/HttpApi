package me.andy5.http_api.api.base;

import java.io.InputStream;
import java.util.List;

/**
 * @author andy(Andy)
 * @datetime 2017-08-07 10:24 GMT+8
 * @email 411086563@qq.com
 */
public class HttpsInfo {

    private InputStream[] mNeedTrustServerCerts;
    private InputStream mClientBks;
    private String mClientBksPassword;
    private List<String> mNotVerifyHostNames;

    public HttpsInfo(InputStream[] needTrustServerCerts, InputStream clientBks, String clientBksPassword,
                     List<String> notVerifyHostNames) {
        mNeedTrustServerCerts = needTrustServerCerts;
        mClientBks = clientBks;
        mClientBksPassword = clientBksPassword;
        mNotVerifyHostNames = notVerifyHostNames;
    }

    public InputStream[] getNeedTrustServerCerts() {
        return mNeedTrustServerCerts;
    }

    public InputStream getClientBks() {
        return mClientBks;
    }

    public String getClientBksPassword() {
        return mClientBksPassword;
    }

    public List<String> getNotVerifyHostNames() {
        return mNotVerifyHostNames;
    }

    public boolean isInit() {
        return mNeedTrustServerCerts != null;
    }
}
