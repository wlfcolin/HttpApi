package me.andy5.http_api.api.https;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Https HostName Verifier
 *
 * @author wlf(Andy)
 * @datetime 2016-04-21 10:30 GMT+8
 * @email 411086563@qq.com
 */
public class HttpsHostNameVerifier implements HostnameVerifier {

    private List<String> mNotVerifyHostNames = new ArrayList<>();

    public HttpsHostNameVerifier(List<String> notVerifyHostNames) {
        if (notVerifyHostNames != null) {
            mNotVerifyHostNames = notVerifyHostNames;
        }
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        // ignore the Hostname in mNotVerifyHostNames
        for (String ignoreHostname : mNotVerifyHostNames) {
            if (TextUtils.isEmpty(ignoreHostname)) {
                continue;
            }
            if (ignoreHostname.equalsIgnoreCase(hostname)) {
                // find ignore
                return true;
            }
        }

        // use default OkHostnameVerifier to verify
        return OkHostnameVerifier.INSTANCE.verify(hostname, session);
    }
}
