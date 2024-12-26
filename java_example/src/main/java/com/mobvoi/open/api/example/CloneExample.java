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
 * Time: 14:32
 * Description:调用mobvoi开放平台声音克隆接口demo
 */

public class CloneExample {

    //3s克隆 提交任务
    private static final String CLONE3S_URL = "https://open.mobvoi.com/clone";

    //声音克隆 1. 提交训练任务接口
    private static final String CLONE_TRAIN_URL = "https://open.mobvoi.com/clone/train";

    //声音克隆 2. 查询任务状态接口
    private static final String CLONE_TRAINSTATUS_URL = "https://open.mobvoi.com/clone/trainStatus";


    //声音克隆 3. 音频检测接口
    private static final String AUDIODETECT_URL = "https://open.mobvoi.com/clone/audioDetect";


    //声音克隆 4. 音频处理接口
    private static final String AUDIOHANDLE_URL = "https://open.mobvoi.com/clone/audioHandle";


    private static final String APPKEY = "你的appkey";

    private static final String SECRET = "你的secret";

    //可以公网访问的音频文件地址。3s克隆、 音频检测接口、 音频处理接口 都需要用到
    private static final String WAVURI = "音频文件地址";


    //音频检测接口，音频文本
    private static final String sentenceText = "音频检测接口，音频文本";

    //声音克隆 提交训练任务接口中的音频和文本文件url
    private static final String[] fileUrls = {"第一条音频的url", "第二条音频的url", "...", "第N条音频的url", "文本文件url"};


    public static void main(String[] args) {

        //3s克隆 提交任务
        clone3s();

        //声音克隆 提交训练任务接口
        // train();

        //声音克隆 查询任务状态接口
        // trainStatus();

        //声音克隆 音频检测接口
        //audioDetect();


        //声音克隆 音频处理接口
        // audioHandle();

    }


    /**
     * 3s克隆 提交任务
     */
    private static void clone3s() {
        //设置https协议版本,如出现SSLHandshakeException时可开启此选项
        //  System.setProperty("https.protocols", "TLSv1.2");
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        System.out.println("appKey: " + APPKEY);
        System.out.println("timestamp: " + timestamp);
        System.out.println("signature: " + signature);
        System.out.println("wavUri: " + WAVURI);
        HttpResponse<String> response = Unirest.post(CLONE3S_URL)
                .field("appKey", APPKEY)
                .field("timestamp", timestamp)
                .field("signature", signature)
                .field("wavUri", WAVURI)
                .asString();
        System.out.println(response.body());

    }


    /**
     * 声音克隆 提交训练任务接口
     */
    private static void train() {
        //设置https协议版本,如出现SSLHandshakeException时可开启此选项
        //  System.setProperty("https.protocols", "TLSv1.2");
        Map<String, Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appKey", APPKEY);
        params.put("timestamp", timestamp);
        params.put("signature", signature);
        params.put("fileUrls", fileUrls);
        params.put("gender", "M");
        params.put("language", "Chinese");
        params.put("speakerName", "testSpeaker");
        System.out.println("params: " + params);
        HttpResponse<String> response = Unirest.post(CLONE_TRAIN_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();
        System.out.println(response.body());
    }


    /**
     * 声音克隆 查询任务状态接口
     *
     * @param taskId
     */
    private static void trainStatus(String taskId) {
        //设置https协议版本,如出现SSLHandshakeException时可开启此选项
        //  System.setProperty("https.protocols", "TLSv1.2");
        HttpResponse<String> response = Unirest.get(CLONE_TRAINSTATUS_URL)
                .queryString("taskId", taskId)
                .header("Accept", "application/json")
                .asString();
        System.out.println(response.body());
    }

    /**
     * 声音克隆 音频检测接口
     */
    private static void audioDetect() {
        Map<String, Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appKey", APPKEY);
        params.put("timestamp", timestamp);
        params.put("signature", signature);
        //音频文本
        params.put("sentenceText", sentenceText);
        //音频URL，目前只支持wav格式
        params.put("audioFileUrl", WAVURI);
        params.put("language", "Chinese");
        HttpResponse<String> response = Unirest.post(AUDIODETECT_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();
        System.out.println(response.body());
    }


    /**
     * 声音克隆 音频处理接口
     */
    private static void audioHandle() {
        Map<String, Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appKey", APPKEY);
        params.put("timestamp", timestamp);
        params.put("signature", signature);
        params.put("isDenoise", true);
        params.put("isRemoveReverb", true);
        params.put("audioFileUrl", WAVURI);
        HttpResponse<String> response = Unirest.post(AUDIOHANDLE_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();
        System.out.println(response.body());
    }

}
