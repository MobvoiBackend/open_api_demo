import asyncio
import hashlib
import json
import time
import requests
import wave

import websockets

appkey = "你的appKey"
secret = "你的app secret"
audio_path = "一句话识别你的本地音频文件地址"
audio_format = "wav"
ws_url = "wss://open.mobvoi.com/ws/asr"

asr_create_task_url = "https://open.mobvoi.com/api/asr/create"
asr_get_task_url = "https://open.mobvoi.com/api/asr/get"
audio_url = "你的音频文件url"


class AsrWsClient:
    def __init__(self) -> None:
        self.audio_path = audio_path
        self.appkey = appkey
        self.ws_url = ws_url
        self.format = audio_format

    def construct_request(self, signal):
        timestamp = str(int(time.time()))
        message = '+'.join([appkey, secret, timestamp])
        m = hashlib.md5()
        m.update(message.encode('utf-8'))
        signature = m.hexdigest()
        req = {
            "signal": signal,
            "contentType": "audio/x-wav;rate=16000",#;codec=pcm;bit=16;",
            "silence_detection": "enable",
            "partial_result": "enable",
            "appKey": appkey,
            "timestamp": timestamp,
            "signature": signature,
        }
        return req

    @staticmethod
    def slice_data(data: bytes, chunk_size: int):
        data_len = len(data)
        offset = 0
        while offset + chunk_size < data_len:
            yield data[offset:offset + chunk_size]
            offset += chunk_size
        else:
            yield data[offset:data_len]

    async def segment_data_processor(self, wav_data: bytes, segment_size: int):
        client_request = self.construct_request("start")
        print(self.ws_url)
        async with websockets.connect(self.ws_url, max_size=1000000000) as ws:
            await ws.send(json.dumps(client_request))
            res = await ws.recv()
            print(res)
            result_type = json.loads(res)["type"]

            for seq, chunk in enumerate(
                    AsrWsClient.slice_data(wav_data, segment_size)):
                try:
                    await ws.send(chunk)
                    rec = await asyncio.wait_for(ws.recv(),
                                                 timeout=0.01)
                    print(rec)
                    result_type = json.loads(rec)["type"]
                    if result_type == "server_error" or result_type == "speech_end":
                        break
                    if result_type == "silence":
                        await ws.send(json.dumps({"signal": "end"}))
                        break
                except asyncio.TimeoutError:
                    pass
            while result_type != "speech_end" or result_type == "server_error":
                try:
                    rec = await asyncio.wait_for(ws.recv(), timeout=1.0)
                except asyncio.TimeoutError:
                    continue
                print(rec)
                result_type = json.loads(rec)["type"]
                if result_type == "silence":
                    await ws.send(json.dumps({"signal": "end"}))
            if result_type == "speech_end":
                result = json.loads(rec)["content"]
                print(f"final result: {result}")

            print("send over")

    async def execute(self):
        with open(self.audio_path, mode="rb") as f:
            data = f.read()
        audio_data = bytes(data)

        if self.format != "wav" and self.format != "pcm":
            raise Exception("format should in wav or pcm")
        segment_size = 16000
        return await self.segment_data_processor(audio_data, segment_size)


def execute():
    asr_http_client = AsrWsClient()
    asyncio.run(asr_http_client.execute())


def call_asr_for_sentence():
    execute()


def call_asr_for_file():
    task_id = create_asr_task()
    time.sleep(10)
    get_asr_task(task_id)


def create_asr_task():
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
        "language": "zh_cn",
        "enable_punctuation": True
    }

    resp = requests.post(asr_create_task_url, data=json.dumps(req), headers=headers, timeout=10)
    print(resp.text)
    task_id = ""
    if resp.status_code == 200:
        resp = json.loads(resp.text)
        task_id = resp["data"]["task_id"]
    return task_id


def get_asr_task(task_id):
    req = {
        "taskId": task_id
    }
    resp = requests.get(asr_get_task_url, params=req, timeout=10)
    print(resp.text)


if __name__ == "__main__":
    # 一句话识别
    call_asr_for_sentence()

    # 语音文件识别
    # call_asr_for_file()
