// Copyright 2023 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.tool;

import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import okio.ByteString;

/**
 * @author sunlonggen
 * @date 2023/3/21 15:39
 **/
public class SseClient {

    public static void ssePost(String url, String params, EventSourceListener listener) throws Exception {
        //创建OkHttp客户端
        OkHttpClient okHttpClient = new OkHttpClient.Builder().
                connectTimeout(600, TimeUnit.SECONDS) // 连接超时时间
                .writeTimeout(600, TimeUnit.SECONDS)      // 写超时时间
                .readTimeout(600, TimeUnit.SECONDS).build();    // 读超时时间;
        //创建请求对象
        ByteString byteString = ByteString.encodeUtf8(params);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), byteString);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        Request request = requestBuilder.build();
        EventSources.createFactory(okHttpClient).newEventSource(request, listener);
    }

}
