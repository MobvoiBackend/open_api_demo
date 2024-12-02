#!/usr/bin/env python3

"""Tts demo which receive pcm data in streaming."""

import sys
import time
import hashlib
import os
import json
import requests
from loguru import logger

APP_KEY = ""
APP_SECRET = ""
TTS_HTTP_URL = "https://open.mobvoi.com/api/tts/v1"

tts_post_data ={
    'text': '',
    'speaker': 'xiaoyi_meet',
    'audio_type': 'pcm',
    'rate': 24000,
    'speed': 1.0,
    'ignore_limit': True,
    'appkey': APP_KEY,
    'timestamp': "",
    'signature': "",
    'streaming': True
}

def gen_long_text() -> str:
    """Generate long text."""
    text = """海南长臂猿的叫声，高亢洪亮，响彻山谷。海南热带雨林国家公园是这种濒危灵长类动物的全球唯一栖息地。经过近年来的科学保护和生态恢复，海南长臂猿已由最少时的寥寥几只，恢复到5群3\
5只，创造了世界珍稀动物保护的奇迹。国家公>园堪称最美国土，具有典型独特的自然生态系统、世界瞩目的野生动植物种。在海南热带雨林国家公园，这里生长着846种特有植物、145种国家重点保护野生动物，生物\
多样性指数与巴西亚马孙雨林相当。2018年4月，习近平总书记在庆祝海>南建省办经济特区30周年大会上强调，要积极开展国家公园体制试点，建设热带雨林等国家公园。2019年1月，总书记又主持召开中央全面\
深化改革委员会第六次会议，审议通过《海南热带雨林国家公园体制试点方案》。被称为海南“生态绿心”的这片最美国土迈出保护和建设的历史性一步。我国的国家公园在自然保护地体系中保护等级最高、生态价值最大、管\
控措施最严。"""
    return text

def gen_md5_signature(message: str) -> str:
    """Generate md5sum."""
    logger.debug(":" + message)
    md5_hasher = hashlib.md5()
    md5_hasher.update(message.encode("utf-8"))
    signature = md5_hasher.hexdigest()
    logger.debug(message + " md5 value: " + signature)
    return signature

def synthesis():
    """Synthesis online tts in streaming."""
    timestamp = str(int(time.time()))
    tts_post_data['timestamp'] = timestamp
    message = '+'.join([APP_KEY, APP_SECRET, timestamp])
    tts_post_data['signature'] = gen_md5_signature(message)
    tts_post_data['text'] += gen_long_text()
    try:
        headers = {'Content-Type': 'application/json'}
        logger.info(f"Post data: {json.dumps(tts_post_data, ensure_ascii=False)}")
        ret = requests.post(url=TTS_HTTP_URL, headers=headers, data=json.dumps(tts_post_data),
                            stream=True, timeout=None)
        if ret.status_code != 200:
            logger.error(f"response status code : {ret.status_code}")
            return
        if ret.headers.get('Content-Type') != "audio/pcm":
            logger.error(f"response invalid Content-Type! {ret.headers}")
            logger.info(f"response body {ret.content.decode()}")
            return
        content =b""
        begin = time.time()
        for chunk in ret.iter_content(chunk_size=None):
            logger.trace("Receive chunk size: " + str(len(chunk)))
            content += chunk
        end = time.time()
        logger.info(f"Receive pcm data costs: {end - begin}s")
        with open(os.path.join(os.path.dirname(os.path.abspath("__file__")),
                                "sample.pcm"), "wb") as f_handler:
            f_handler.write(content)

    except Exception as error:
        logger.error(f"Exception {error}")

def main():
    """main function"""
    synthesis()

if __name__ == '__main__':
    logger.remove()
    logger.add(sys.stdout, level="TRACE")
    main()
