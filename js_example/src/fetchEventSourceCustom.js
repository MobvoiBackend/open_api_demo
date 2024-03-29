// 直接用fetch流式读取方式读取sse
export const fetchStream = async (url, data, customOptions) => {
  const { onMessage, onEnd, onCancel, ...options } = customOptions
  return new Promise((r, j) => {
    const abortController = new AbortController()
    const signal = abortController.signal
    onCancel?.(abortController)
    fetch(
      url
      , {
        method: 'post',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify(data),
        ...options,
      },
      {
        signal
      }
    )
      .then(async (response) => {
        const contentType = response.headers.get('Content-Type') || ''
        if (contentType.includes('application/json')) {
          onMessage?.(await response.json())
          return
        }
        const reader = response.body.getReader()
        const decoder = new TextDecoder();
        reader.read().then(function processText({ done, value }) {
          if (done) {
            onEnd && onEnd()
            r(done)
            return
          }
          try {
            onMessage && onMessage(JSON.parse(decoder.decode(value, { stream: true }).split('data:')[1]))
          } catch (e) {
          }
          return reader.read().then(processText)
        })
      })
      .catch((err) => {
        j(err)
      })
  })
}

export const fetchStream2 = (url, customOptions) => {
  const { onmessage, onend, data, ...otherParams } = customOptions;
  const abortController = new AbortController()
    const signal = abortController.signal
    onCancel?.(abortController)
  const decoder = new TextDecoder()
  const push = async (controller, reader) => {
    const { value, done } = await reader.read();
    if (done) {
      controller.close();
      onend?.();
    } else {
      onMessage && onMessage(JSON.parse(decoder.decode(value, { stream: true }).split('data:')[1]))
      controller.enqueue(value);
      push(controller, reader);
    }
  };
  // 发送请求
  return fetch(url
    , {
      method: 'post',
      headers: {
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify(data),
      ...otherParams
    }, {
    signal
  })
    .then(async (response) => {
      const contentType = response.headers.get('Content-Type') || ''
      if (contentType.includes('application/json')) {
        onMessage?.(await response.json())
        return
      }
      // 以ReadableStream解析数据
      const reader = response.body.getReader();
      const stream = new ReadableStream({
        start(controller) {
          push(controller, reader);
        },
      });
      return stream;
    })
    .then((stream) => new Response(stream, { headers: { 'Content-Type': 'text/html' } }).text());
};