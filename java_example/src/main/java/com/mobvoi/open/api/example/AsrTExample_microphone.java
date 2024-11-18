package com.mobvoi.open.api.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobvoi.open.api.tool.SignatureUtil;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class AsrTExample_microphone {
  private static final String ASR_FOR_SENTENCE = "wss://open-ka.mobvoi.com/ws/asr";

  private static final String APPKEY = "";

  private static final String SECRET = "";

  private static void callAsrForSentence() {
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
      URI uri = new URI(ASR_FOR_SENTENCE);
      JSONObject jsonObject = new JSONObject();

      WebSocketClient webSocketClient = new WebSocketClient(uri) {
        @Override
        public void onMessage(String s) {
          System.out.println("rev message:" + s + "  rev time is " + simpleDateFormat.format(new Date()));
          JSONObject json = JSON.parseObject(s);
          String type = json.getString("type");
          if (type.equals("server_ready")) {
            jsonObject.put("flag", "y");
          }
          if(type.equals("silence")){
            JSONObject end = new JSONObject();
            end.put("signal", "end");
            send(end.toJSONString());
          }
        }
        @Override
        public void onOpen(ServerHandshake serverHandshake) {
        }
        @Override
        public void onClose(int i, String s, boolean b) {
          Date date = new Date();
          System.out.println("channel close, end time is " + date);
        }
        @Override
        public void onError(Exception e) {
        }
      };
      System.out.println(new Date());
      System.out.println("start time is " + simpleDateFormat.format(new Date()));
      webSocketClient.connect();
      while (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
        Thread.sleep(1 * 10);
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

      while (true) {
        if (jsonObject.containsKey("flag")) {
          break;
        }
        Thread.sleep(100);
      }

      new Thread(()->{
        // 配置音频格式
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // 打开麦克风并开始捕获音频数据
        try (TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info)) {
          microphone.open(format);
          microphone.start();

          ByteArrayOutputStream out = new ByteArrayOutputStream();
          byte[] buffer = new byte[16000];
          int bytesRead;

          while (true) {
            bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
              out.write(buffer, 0, bytesRead);
              byte[] frameData = new byte[bytesRead];
              System.arraycopy(buffer, 0, frameData, 0, bytesRead);
              webSocketClient.send(frameData);
            }
          }
        } catch (LineUnavailableException e) {
          e.printStackTrace();
        }
      }).start();

    }
    catch (Exception e) {
      System.out.println("catch error");
      System.out.println(e);
    }
  }

  public static void main(String[] args) {
    // 一句话识别
    callAsrForSentence();
  }
}
