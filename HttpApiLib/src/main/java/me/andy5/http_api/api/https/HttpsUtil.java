package me.andy5.http_api.api.https;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Https utils
 *
 * @author wlf(Andy)
 * @datetime 2016-02-19 14:56 GMT+8
 * @email 411086563@qq.com
 */
public class HttpsUtil {

    /**
     * get HostnameVerifier
     *
     * @return HostnameVerifier
     */
    public static HostnameVerifier getHostnameVerifier(List<String> notVerifyHostNames) {
        return new HttpsHostNameVerifier(notVerifyHostNames);
    }

    /**
     * get double SSLSocketFactory
     *
     * @param needTrustServerCerts need to trust server public certs
     * @param clientBks            client bks cert
     * @param clientBksPassword    client bks cert password
     * @return double SSLSocketFactory
     */
    public static SSLSocketFactory getSslSocketFactory(InputStream[] needTrustServerCerts, InputStream clientBks,
                                                       String clientBksPassword) {
        try {
            // trust server certs list manager
            TrustManager[] trustManagers = prepareTrustManager(needTrustServerCerts);
            // get client bks manager
            KeyManager[] keyManagers = prepareKeyManager(clientBks, clientBksPassword);
            // SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // init
            sslContext.init(keyManagers, new TrustManager[]{new DefaultTrustManager(chooseX509TrustManager
                    (trustManagers))}, new SecureRandom());
            // get the SSLSocketFactory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * get single SSLSocketFactory
     *
     * @param needTrustServerCerts need to trust server public certs
     * @return single SSLSocketFactory
     */
    public static SSLSocketFactory getSslSocketFactory(InputStream[] needTrustServerCerts) {
        return getSslSocketFactory(needTrustServerCerts, null, null);
    }

    private static TrustManager[] prepareTrustManager(InputStream[] needTrustServerCerts) {

        if (needTrustServerCerts == null || needTrustServerCerts.length <= 0) {
            return null;
        }

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            int index = 0;
            for (InputStream certificate : needTrustServerCerts) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {

        if (bksFile == null || password == null) {
            return null;
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(bksFile, password.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm
                    ());
            keyManagerFactory.init(keyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static X509TrustManager chooseX509TrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static class DefaultTrustManager implements X509TrustManager {

        private X509TrustManager mAllX509TrustManager;
        private X509TrustManager mLocalTrustManager;

        public DefaultTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException,
                KeyStoreException {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            mAllX509TrustManager = chooseX509TrustManager(trustManagerFactory.getTrustManagers());
            this.mLocalTrustManager = localTrustManager;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                mAllX509TrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException e) {
                e.printStackTrace();
                try {
                    mLocalTrustManager.checkServerTrusted(chain, authType);
                } catch (CertificateException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
