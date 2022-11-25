# coding=utf-8
import time
import hashlib
import urllib2

timestamp = str(int(time.time()))
appkey = '你的appkey'
secret = '你的secret'

message = '+'.join([appkey, secret, timestamp])

m = hashlib.md5()
m.update(message)
signature = m.hexdigest()

http_url = 'https://open.mobvoi.com/api/search/v1?appkey={appkey}&signature={signature}&timestamp={timestamp}&query={query}&address={address}&output={output}&version={version}'

http_url = http_url.format(appkey=appkey,signature=signature,timestamp=timestamp,query="今天天气怎么样",address="中国,上海市,上海市,杨浦区,武东路,,31.308912471391192,121.49982640557423",output="lite",version="43000");

print(http_url)

try:
    # buld http request
    req = urllib2.Request(http_url)
    # header
    req.add_header('User-Agent', 'Mozilla/5.0')
    # post data to server
    resp = urllib2.urlopen(req, timeout=5)
    # get response
    data = resp.read()

    print data

except Exception, e:
    print 'http error'