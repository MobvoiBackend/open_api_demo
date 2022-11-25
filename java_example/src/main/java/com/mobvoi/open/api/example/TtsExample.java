//Copyright 2020 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.example;

import com.alibaba.fastjson.JSONObject;
import com.mobvoi.open.api.tool.HttpClientUtil;
import com.mobvoi.open.api.tool.SignatureUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * @author qhsong
 * @since 2020-07-22 调用出门问问开放平台tts接口demo
 */
public class TtsExample {

  private static final String TTS_URL = "https://open.mobvoi.com/api/tts/v1";
  private static final String APPKEY = "你的appkey";
  private static final String SECRET = "你的secret";

  public static void main(String[] args) {
    sample();
//    sampleWithSrt();
//    ssmlSample();
  }

  /**
   * 文字转语音
   */
  private static void sample() {
    JSONObject params = new JSONObject();
    params.put("text", "出门问问成立于2012年，是一家以语音交互和软硬结合为核心的人工智能公司，为全球40多个国家和地区的消费者、企业提供人工智能产品和服务。");
    params.put("speaker", "xiaoyi_meet");
    params.put("audio_type", "mp3");
    params.put("speed", 1.0);
    // 停顿调节需要对appkey授权后才可以使用，授权前传参无效。
    params.put("symbol_sil",
        "semi_250,exclamation_300,question_250,comma_200,stop_300,pause_150,colon_200");
    // 忽略1000字符长度限制，需要对appkey授权后才可以使用
//    params.put("ignore_limit", true);
    // 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
    params.put("gen_srt", false);
    params.put("appkey", APPKEY);
    String timestamp = System.currentTimeMillis() / 1000 + "";
    params.put("timestamp", timestamp);
    params.put("signature", SignatureUtil.getSignature(APPKEY, SECRET, timestamp));

    System.out.println(params.toJSONString());
    CloseableHttpResponse audioResponse = null;
    try {
      audioResponse = HttpClientUtil.doPostJsonStreaming(TTS_URL, params.toJSONString());
      Header firstHeader = audioResponse.getFirstHeader("Content-Type");
      if (audioResponse.getEntity().isStreaming() &&
          !firstHeader.getValue().contains("application/json")) {
        // 下载audio文件
        InputStream input = audioResponse.getEntity().getContent();
        byte[] bytes = IOUtils.toByteArray(input);
        FileUtils.writeByteArrayToFile(new File("sample.mp3"), bytes);
      } else {
        System.out.println(EntityUtils.toString(audioResponse.getEntity(), "utf-8"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (audioResponse != null) {
          audioResponse.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 文字转语音，带srt字幕文件生成
   */
  private static void sampleWithSrt() {
    JSONObject params = new JSONObject();
    params.put("text", "出门问问成立于2012年，是一家以语音交互和软硬结合为核心的人工智能公司，为全球40多个国家和地区的消费者、企业提供人工智能产品和服务。");
    params.put("speaker", "xiaoyi_meet");
    params.put("audio_type", "mp3");
    params.put("speed", 1.0);
    // 忽略1000字符长度限制，需要对appkey授权后才可以使用
//    params.put("ignore_limit", true);
    // 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
    params.put("gen_srt", true);
    params.put("appkey", APPKEY);
    String timestamp = System.currentTimeMillis() / 1000 + "";
    params.put("timestamp", timestamp);
    params.put("signature", SignatureUtil.getSignature(APPKEY, SECRET, timestamp));

    System.out.println(params.toJSONString());
    CloseableHttpResponse audioResponse = null;
    CloseableHttpResponse srtResponse = null;
    try {
      audioResponse = HttpClientUtil.doPostJsonStreaming(TTS_URL, params.toJSONString());
      Header firstHeader = audioResponse.getFirstHeader("Content-Type");
      if (audioResponse.getEntity().isStreaming() &&
          !firstHeader.getValue().contains("application/json")) {
        // 下载audio文件
        InputStream input = audioResponse.getEntity().getContent();
        byte[] bytes = IOUtils.toByteArray(input);
        FileUtils.writeByteArrayToFile(new File("sample.mp3"), bytes);
      } else {
        System.out.println(EntityUtils.toString(audioResponse.getEntity(), "utf-8"));
      }

      // 下载srt字幕文件
      Header srtHeader = audioResponse.getFirstHeader("srt_address");
      String srtAddress = srtHeader.getValue();
      System.out.println("srt_address: " + srtAddress);
      srtResponse = HttpClientUtil.getResult(srtAddress, null);
      InputStream input = srtResponse.getEntity().getContent();
      byte[] bytes = IOUtils.toByteArray(input);
      FileUtils.writeByteArrayToFile(new File("sample.srt"), bytes);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (audioResponse != null) {
          audioResponse.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if (srtResponse != null) {
          srtResponse.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 使用ssml格式文字转语音
   */
  private static void ssmlSample() {
    JSONObject params = new JSONObject();
    params.put("text",
        "<speak version=\"1.0\" xml:lang=\"zh-CN\" xmlns=\"http://www.w3.org/2001/10/synthesis\">9月10日，庆祝2019年<w phoneme=\"jiao4 shi1 jie2\">教师节</w>暨全国教育系统先进集体和先进个人表彰大会在京举行。<break time=\"500ms\" />习近平总书记在人民大会堂亲切会见受表彰代表，<break time=\"500ms\" />向受到表彰的先进集体和先进个人表示热烈祝贺，<break time=\"500ms\" />向全国广大<p phoneme=\"jiao4\">教</p>师和教育工作者致以节日的问候。</speak>");
    params.put("speaker", "xiaoyi_meet");
    params.put("audio_type", "mp3");
    params.put("speed", 1.0);
    // 忽略1000字符长度限制，需要对appkey授权后才可以使用
//    params.put("ignore_limit", true);
    // 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
    params.put("gen_srt", false);
    params.put("appkey", APPKEY);
    String timestamp = System.currentTimeMillis() / 1000 + "";
    params.put("timestamp", timestamp);
    params.put("signature", SignatureUtil.getSignature(APPKEY, SECRET, timestamp));

    System.out.println(params.toJSONString());
    CloseableHttpResponse audioResponse = null;
    try {
      audioResponse = HttpClientUtil.doPostJsonStreaming(TTS_URL, params.toJSONString());
      Header firstHeader = audioResponse.getFirstHeader("Content-Type");
      if (audioResponse.getEntity().isStreaming() &&
          !firstHeader.getValue().contains("application/json")) {
        // 下载audio文件
        InputStream input = audioResponse.getEntity().getContent();
        byte[] bytes = IOUtils.toByteArray(input);
        FileUtils.writeByteArrayToFile(new File("ssmlSample.mp3"), bytes);
      } else {
        System.out.println(EntityUtils.toString(audioResponse.getEntity(), "utf-8"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (audioResponse != null) {
          audioResponse.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
