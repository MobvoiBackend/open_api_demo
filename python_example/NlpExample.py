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

http_url = 'https://open.mobvoi.com/api/nlp/v1?appkey={appkey}&signature={signature}&timestamp={timestamp}&query={query}'

http_url = http_url.format(appkey=appkey,signature=signature,timestamp=timestamp,query="一千乘以一千等于多少");

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