export const chatError = {
  CHAT2DB_KEY_INVALID: 'apikey 不在我们的数据库中需要扫码登录',
  CHAT2DB_KEY_LIMIT: '次数用完了，需要发起推广',
  CHAT2DB_KEY_EXPIRED: '到过期时间了',
  CHAT2DB_SERVICE_BUSY: '这个异常就稍后重试就行了',
  CHAT2DB_AUTH_HEADER_MISSING: '这个是 http 请求 header 没传 Authorization 字段，这个你看要怎么处理',
  CHAT2DB_AUTH_TOKEN_MISSING:
    '这个是 http 请求 header 中 Authorization 后面没有以 Bearer 开头，也是传的认证信息有问题，你看要怎么处理',
  CHAT2DB_SERVICE_ERROR: '这个是出了意料之外的异常要联系管理员',
  CHAT2DB_BAD_JSON_FORMAT: '这个是传的请求不是 json 格式',
  CHAT2DB_HTTP_METHOD_INVALID: '这个是传的请求不是 post 请求，目前给 openai 的请求必须是 post',
};

export const chatErrorCodeArr = Object.keys(chatError);

export const chatErrorToLogin = ['CHAT2DB_KEY_INVALID', 'CHAT2DB_AUTH_HEADER_MISSING', 'CHAT2DB_AUTH_TOKEN_MISSING'];
export const chatErrorForKey = ['CHAT2DB_KEY_LIMIT', 'CHAT2DB_KEY_EXPIRED'];
