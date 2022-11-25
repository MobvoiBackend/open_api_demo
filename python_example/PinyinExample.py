# coding=utf-8
import time
import hashlib
import urllib2
import json

timestamp = str(int(time.time()))
appkey = '你的appkey'
secret = '你的secret'

message = '+'.join([appkey, secret, timestamp])

m = hashlib.md5()
m.update(message)
signature = m.hexdigest()

param = {}
param['appkey'] = appkey
param['signature'] = signature
param['timestamp'] = timestamp
param['uid'] = '真实用户的唯一id'
param['text'] = '你好小问,今天天气怎么样!'

requestParam = json.dumps(param)

http_url = 'https://open.mobvoi.com/api/tts/v1/analysis'

try:
    # buld http request
    req = urllib2.Request(http_url,requestParam)
    # header
    req.add_header('User-Agent', 'Mozilla/5.0')
    req.add_header('Content-Type','application/json')
    # post data to server
    resp = urllib2.urlopen(req, timeout=5)
    # get response
    data = resp.read()

    print data

except Exception, e:
    print 'http error %s' % e
