/**
 * 获取url参数
 * @param paramName
 * @returns
 */

export function getUrlParam(paramName) {
  // 获取当前URL
  const currentUrl = window.location.href;

  // 获取URL中的查询字符串
  const queryString = currentUrl.split('?')[1];

  // 如果没有查询字符串，直接返回
  if (queryString === undefined) {
    return;
  }

  // 将查询字符串分割成数组
  const paramList = queryString.split('&');

  // 遍历每个参数
  for (let i = 0; i < paramList.length; i++) {
    // 检查每个参数的名称是否匹配
    const param = paramList[i].split('=')[0];
    if (param === paramName) {
      // 返回参数值
      return paramList[i].split('=')[1];
    }
  }

  // 如果没有找到参数，则返回null
  return null;
}
/**
 * 更新URL的参数
 * @param key
 * @param value
 * @returns
 */
export function updateQueryStringParameter(key, value) {
  const uri = window.location.href;
  if (!value) {
    return uri;
  }
  const re = new RegExp('([?&])' + key + '=.*?(&|$)', 'i');
  const separator = uri.indexOf('?') !== -1 ? '&' : '?';
  if (uri.match(re)) {
    return uri.replace(re, '$1' + key + '=' + value + '$2');
  } else {
    return uri + separator + key + '=' + value;
  }
}

/**
 * 格式化参数
 * @param obj
 * @returns
 */
export function formatParams(obj: { [key: string]: any }) {
  const params = new URLSearchParams();
  Object.entries(obj).forEach(([key, value]) => {
    if (value === undefined || value === null) {
      return;
    }
    if (Array.isArray(value)) {
      value.forEach((item) => {
        params.append(key, item);
      });
    } else {
      params.append(key, value);
    }
  });
  return params.toString();
}
