import { ThemeType } from '@/constants';
import { ITreeNode } from '@/typings';
import lodash from 'lodash';

export function getOsTheme() {
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
    ? ThemeType.Dark
    : ThemeType.Light;
}

export function deepClone(target: any) {
  const map = new WeakMap();

  function isObject(target: any) {
    return (typeof target === 'object' && target) || typeof target === 'function';
  }

  function clone(data: any) {
    if (!isObject(data)) {
      return data;
    }
    if ([Date, RegExp].includes(data.constructor)) {
      return new data.constructor(data);
    }
    if (typeof data === 'function') {
      return new Function('return ' + data.toString())();
    }
    const exist = map.get(data);
    if (exist) {
      return exist;
    }
    if (data instanceof Map) {
      const result = new Map();
      map.set(data, result);
      data.forEach((val, key) => {
        if (isObject(val)) {
          result.set(key, clone(val));
        } else {
          result.set(key, val);
        }
      });
      return result;
    }
    if (data instanceof Set) {
      const result = new Set();
      map.set(data, result);
      data.forEach((val) => {
        if (isObject(val)) {
          result.add(clone(val));
        } else {
          result.add(val);
        }
      });
      return result;
    }
    const keys = Reflect.ownKeys(data);
    const allDesc = Object.getOwnPropertyDescriptors(data);
    const result = Object.create(Object.getPrototypeOf(data), allDesc);
    map.set(data, result);
    keys.forEach((key) => {
      const val = data[key];
      if (isObject(val)) {
        result[key] = clone(val);
      } else {
        result[key] = val;
      }
    });
    return result;
  }

  return clone(target);
}

// 模糊匹配树并且高亮
export function approximateTreeNode(treeData: ITreeNode[], target: string = '', isDelete = true) {
  if (target) {
    const newTree: ITreeNode[] = lodash.cloneDeep(treeData || []);
    newTree.map((item, index) => {
      // 暂时不递归，只搜索datasource
      // if(item.children?.length){
      //   item.children = approximateTreeNode(item.children, target,false);
      // }
      if (item.name?.toUpperCase()?.indexOf(target?.toUpperCase()) == -1 && isDelete) {
        delete newTree[index];
      } else {
        item.name = item.name?.replace(target, `<span style='color:red;'>${target}</span>`);
      }
    });
    return newTree.filter((i) => i);
  } else {
    return treeData;
  }
}

// 模糊匹配树并且高亮
export function approximateList<T, K extends keyof T>(
  data: T[],
  target: string,
  // @ts-ignore'
  keyName: K = 'name',
  isDelete = true,
) {
  if (target) {
    const newData: T[] = lodash.cloneDeep(data || []);
    newData.map((item, index) => {
      // 暂时不递归，只搜索datasource
      // if(item.children?.length){
      //   item.children = approximateTreeNode(item.children, target,false);
      // }
      // @ts-ignore'
      if (item[keyName]?.toUpperCase()?.indexOf(target?.toUpperCase()) == -1 && isDelete) {
        delete newData[index];
      } else {
        // @ts-ignore'
        item[keyName] = item[keyName]?.replace(target, `<span style='color:red;'>${target}</span>`);
      }
    });
    return newData.filter((i) => i);
  } else {
    return data;
  }
}

// 获取var变量的值
export const callVar = (css: string) => {
  return getComputedStyle(document.documentElement).getPropertyValue(css).trim();
};

// 给我一个 obj[]， 和 obj的 key 和 value，给你返index
export function findObjListValue<T, K extends keyof T>(list: T[], key: K, value: any) {
  let flag = -1;
  list.forEach((t: T, index) => {
    Object.keys(t).forEach((j: K) => {
      if (j === key && t[j] === value) {
        flag = index;
      }
    });
  });
  return flag;
}

// 清理就版本不兼容的LocalStorage
export function clearOlderLocalStorage() {
  if (localStorage.getItem('app-local-storage-versions') !== 'v3') {
    localStorage.clear();
    localStorage.setItem('app-local-storage-versions', 'v3');
  }
}

// 退出登录清理一些记录位置的localStorage
export function logoutClearSomeLocalStorage() {
  localStorage.removeItem('current-workspace-database');
  localStorage.removeItem('cur-connection');
  localStorage.removeItem('active-console-id');
  localStorage.removeItem('curPage');
}

// 判断是否需要更新版本
export function isVersionHigher(version: string, currentVersion: string): boolean {
  // 按照 . 分割版本号
  const versionParts = version.split('.');
  const currentVersionParts = currentVersion.split('.');

  // 按照从左到右的顺序比较每一位的大小
  for (let i = 0; i < versionParts.length; i++) {
    const part = parseInt(versionParts[i]);
    const currentPart = parseInt(currentVersionParts[i] || '0');

    if (part > currentPart) {
      return true;
    } else if (part < currentPart) {
      return false;
    }
  }

  // 如果两个版本号完全相等，则返回false
  return false;
}

// 获取应用的一些基本信息
export function getApplicationMessage() {
  const env = __ENV__;
  const versions = __APP_VERSION__;
  const buildTime = __BUILD_TIME__;
  const userAgent = navigator.userAgent;
  return {
    env,
    versions,
    buildTime,
    userAgent,
  };
}

// os is mac or windows
export function osNow(): {
  isMac: boolean;
  isWin: boolean;
} {
  const agent = navigator.userAgent.toLowerCase();
  const isMac = /macintosh|mac os x/i.test(navigator.userAgent);
  const isWin =
    agent.indexOf('win32') >= 0 ||
    agent.indexOf('wow32') >= 0 ||
    agent.indexOf('win64') >= 0 ||
    agent.indexOf('wow64') >= 0;
  return {
    isMac,
    isWin,
  };
}

// 桌面端用hash模式，web端用history模式，路由跳转
export function navigate(path: string) {
  if (__ENV__ === 'desktop') {
    window.location.replace(`#${path}`);
  } else {
    window.location.replace(path);
  }
}

// 获取cookie
export function getCookie(name: string) {
  const arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'));
  if (arr != null) {
    return decodeURIComponent(arr[2]);
  }
  return null;
}

// 注释出参的含义
export function compareVersion(version1: string, version2: string) {
  const version1Arr = version1.split('.');
  const version2Arr = version2.split('.');
  const v1 = Number(version1Arr.join(''));
  const v2 = Number(version2Arr.join(''));
  if (v1 > v2) {
    return 1;
  } else if (v1 < v2) {
    return -1;
  }
  return 0;
}

// 把剪切板的内容转成二维数组
export function clipboardToArray(text: string): Array<Array<string | null>> {
  if (!text) {
    return [[]];
  }
  try {
    const rows = text.split('\n');
    const array2D = rows.map((row) => row.split('\t'));
    return array2D;
  } catch {
    console.log('copy error');
    return [[]];
  }
}

// Copy
export function copy(message: string) {
  // clipboardCopy(message);
  navigator.clipboard.writeText(message);
}

// 二维数组复制
export function tableCopy(array2D: Array<Array<string | null>>) {
  try {
    const text = array2D.map((row) => row.join('\t')).join('\n');
    navigator.clipboard.writeText(text);
  } catch {
    console.log('copy error');
  }
}
