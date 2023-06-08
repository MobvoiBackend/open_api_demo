//Copyright 2020 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.example;

import com.alibaba.fastjson.JSON;
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
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * @author qhsong
 * @since 2020-07-21
 * 调用mobvoi开放平台asr识别接口demo
 */
public class AsrExample {

  private static final String ASR_FOR_SENTENCE = "wss://open.mobvoi.com/ws/asr";

  private static final String ASR_FOR_FILE_CREATE = "https://open.mobvoi.com/api/asr/create";

  private static final String ASR_FOR_FILE_GET = "https://open.mobvoi.com/api/asr/get";

  private static final String audioFilePath = "你的本地文件地址";

  private static final String audioUrl = "你的音频文件url地址需可以公网访问";

  private static final String APPKEY = "你的appKey";

  private static final String SECRET = "你的appSc";

  private static void callAsrForSentence() {
    try {
      URI uri = new URI(ASR_FOR_SENTENCE);
      WebSocketClient webSocketClient = new WebSocketClient(uri) {
        @Override
        public void onMessage(String s) {
          System.out.println("rev message:" + s);
        }
        @Override
        public void onOpen(ServerHandshake serverHandshake) {
        }
        @Override
        public void onClose(int i, String s, boolean b) {
        }
        @Override
        public void onError(Exception e) {
        }
      };

      webSocketClient.connect();

      while (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
        System.out.println("连接中...");
        Thread.sleep(5 * 100);
      }

      JSONObject start = new JSONObject();
      start.put("appKey", APPKEY);
      String timestamp = System.currentTimeMillis()/1000 +"";
      start.put("timestamp", timestamp);
      start.put("signature", SignatureUtil.getSignature(APPKEY,SECRET,timestamp));
      start.put("signal", "start");
      start.put("contentType", "audio/x-wav;codec=pcm;bit=16;rate=16000");
      start.put("partial_result", "enable");
      start.put("silence_detection", "enable");

      webSocketClient.send(start.toJSONString());

      Path file = Paths.get(audioFilePath);
      try (FileChannel fileChannel = FileChannel.open(file)) {
        ByteBuffer buffer = ByteBuffer.allocate(16000);
        int bytesRead;
        while ((bytesRead = fileChannel.read(buffer)) != -1) {
          buffer.flip();
          byte[] frameData = new byte[bytesRead];
          buffer.get(frameData);
          webSocketClient.send(frameData);
          buffer.clear();
        }
      }
    }
    catch (Exception e) {
      System.out.println("catch error");
      System.out.println(e);
    }
  }


  private static void callAsrForFile() {
    Long asrTask = createAsrTask();
    try {
      Thread.sleep(10 * 1000);
    }
    catch (Exception e) {
      System.out.println("catch error...");
    }

    getAsrTask(asrTask);
  }

  private static Long createAsrTask() {
    JSONObject param = new JSONObject();
    param.put("appKey", APPKEY);
    String timestamp = System.currentTimeMillis()/1000 + "";
    param.put("timestamp", timestamp);
    param.put("signature", SignatureUtil.getSignature(APPKEY,SECRET,timestamp));
    param.put("audio_url", audioUrl);
    param.put("language", "zh_cn");
    param.put("enable_punctuation", true);


    HttpPost httpPost = new HttpPost(ASR_FOR_FILE_CREATE);
    CloseableHttpClient client = HttpClients.createDefault();

    //请求参数转JOSN字符串
    StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
    entity.setContentEncoding("UTF-8");
    entity.setContentType("application/json");
    httpPost.setEntity(entity);

    JSONObject result = new JSONObject();
    try {
      HttpResponse response = client.execute(httpPost);
      if (response.getStatusLine().getStatusCode() == 200) {
        result = JSON.parseObject(EntityUtils.toString(
                response.getEntity(), "UTF-8"));
        System.out.println(result.toJSONString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result.getJSONObject("data").getLong("task_id");
  }

  private static void getAsrTask(Long taskId) {
    try {
      CloseableHttpClient client = HttpClients.createDefault();
      URIBuilder builder = new URIBuilder(ASR_FOR_FILE_GET);
      builder.addParameter("taskId", taskId.toString());
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

  public static void main(String[] args) {
    // 一句话识别
    callAsrForSentence();

    // 录音文件识别
//    callAsrForFile();

  }
}
