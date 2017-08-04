package me.andy5.http_api.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author andy(Andy)
 * @datetime 2016-07-27 10:14 GMT+8
 * @email 411086563@qq.com
 */
public class OkHttpUtil {

    // request body to string
    public static String bodyToString(Request request) {
        String result = "";
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            result = buffer.readUtf8();
            result = URLDecoder.decode(result, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // response body to string
    public static String bodyToString(ResponseBody responseBody) {
        String result = "";
        try {
            BufferedSource source = responseBody.source();
            long contentLength = responseBody.contentLength();
            if (contentLength <= 0) {
                // buffer the entire body
                contentLength = Long.MAX_VALUE;
            }
            source.request(contentLength); // buffer the entire body
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(charset);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            result = buffer.clone().readString(charset);
            result = URLDecoder.decode(result, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
