import { fetchEventSource } from '@microsoft/fetch-event-source'

class FatalError extends Error { }
export function chatStreamApi(url, customOptions) {
  // 解构请求参数
  const { data, onMessage, onError, onCancel, ...options } = customOptions
  const abortController = new AbortController()
  fetchEventSource(url, {
    ...options,
    headers: {
      'accept': 'text/event-stream',
      'content-type': 'application/json; charset=UTF-8',
      'timeout': `${10 * 60 * 1000}`
    },
    method: 'POST',
    body: data ? JSON.stringify(data) : null,
    signal: abortController.signal,
    openWhenHidden: true,
    onmessage(event) {
      onMessage && onMessage(event)
    },
    onerror(err) {
      onError && onError(err)
      abortController.abort()
      if (err instanceof FatalError) {
        throw err
      } else {
        throw err
      }
    }
  })
  onCancel && onCancel(abortController);
}

export const chatSync = async (url, customOptions) => {
  const { data, onMessage, onError, onCancel, ...options } = customOptions
  const abortController = new AbortController()
  return new Promise((r, j) => {
    fetch(
      url
      , {
        method: 'post',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        responseType: 'json',
        body: JSON.stringify(data),
        ...options
      },
      {
        signal: abortController.signal
      }
    )
      .then((res) => {
        return res.json()
      })
      .then((res) => {
        onMessage && onMessage(res)
        r(res)
      })
      .catch((err) => {
        onError&& onError(err)
        j(err)
      })
    onCancel && onCancel(abortController);
  })
}

export const ttsStream = async (url, customOptions) => {
  const { data, onMessage, onEnd, onError, onCancel, ...options } = customOptions
  const abortController = new AbortController()
  return new Promise((r, j) => {
    fetch(
      url
      , {
        method: 'post',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify(data),
        ...options
      },
      {
        signal: abortController.signal
      }
    )
      .then(async (response) => {
        const contentType = response.headers.get('Content-Type') || ''
        if (contentType.includes('application/json')) {
          onMessage && onMessage(await response.json())
          return
        }
        const reader = response.body.getReader()
        reader.read().then(function processText({ done, value }) {
          if (done) {
            onEnd && onEnd()
            r(done)
            return
          }
          try {
            onMessage && onMessage(value)
          } catch (e) {
          }
          return reader.read().then(processText)
        })
      })
      .catch((err) => {
        onError && onError(err)
        j(err)
      })
    onCancel && onCancel(abortController);
  })
}
export const ttsSync = async (url, customOptions) => {
  const { data, onMessage, onError, onCancel, ...options } = customOptions
  const abortController = new AbortController()
  onCancel && onCancel(abortController);
  return new Promise((r, j) => {
    fetch(
      url
      , {
        method: 'post',
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify(data),
        ...options
      },
      {
        signal: abortController.signal
      }
    )
      .then(async (res) => {
        const contentType = res.headers.get('Content-Type') || ''
        if (contentType.includes('application/json')) {
          return res.json()
        }
        return res.arrayBuffer()
      })
      .then((res) => {
        onMessage && onMessage(res)
        r(res)
      })
      .catch((err) => {
        onError(err)
        j(err)
      })
  })
}

