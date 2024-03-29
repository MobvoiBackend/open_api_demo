import {chatStreamApi, chatSync, ttsStream, ttsSync} from './apis.js';
import md5 from 'js-md5';

export default class openApiRequest {
    urlText = 'https://open-ka.mobvoi.com';
    urlTts = 'https://open.mobvoi.com';

    AppKey = '你的AppKey';
    AppSecret = '你的AppSecret';
    constructor(params) {
        if (
            !params?.AppKey ||
            !params?.AppSecret ||
            !params?.urlText ||
            !params?.urlTts
        ) {
            // throw new Error('请填写AppKey、AppSecret、urlTts、urlText')
        }
        this.AppKey = params?.AppKey || this.AppKey;
        this.AppSecret = params?.AppSecret || this.AppSecret;
        this.urlText = params?.urlText || this.urlText;
        this.urlTts = params?.urlTts || this.urlTts;
    }
    // chat 流式
    chatStream(obj) {
        const url = this.urlText + '/api/chat/v2/chat';
        const timestamp = Date.parse(new Date()) / 1000;
        const {data, ...other} = obj;
        const options = {
            data: {
                appkey: this.AppKey,
                timestamp: timestamp + '',
                signature: md5(
                    this.AppKey + '+' + this.AppSecret + '+' + timestamp
                ),
                model: 'uclai-large',
                stream: true,
                ...data,
            },
            ...other,
        };
        return chatStreamApi(url, options);
    }
    // chat 异步阻塞
    chatSync(obj) {
        const url = this.urlText + '/api/chat/v2/chat';
        const timestamp = Date.parse(new Date()) / 1000;
        const {data, ...other} = obj;
        const options = {
            data: {
                appkey: this.AppKey,
                timestamp: timestamp + '',
                signature: md5(
                    this.AppKey + '+' + this.AppSecret + '+' + timestamp
                ),
                model: 'uclai-large',
                stream: false,
                ...data,
            },
            ...other,
        };
        return chatSync(url, options);
    }

    // tts流式请求
    ttsStream(obj) {
        const url = this.urlTts + '/api/tts/v1';
        const timestamp = Date.parse(new Date()) / 1000;
        const {data, ...other} = obj;
        const options = {
            data: {
                appkey: this.AppKey,
                timestamp,
                signature: md5(
                    this.AppKey + '+' + this.AppSecret + '+' + timestamp
                ),
                streaming: true,
                ...data,
            },
            ...other,
        };
        return ttsStream(url, options);
    }
    // tts异步阻塞
    ttsSync(obj) {
        const url = this.urlTts + '/api/tts/v1';
        const timestamp = Date.parse(new Date()) / 1000;
        const {data, ...other} = obj;
        const options = {
            data: {
                appkey: this.AppKey,
                timestamp,
                signature: md5(
                    this.AppKey + '+' + this.AppSecret + '+' + timestamp
                ),
                streaming: false,
                ...data,
            },
            ...other,
        };

        return ttsSync(url, options);
    }

    static changeOptionsToParams(paras) {}
}
