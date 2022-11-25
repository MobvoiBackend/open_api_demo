//Copyright 2020 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.example;

import com.mobvoi.open.api.tool.HttpClientUtil;
import com.mobvoi.open.api.tool.SignatureUtil;
import java.util.HashMap;

/**
 * @author qhsong
 * @since 2020-07-22
 * 调用mobvoi开放平台nlp接口demo
 */
public class NlpExample {

  private static final String NLP_URL = "https://open.mobvoi.com/api/nlp/v1";

  private static final String APPKEY = "你的appkey";

  private static final String SECRET = "你的secret";

  public static void main(String[] args) {

    HashMap<String,String> params = new HashMap();
    params.put("query","北京到上海的火车");
    params.put("appkey",APPKEY);
    String timestamp = System.currentTimeMillis()/1000 +"";
    params.put("timestamp",timestamp);
    params.put("signature", SignatureUtil.getSignature(APPKEY,SECRET,timestamp));
    String result = HttpClientUtil.doGet(NLP_URL, params);
    System.out.println(result);

  }
}
