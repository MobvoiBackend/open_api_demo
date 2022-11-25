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

# buld post body data
boundary = '----------%s' % hex(int(time.time() * 1000))
data = []
data.append('--%s' % boundary)

data.append('Content-Disposition: form-data; name="%s"\r\n' % 'type')
data.append('audio/x-wav;rate=16000')
data.append('--%s' % boundary)

data.append('Content-Disposition: form-data; name="%s"\r\n' % 'appkey')
data.append(appkey)
data.append('--%s' % boundary)

data.append('Content-Disposition: form-data; name="%s"\r\n' % 'timestamp')
data.append(timestamp)
data.append('--%s' % boundary)

data.append('Content-Disposition: form-data; name="%s"\r\n' % 'signature')
data.append(signature)
data.append('--%s' % boundary)


fr = open(r'你的音频文件', 'rb')
data.append('Content-Disposition: form-data; name="%s"; filename="239673789.wav"' % 'file')
data.append('Content-Type: %s\r\n' % 'audio/x-wav;rate=16000')
data.append(fr.read())
fr.close()
data.append('--%s--\r\n' % boundary)

http_url = 'https://open.mobvoi.com/api/asr/v1'
http_body = '\r\n'.join(data)
try:
    # buld http request
    req = urllib2.Request(http_url, data=http_body)
    # header
    req.add_header('Content-Type', 'multipart/form-data; boundary=%s' % boundary)
    req.add_header('User-Agent', 'Mozilla/5.0')
    # post data to server
    resp = urllib2.urlopen(req, timeout=5)
    # get response
    qrcont = resp.read()
    print qrcont


except Exception, e:
    print 'http error'