//Copyright 2020 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.example;

import com.mobvoi.open.api.tool.HttpClientUtil;
import com.mobvoi.open.api.tool.SignatureUtil;
import java.util.HashMap;

/**
 * @author qhsong
 * @since 2020-07-22
 */
public class OneboxExample {

  private static final String Onebox_URL = "https://open.mobvoi.com/api/search/v1";

  private static final String APPKEY = "你的appkey";

  private static final String SECRET = "你的secret";

  public static void main(String[] args) {
    HashMap params = new HashMap<String,String>();
    params.put("query","一千乘以一千等于多少");
    params.put("address","中国,上海市,上海市,杨浦区,武东路,,31.308912471391192,121.49982640557423");
    params.put("output","lite");
    params.put("version","43000");
    params.put("user_id","222100f9-ac2e-4f6b-95b4-8230eee1bdd7");
    params.put("appkey",APPKEY);
    String timestamp = System.currentTimeMillis()/1000 +"";
    params.put("timestamp",timestamp);
    params.put("signature", SignatureUtil.getSignature(APPKEY,SECRET,timestamp));
    String result = HttpClientUtil.doGet(Onebox_URL, params);
    System.out.println(result);

  }
}
