import { ThemeType, OSType } from '@/constants';
import { ITreeNode } from '@/typings';
import clipboardCopy from 'copy-to-clipboard';
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
export function approximateTreeNode(treeData: ITreeNode[], target: string, isDelete = true) {
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

// 处理console的保存和删除操作
export function handleLocalStorageSavedConsole(id: number, type: 'save' | 'delete', text?: string) {
  const saved = localStorage.getItem(`timing-auto-save-console-v1`);
  let savedObj: any = {};
  if (saved) {
    savedObj = JSON.parse(saved);
  }

  if (type === 'save') {
    savedObj[id] = text || '';
  } else if (type === 'delete') {
    delete savedObj[id];
  }

  localStorage.setItem(`timing-auto-save-console-v1`, JSON.stringify(savedObj));
}

// 获取保存的console
export function readLocalStorageSavedConsoleText(id: number) {
  const saved = localStorage.getItem(`timing-auto-save-console-v1`);
  let savedObj: any = {};
  if (saved) {
    savedObj = JSON.parse(saved);
  }
  return savedObj[id] || '';
}

// 清理就版本不兼容的LocalStorage
export function clearOlderLocalStorage() {
  if (localStorage.getItem('app-local-storage-versions') !== 'v2') {
    localStorage.clear();
    localStorage.setItem('app-local-storage-versions', 'v2');
  }
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

// Copy
export function copy(message: string) {
  clipboardCopy(message);
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
    userAgent
  }
}

// os is mac or windows
export function OSnow(): {
  isMac: boolean;
  isWin: boolean;
} {
  const agent = navigator.userAgent.toLowerCase();
  const isMac = /macintosh|mac os x/i.test(navigator.userAgent);
  const isWin = agent.indexOf("win32") >= 0 || agent.indexOf("wow32") >= 0 || agent.indexOf("win64") >= 0 || agent.indexOf("wow64") >= 0
  return {
    isMac,
    isWin
  }
}

