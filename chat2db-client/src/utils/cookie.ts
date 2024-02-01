import dayjs from 'dayjs';

const COOKIE_CACHE_DAYS = 30;

export const setCookie = (key: string, value: string | undefined) => {
  const expires = dayjs().add(COOKIE_CACHE_DAYS, 'day').toISOString();

  // eslint-disable-next-line unicorn/no-document-cookie
  document.cookie = `${key}=${value};expires=${expires};path=/;`;
};

export const getCookie = (name: string) => {
  // 添加等号是为了后面在字符串中进行匹配
  let cookieName = name + '=';
  // 分割 document.cookie 字符串，使用 ; 分隔成数组
  let cookieArray = document.cookie.split(';');

  for (let i = 0; i < cookieArray.length; i++) {
    let cookie = cookieArray[i];
    // 去除cookie前的空白字符
    while (cookie.charAt(0) === ' ') {
      cookie = cookie.substring(1);
    }
    // 检查cookie是否以所需的name开头
    if (cookie.indexOf(cookieName) === 0) {
      // 返回cookie值，它位于等号后面
      return cookie.substring(cookieName.length, cookie.length);
    }
  }
  // 如果没有找到该cookie，返回空字符串
  return '';
};
