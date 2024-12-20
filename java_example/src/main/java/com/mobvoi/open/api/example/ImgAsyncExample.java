package com.mobvoi.open.api.example;


import com.alibaba.fastjson.JSONObject;
import com.mobvoi.open.api.tool.SignatureUtil;
import io.joshworks.restclient.http.HttpResponse;
import io.joshworks.restclient.http.Unirest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：mobvoi
 * Date: 2024/12/19
 * Time: 17:50
 * Description:图片生成demo
 */

public class ImgAsyncExample {

    //提交任务接口
    private static final String IMGASYNC_URL = "https://open.mobvoi.com/api/imgAsync";

    //查询任务接口
    private static final String GETRESULT_URL = "https://open.mobvoi.com/api/asyncGenImg/getResult";

    //图片转文本接口
    private static final String IMGTOPROMPT_URL = "https://open.mobvoi.com/api/imgToPrompt";


    private static final String APPKEY = "26745D67ED5430EC07D19068D33CCF29";

    private static final String SECRET = "EF3625F3A6DAD8C734CFBDCB55414ABC";
    private static final String prompt = "落霞与孤鹜齐飞";

    //图片转文本接口,原始图片URl
    private static final String original_image = "https://mobvoi-digitalhuman-public.cn-bj.ufileos.com/sdxl/img_tmp/0f0ea12308_79fae1ac-bdf3-11ef-b149-5254002b285b_wm_1.png";

    //任务id
    private static final String taskId = "9019";

    public static void main(String[] args) {
        //提交任务
        imgAsyncSample();

        //查询任务
        getResult(taskId);

        //图片转文本
        // imgToPrompt();

    }


    /**
     * 提交任务接口
     */
    private static void imgAsyncSample() {
        Map<String, Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appkey", APPKEY);
        params.put("timestamp", timestamp);
        params.put("signature", signature);
        params.put("prompt", prompt);
        HttpResponse<String> response = Unirest.post(IMGASYNC_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();

        System.out.println(response.body());
    }


    /**
     * 查询任务接口
     *
     * @param taskId
     */
    private static void getResult(String taskId) {
        //设置https协议版本,如出现SSLHandshakeException时可开启此选项
        //  System.setProperty("https.protocols", "TLSv1.2");
        HttpResponse<String> response = Unirest.get(GETRESULT_URL)
                .queryString("taskId", taskId)
                .header("Accept", "application/json")
                .asString();
        System.out.println(response.body());
    }

    /**
     * 图片转文本接口
     */
    private static void imgToPrompt() {
        //设置https协议版本,如出现SSLHandshakeException时可开启此选项
        //  System.setProperty("https.protocols", "TLSv1.2");
        Map<String, Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appkey", APPKEY);
        params.put("original_image", original_image);
        HttpResponse<String> response = Unirest.post(IMGTOPROMPT_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();

        System.out.println(response.body());
    }


}
