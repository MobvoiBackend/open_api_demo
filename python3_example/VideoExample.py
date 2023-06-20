import hashlib
import json
import time
import requests

appkey = "你的appKey"
secret = "你的app的密钥"

gen_video_url = "https://open.mobvoi.com/cmp/video"
get_video_url = "https://open.mobvoi.com/cmp/result"
audio_url = "你的音频文件url"
wetaSpeakerId = "模特id"


def gen_video_with_url():
    headers = {'content-type': 'application/json'}
    timestamp = str(int(time.time()))
    message = '+'.join([appkey, secret, timestamp])
    m = hashlib.md5()
    m.update(message.encode('utf-8'))
    signature = m.hexdigest()
    req = {
        "appKey": appkey,
        "timestamp": timestamp,
        "signature": signature,
        "audio_url": audio_url,
        "wetaSpeakerId": wetaSpeakerId
    }

    resp = requests.post(gen_video_url, data=json.dumps(req), headers=headers, timeout=10)
    print(resp.text)
    video_id = ""
    if resp.status_code == 200:
        resp = json.loads(resp.text)
        video_id = resp["data"]["videoId"]
    return video_id


def gen_video_with_text():
    headers = {'content-type': 'application/json'}
    timestamp = str(int(time.time()))
    message = '+'.join([appkey, secret, timestamp])
    m = hashlib.md5()
    m.update(message.encode('utf-8'))
    signature = m.hexdigest()
    ttsParam = {
        "text": "这是一个简单的测试",
        "speaker": "cissy_meet"
    }

    req = {
        "appKey": appkey,
        "timestamp": timestamp,
        "signature": signature,
        "wetaSpeakerId": wetaSpeakerId,
        "ttsParam": ttsParam
    }

    resp = requests.post(gen_video_url, data=json.dumps(req), headers=headers, timeout=10)
    print(resp.text)
    video_id = ""
    if resp.status_code == 200:
        resp = json.loads(resp.text)
        video_id = resp["data"]["videoId"]
    return video_id


def get_video_result(video_id):
    req = {
        "videoId": video_id
    }
    resp = requests.get(get_video_url, params=req, timeout=10)
    print(resp.text)


if __name__ == "__main__":
    # 音频文件驱动视频合成
    # video_id = gen_video_with_url()

    # 文本驱动视频合成
    video_id = gen_video_with_text()

    get_video_result(video_id)
