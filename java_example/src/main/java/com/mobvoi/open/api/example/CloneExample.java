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


    private static final String APPKEY = "26745D67ED5430EC07D19068D33CCF29";

    private static final String SECRET = "EF3625F3A6DAD8C734CFBDCB55414ABC";

    //可以公网访问的音频文件地址,时长要求在3秒～150秒之间
    private static final String WAVURI = "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/57d3ea8923e8c110963573bda9d4ec6e.wav";

    //音频文件，时长要求在3秒～150秒之间
    private static final String file = "";

    //上传音频文件的文本，含有标点的准确文本（与音频文案相匹配）有助于提升模型效果，默认为空
    private static final String text = "";


    //音频URL，目前只支持wav格式
    private static final String sentenceText = "俱往矣，数风流人物，还看今朝。";






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
        Map<String,Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appKey",APPKEY);
        params.put("timestamp",timestamp);
        params.put("signature",signature);
        String[] fileUrls = {
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/57d3ea8923e8c110963573bda9d4ec6e.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/d21935fa4dcbd5d999cb880b9b478d88.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/2e49e38d184c919fbea29a7ba5e18b5f.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/ba5a144de3a9c9e65880779d4ed86f89.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/61c897fa25a419b6673187ab105b1b82.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/da4414df56187584a78f0a7e01047de8.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/f2d1559315961180af3d22878d91dfce.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/109d21ca869f07be7a876c308f995679.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/d379d4abd074cf513266fc2999c1ec81.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/ebf8c87cf5eab499b62cfa1b1f7f0239.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/1866a9cd6505224f953c63e1f24bc93b.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/6175437e25f354b28ac6fdbd99937ad6.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/8056e2af8bad0da6944bb7d85c02854b.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/4af6b2d74d2dea090257804d7784a0b0.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/c514006ff6a4f1456d321189041a15f2.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/554efffb8bf1ed8cb37bb630bf5f0c01.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/10b2b8ceb1d1fcd84d5223ddf68ca442.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/9d3c519d1b77f78595d1c40fc280b85f.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/ef2560fecada25e6acc861a596621193.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/wav/be4d733f22e2764edee9f9f7b67f1a7a.wav",
                "https://tc-nj-backend-pub-cdn.mobvoi.com/subtitles/txt/c3d4f5250f8f7adb492a4ed0cbec3042.txt"
        };

        params.put("fileUrls",fileUrls);
        params.put("gender","M");
        params.put("language","Chinese");
        params.put("speakerName","testSpeaker");
        System.out.println("params: " + params);


        HttpResponse<String> response = Unirest.post(CLONE_TRAIN_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();

        System.out.println(response.body());



    }


    /**
     * 声音克隆 查询任务状态接口
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
        Map<String,Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appKey",APPKEY);
        params.put("timestamp",timestamp);
        params.put("signature",signature);
        //音频文本
        params.put("sentenceText",sentenceText);
        //音频URL，目前只支持wav格式
        params.put("audioFileUrl",WAVURI);
        params.put("language","Chinese");



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
        Map<String,Object> params = new HashMap<>();
        String timestamp = System.currentTimeMillis() / 1000 + "";
        String signature = SignatureUtil.getSignature(APPKEY, SECRET, timestamp);
        params.put("appKey",APPKEY);
        params.put("timestamp",timestamp);
        params.put("signature",signature);

        params.put("isDenoise",true);

        params.put("isRemoveReverb",true);
        params.put("audioFileUrl",WAVURI);


        HttpResponse<String> response = Unirest.post(AUDIOHANDLE_URL)
                .header("content-type", "application/json")
                .body(JSONObject.toJSONString(params))
                .asString();

        System.out.println(response.body());

    }

}
