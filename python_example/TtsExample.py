# coding=utf-8
import time
import hashlib
import os
import json
import requests

timestamp = str(int(time.time()))
appkey = '你的appkey'
secret = '你的secret'

message = '+'.join([appkey, secret, timestamp])

m = hashlib.md5()
m.update(message)
signature = m.hexdigest()

http_url = 'https://open.mobvoi.com/api/tts/v1'

def sample():
    data = {
        'text': '出门问问成立于2012年，是一家以语音交互和软硬结合为核心的人工智能公司，为全球40多个国家和地区的消费者、企业提供人工智能产品和服务。',
        'speaker': 'xiaoyi_meet',
        'audio_type': 'mp3',
        'speed': 1.0,
        #'symbol_sil': 'semi_250,exclamation_300,question_250,comma_200,stop_300,pause_150,colon_200', # 停顿调节需要对appkey授权后才可以使用，授权前传参无效。
        #'ignore_limit': True, # 忽略1000字符长度限制，需要对appkey授权后才可以使用
        'gen_srt': False, # 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
        'appkey': appkey,
        'timestamp': timestamp,
        'signature': signature
    }
    try:
        headers = {'Content-Type': 'application/json'}
        response = requests.post(url=http_url, headers=headers, data=json.dumps(data))
        content = response.content

        with open(os.path.join(os.path.dirname(os.path.abspath("__file__")), "sample.mp3"), "wb") as f:
            f.write(content)
    except Exception, e:
        print e

def sampleWithSrt():
    data = {
        'text': '出门问问成立于2012年，是一家以语音交互和软硬结合为核心的人工智能公司，为全球40多个国家和地区的消费者、企业提供人工智能产品和服务。',
        'speaker': 'xiaoyi_meet',
        'audio_type': 'wav',
        'speed': 1.0,
        #'symbol_sil': 'semi_250,exclamation_300,question_250,comma_200,stop_300,pause_150,colon_200', # 停顿调节需要对appkey授权后才可以使用，授权前传参无效。
        #'ignore_limit': True, # 忽略1000字符长度限制，需要对appkey授权后才可以使用
        'gen_srt': True, # 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
        'appkey': appkey,
        'timestamp': timestamp,
        'signature': signature
    }
    try:
        headers = {'Content-Type': 'application/json'}
        print json.dumps(data)
        response = requests.post(url=http_url, headers=headers, data=json.dumps(data))
        content = response.content

        with open(os.path.join(os.path.dirname(os.path.abspath("__file__")), "sample.mp3"), "wb") as f:
            f.write(content)

        srtUrl = response.headers.get('srt_address', None)
        if srtUrl is not None:
            print srtUrl
            content = requests.get(url=srtUrl).content
            with open(os.path.join(os.path.dirname(os.path.abspath("__file__")), "sample.srt"), "wb") as f:
                f.write(content)

    except Exception, e:
        print e

def sampleWithSsml():
    data = {
        'text': '<speak version="1.0" xml:lang="zh-CN" xmlns="http://www.w3.org/2001/10/synthesis">9月10日，庆祝2019年<w phoneme="jiao4 shi1 jie2">教师节</w>暨全国教育系统先进集体和先进个人表彰大会在京举行。<break time="500ms" />习近平总书记在人民大会堂亲切会见受表彰代表，<break time="500ms" />向受到表彰的先进集体和先进个人表示热烈祝贺，<break time="500ms" />向全国广大<p phoneme="jiao4">教</p>师和教育工作者致以节日的问候。</speak>',
        'speaker': 'xiaoyi_meet',
        'audio_type': 'wav',
        'speed': 1.0,
        #'symbol_sil': 'semi_250,exclamation_300,question_250,comma_200,stop_300,pause_150,colon_200', # 停顿调节需要对appkey授权后才可以使用，授权前传参无效。
        #'ignore_limit': True, # 忽略1000字符长度限制，需要对appkey授权后才可以使用
        'gen_srt': True, # 是否生成srt字幕文件，默认不开启。如果开启生成字幕，需要额外计费。生成好的srt文件地址将通过response header中的srt_address字段返回。
        'appkey': appkey,
        'timestamp': timestamp,
        'signature': signature
    }
    try:
        headers = {'Content-Type': 'application/json'}
        print json.dumps(data)
        response = requests.post(url=http_url, headers=headers, data=json.dumps(data))
        content = response.content

        with open(os.path.join(os.path.dirname(os.path.abspath("__file__")), "ssmlSample.mp3"), "wb") as f:
            f.write(content)

    except Exception, e:
        print e

def main():
    sample()
    # sampleWithSrt()
    # sampleWithSsml()

if __name__ == '__main__':
    main()
