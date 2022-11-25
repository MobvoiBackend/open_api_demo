//Copyright 2020 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.example;

import com.mobvoi.open.api.tool.HttpClientUtil;
import com.mobvoi.open.api.tool.SignatureUtil;
import java.io.File;
import java.util.HashMap;

/**
 * @author qhsong
 * @since 2020-07-21
 * 调用mobvoi开放平台asr识别接口demo
 */
public class AsrExample {

  private static final String ASR_URL = "https://open.mobvoi.com/api/asr/v1";

  private static final String APPKEY = "你的appkey";

  private static final String SECRET = "你的secret";

  public static void main(String[] args) {
    String filePath = "音频文件路径";
    HashMap params = new HashMap<String,String>();
    params.put("type","audio/x-wav;rate=16000");
    params.put("appkey",APPKEY);
    String timestamp = System.currentTimeMillis()/1000 +"";
    params.put("timestamp",timestamp);
    params.put("device_id", "222100f9-ac2e-4f6b-95b4-8230eee1bdd7");
    params.put("signature", SignatureUtil.getSignature(APPKEY,SECRET,timestamp));
    String result = HttpClientUtil.doPostForm(ASR_URL, params,new File(filePath));
    System.out.println(result);
  }
}
