package com.mobvoi.open.api.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobvoi.open.api.tool.SignatureUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * @Description: 视频合成demo
 * @CreateDate: 2023/6/14 10:20
 **/
public class VideoExample {

    private final static String video_url = "https://open.mobvoi.com/cmp/video";

    private final static String get_video_url = "https://open.mobvoi.com/cmp/result";

    private final static String audio_url = "你的音频文件公网url地址";

    private final static String appKey = "你的appKey";

    private final static String secret = "你的app密钥";

    private final static String wetaSpeakerId = "模特id";

    public static void main(String[] args) {
        // 通过音频驱动生成视频
        String videoId = genVideoWithUrl();

        // 通过文本驱动生成视频
//        String videoId = genVideoWithText();

        //获取结果
        getVideoResult(videoId);
    }

    private static String genVideoWithUrl() {
        JSONObject param = new JSONObject();
        param.put("appKey", appKey);
        String timestamp = System.currentTimeMillis()/1000 + "";
        param.put("timestamp", timestamp);
        param.put("signature", SignatureUtil.getSignature(appKey, secret, timestamp));
        param.put("audioUrl", audio_url);
        param.put("wetaSpeakerId", wetaSpeakerId);
        JSONObject style = new JSONObject();
        style.put("width", "1000");
        style.put("height", "1900");
        param.put("style", style);

        HttpPost httpPost = new HttpPost(video_url);
        CloseableHttpClient client = HttpClients.createDefault();

        //请求参数转JOSN字符串
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        String videoId = "";
        try {
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject result = JSON.parseObject(EntityUtils.toString(
                        response.getEntity(), "UTF-8"));
                System.out.println(result.toJSONString());
                if (result.getInteger("code") == 200) {
                    videoId = result.getJSONObject("data").getString("videoId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoId;
    }

    private static String genVideoWithText() {
        JSONObject param = new JSONObject();
        param.put("appKey", appKey);
        String timestamp = System.currentTimeMillis()/1000 + "";
        param.put("timestamp", timestamp);
        param.put("signature", SignatureUtil.getSignature(appKey, secret, timestamp));
        param.put("wetaSpeakerId", wetaSpeakerId);

        JSONObject ttsParam = new JSONObject();
        ttsParam.put("text", "你好，我是出门问问的数字合成人，很高兴见到你");
//        ttsParam.put("speaker", "caicai_meet_48k");
        ttsParam.put("audio_type", "wav");
        ttsParam.put("gen_srt", true);
        ttsParam.put("ignore_limit", true);
        param.put("ttsParam", ttsParam);

        JSONObject style = new JSONObject();
        style.put("width", "1000");
        style.put("height", "1900");
        param.put("style", style);

        JSONArray materials = new JSONArray();

        JSONObject material = new JSONObject();
        material.put("type", "subtitles");
        material.put("fontsize", "15");
        materials.add(material);
        param.put("materials", materials);


        HttpPost httpPost = new HttpPost(video_url);
        CloseableHttpClient client = HttpClients.createDefault();

        //请求参数转JOSN字符串
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        String videoId = "";
        try {
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject result = JSON.parseObject(EntityUtils.toString(
                        response.getEntity(), "UTF-8"));
                System.out.println(result.toJSONString());
                if (result.getInteger("code") == 200) {
                    videoId = result.getJSONObject("data").getString("videoId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videoId;
    }

    private static void getVideoResult(String videoId) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(get_video_url);
            builder.addParameter("videoId", videoId);
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject result = JSON.parseObject(EntityUtils.toString(
                        response.getEntity(), "UTF-8"));
                System.out.println(result.toJSONString());
            }
        }
        catch (Exception e) {
            System.out.println("catch error...");
        }
    }

}
