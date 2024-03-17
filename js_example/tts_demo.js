#!/usr/bin/env node

const md5 = require("js-md5");
const axios = require("axios");
const fs = require("fs");

const TTS_URL = "https://open.mobvoi.com/api/tts/v1";
const APP_KEY = "";
const APP_SECRET = "";

function getTimestampInSeconds() {
  return Math.floor(Date.now() / 1000);
}

function getMd5Sum(message) {
  console.log("calcute md5sum: " + message);
  var hash = md5.create();
  hash.update(message);
  return hash.hex();
}

let post_data = {
  text: "我是一段测试文本，要生成wav文件",
  speaker: "cissy_meet",
  audio_type: "mp3",
  ignore_limit: true,
  gen_srt: true,
  speed: 1.0,
  appkey: APP_KEY,
  timestamp: "",
  signature: "APPKEY+APPSECRET+TIMESTAMP的md5值",
};

post_data["timestamp"] = getTimestampInSeconds();
const message = APP_KEY + "+" + APP_SECRET + "+" + post_data["timestamp"];
post_data["signature"] = getMd5Sum(message);
headers = { "Content-Type": "application/json" };

axios
  .post(TTS_URL, post_data, { headers, responseType: "arraybuffer" })
  .then((res) => {
    if (res.status != 200) {
      console.log("Response code: ", res.status, res.statusText);
      return;
    }
    console.log(res.headers);
    console.log(typeof res.data);

    fs.writeFile("./tts.mp3", res.data, (e) => {
      if (e) {
        return console.log(e);
      }
      console.log("Save mp3 to tts.mp3");
    });
  })
  .catch((err) => {
    console.log("AXIOS ERROR: ", err);
  });
