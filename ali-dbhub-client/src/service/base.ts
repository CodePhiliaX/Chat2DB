import { extend, ResponseError } from 'umi-request';
import { message } from 'antd';

export type IErrorLevel = 'toast' | 'prompt' | 'critical' | false;
export interface IOptions {
  method?: 'get' | 'post' | 'put' | 'delete';
  mock?: boolean;
  errorLevel?: 'toast' | 'prompt' | 'critical' | false;
  delayTime?: number | true;
}

// TODO:
const codeMessage: { [errorCode: number]: string } = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

enum ErrorCode {
  /** 需要登录 */
  NEED_LOGGED_IN = 'NEED_LOGGED_IN',
}

const noNeedToastErrorCode = [ErrorCode.NEED_LOGGED_IN];

// yapi mock地址
const mockUrl = 'https://yapi.alibaba.com/mock/1000160';

// 桌面端的服务器地址
const desktopServiceUrl = `http://127.0.0.1:${process.env.APP_PORT || '10824'}`;

// 非桌面端的服务器地址
const prodServiceUrl = location.origin;

// 是否自定义了 _BaseURL || 是否为桌面端地址
const baseURL = localStorage.getItem('_BaseURL')
  || (location.href.indexOf('dist/index.html') > -1 ? desktopServiceUrl : prodServiceUrl);

window._BaseURL = baseURL;

const errorHandler = (error: ResponseError, errorLevel: IErrorLevel) => {
  const { response } = error;
  if (!response) return;
  const errorText = codeMessage[response.status] || response.statusText;
  const { status } = response;
  if (errorLevel === 'toast') {
    message.error(`${status}: ${errorText}`);
  }
};

const request = extend({
  // prefix: '/api',
  credentials: 'include', // 默认请求是否带上cookie
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

request.interceptors.request.use((url, options) => {  
  const myOptions:any = {
    ...options,
    headers: {
      ...options.headers,
    }
  }
  if (localStorage.getItem('DBHUB')) {
    myOptions.headers.DBHUB = localStorage.getItem('DBHUB')
  }
  return {
    options: myOptions,
  };
});

request.interceptors.response.use(async (response, options) => {
  const res = await response.clone().json();
  if (window._ENV === 'desktop') {
    const DBHUB = response.headers.get('DBHUB') || ''
    if (DBHUB) {
      localStorage.setItem('DBHUB', DBHUB)
    }
  }
  const { errorCode, codeMessage } = res;
  if (errorCode === ErrorCode.NEED_LOGGED_IN) {
    // window.location.href = '#/login?callback=' + window.location.hash.substr(1);
    const callback = window.location.hash.substr(1).split('?')[0];
    window.location.href =
      '#/login?' + (callback === '/login' ? '' : `callback=${callback}`);
  }

  return response;
});

export default function createRequest<P = void, R = {}>(
  url: string,
  options: IOptions,
) {
  const { method = 'get', mock = false, errorLevel = 'toast', delayTime } = options;

  // 是否需要mock
  const _baseURL = mock ? mockUrl : baseURL;
  return function (params: P) {
    // 在url上按照定义规则拼接params
    const paramsInUrl: string[] = [];
    const _url = url.replace(/:(.+?)\b/, (_, name: string) => {
      const value = params[name];
      paramsInUrl.push(name);
      return `${value}`;
    });

    if (paramsInUrl.length) {
      paramsInUrl.forEach((name) => {
        delete params[name];
      });
    }

    return new Promise<R>((resolve, reject) => {
      let dataName = '';
      switch (method) {
        case 'get':
          dataName = 'params';
          break;
        case 'delete':
          dataName = 'params';
          break;
        case 'post':
          dataName = 'data';
          break;
        case 'put':
          dataName = 'data';
          break;
      }

      request[method](`${_baseURL}${_url}`, { [dataName]: params }).then((res) => {
        if (!res) return;
        const { success, errorCode, errorMessage, data } = res;
        if (
          !success &&
          errorLevel === 'toast' &&
          !noNeedToastErrorCode.includes(errorCode)
        ) {
          delayTimeFn(() => {
            message.error(`${errorCode}: ${errorMessage}`);
            reject(`${errorCode}: ${errorMessage}`);
          }, delayTime)
          return
        }
        // 有些loding效果添加强制延时效果可能会更好看, 可行性待商榷
        delayTimeFn(() => {
          resolve(data);
        }, delayTime)
      })
      .catch((error) => {
        console.log('catch error', error);
        delayTimeFn(() => {
          errorHandler(error, errorLevel);
          reject(error);
        }, delayTime)
      });
    });
  };
}

// 简单的延时函数
function delayTimeFn(callback: Function, time: number | true | undefined) {
  if (time) {
    const timer = setTimeout(() => {
      callback();
      clearInterval(timer);
    }, time && 500);
  } else {
    callback();
  }
}
