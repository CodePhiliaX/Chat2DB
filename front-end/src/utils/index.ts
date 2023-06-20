import { ThemeType } from '@/constants/common';
import { ITreeNode } from '@/typings/tree';

export function getOsTheme() {
  return window.matchMedia &&
    window.matchMedia('(prefers-color-scheme: dark)').matches
    ? ThemeType.Dark
    : ThemeType.Light;
}

export function deepClone(target: any) {
  const map = new WeakMap();

  function isObject(target: any) {
    return (
      (typeof target === 'object' && target) || typeof target === 'function'
    );
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
export function approximateTreeNode(
  treeData: ITreeNode[],
  target: string,
  isDelete = true,
) {
  if (target) {
    const newTree: ITreeNode[] = JSON.parse(JSON.stringify(treeData));
    newTree.map((item, index) => {
      // 暂时不递归，只搜索datasource
      // if(item.children?.length){
      //   item.children = approximateTreeNode(item.children, target,false);
      // }
      if (
        item.name?.toUpperCase()?.indexOf(target?.toUpperCase()) == -1 &&
        isDelete
      ) {
        delete newTree[index];
      } else {
        item.name = item.name?.replace(
          target,
          `<span style='color:red;'>${target}</span>`,
        );
      }
    });
    return newTree.filter((i) => i);
  } else {
    return treeData;
  }
}

// 获取var变量的值
export const callVar = (css: string) => {
  return getComputedStyle(document.documentElement)
    .getPropertyValue(css)
    .trim();
};
