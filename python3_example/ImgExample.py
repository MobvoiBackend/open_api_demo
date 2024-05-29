# coding=utf-8
import time
import hashlib
import os
import json
import requests
import traceback

timestamp = str(int(time.time()))
appKey = '你的appKey'
secret = '你的secret'

message = '+'.join([appKey, secret, timestamp])

m = hashlib.md5()
m.update(message.encode("utf8"))
signature = m.hexdigest()

http_url = 'https://open.mobvoi.com/api/imgAsync'

def sample():
    data = {
        'prompt': '生成一个狮子头像，长宽比1比1，4k高清，头部特写，油画风格',
        'appKey': appKey,
        'timestamp': timestamp,
        'signature': signature
    }
    try:
        headers = {'Content-Type': 'application/json'}
        response = requests.post(url=http_url, headers=headers, data=json.dumps(data))
        content = response.content.decode('utf-8')

        print('server response is {0}'.format(content))
        jsonObj = json.loads(content)
        print('visit this url to get image generate result: https://open.mobvoi.com/api/asyncGenImg/getResult?taskId={0}'.format(jsonObj['data']['taskId']))
    except Exception as e:
        traceback.print_exc()

def main():
    sample()

if __name__ == '__main__':
    main()
