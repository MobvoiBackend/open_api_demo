#!/usr/bin/env python3
# coding=utf-8
import time
import hashlib
import os
import json
import requests
from loguru import logger

APP_KEY = "1335460331E5C6D0CA4370434ABF5B5A"
APP_SECRET = "1FB9EB79EADB3FA0A9E0DA8BBA929543"
TTS_HTTP_URL = "https://open.mobvoi.com/api/tts/v1"

tts_post_data ={
    'text': '出门问问成立于2012年，是一家以语音交互和软硬结合为核心的人工智能公司，为全球40多个国家和地区的消费者、企业提供人工智能产品和服务。',
    'speaker': 'xiaoyi_meet',
    'audio_type': 'mp3',
    'speed': 1.0,
    #'symbol_sil': 'semi_250,exclamation_300,question_250,comma_200,stop_300,pause_150,colon_200', # 停顿调节需要对appkey授权后才可以使用，授权前传参无效。
    #'ignore_limit': True, # 忽略1000字符长度限制，需要对appkey授权后才可以使用
    'gen_srt': False, # 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
    'appkey': APP_KEY,
    'timestamp': "",
    'signature': ""
}

def gen_md5_signature(message: str) -> str:
    logger.debug(":" + message)
    md5_hasher = hashlib.md5()
    md5_hasher.update(message.encode("utf-8"))
    signature = md5_hasher.hexdigest()
    logger.debug(message + " md5 value: " + signature)
    return signature

def sample(enable_ssml: bool=False, enable_srt: bool=False) -> None:
    timestamp = str(int(time.time()))
    tts_post_data['timestamp'] = timestamp
    message = '+'.join([APP_KEY, APP_SECRET, timestamp])
    tts_post_data['signature'] = gen_md5_signature(message)
    if enable_ssml:
        logger.info("enable_ssml: " + str(enable_ssml))
        ssml_text = '<speak version="1.0" xml:lang="zh-CN" xmlns="http://www.w3.org/2001/10/synthesis">9月10日，庆祝2019年<w phoneme="jiao4 shi1 jie2">教师节</w>暨全国教育系统先进集体和先进个人表彰大会在京举行。<break time="500ms" />习近平总书记在人民大会堂亲切会见受表彰代表，<break time="500ms" />向受到表彰的先进集体和先进个人表示热烈祝贺，<break time="500ms" />向全国广大<p phoneme="jiao4">教</p>师和教育工作者致以节日的问候。</speak>'
        tts_post_data["text"] = ssml_text
        tts_post_data['audio_type'] = "wav"
        tts_post_data['gen_srt'] = True
    if enable_srt:
        logger.info("enable_srt: " + str(enable_srt))
        tts_post_data['audio_type'] = "wav"
        tts_post_data['gen_srt'] = True
    try:
        headers = {'Content-Type': 'application/json'}
        logger.info(f"Post data: {json.dumps(tts_post_data)}")
        ret = requests.post(url=TTS_HTTP_URL, headers=headers, data=json.dumps(tts_post_data))
        content = ret.content
        if ret.status_code != 200:
            logger.error(f"response status code : {ret.status_code}")
            return
        if ret.headers.get('Content-Type') != "audio/mpeg":
            logger.error(f"response invalid Content-Type! {ret.headers}")
            logger.info(f"response body {ret.content.decode()}")
            return
        with open(os.path.join(os.path.dirname(os.path.abspath("__file__")), "sample.mp3"), "wb") as f:
            f.write(content)
        if enable_srt:
            srtUrl = ret.headers.get('srt_address', None)
            if srtUrl is None:
                logger.warning('not found srt url from response header')
                return
            logger.info('srt url:', srtUrl)
            content = requests.get(url=srtUrl).content
            with open(os.path.join(os.path.dirname(os.path.abspath("__file__")), "sample.srt"), "wb") as f:
                f.write(content)

    except Exception as e:
        logger.error(f"Exception {e}")

def main():
    sample(enable_srt=True)

if __name__ == '__main__':
    main()
