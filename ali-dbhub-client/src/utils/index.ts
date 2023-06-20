import i18n, { isEN } from "@/i18n";
import { TreeNodeType, OSType } from '@/utils/constants';
import { ITreeNode } from '@/types';
import querystring from 'query-string';

// TODO: 
// 我们有的版本
// 1. 线上dome版本 / 本地jar包运行版本
// 2. 本地调试版本
// 3. 桌面端版本
export const env = (() => {
  const { host } = location;
  // 本地jar包启用的服务
  if (host.indexOf('127.0.0.1:7001') > -1) {
    return 'jar';
  }
  // 桌面端
  if (host.indexOf('dist/index.html') > -1) {
    return 'desktop';
  }
})();

export function formatDate(date:any, fmt = 'yyyy-MM-dd') {
  if (!date) {
    return '';
  }
  if (typeof date == 'number' || typeof date == 'string') {
    date = new Date(date);
  }
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return '';
  }
  var o:any = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    S: date.getMilliseconds(),
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k  in o)
    if (new RegExp('(' + k + ')').test(fmt))
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
}

const monthNamesEn = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export function formatNaturalDate(date: any) {
  if (!date) {
    return '';
  }
  if (typeof date == 'number' || typeof date == 'string') {
    date = new Date(date);
  }
  const d = date as Date;
  const D = new Date();
  const i = +d;
  const diff = i - +D;
  const minutes = Math.abs(diff / 60000);
  if (minutes < 1) {
    return diff > 0 ? i18n('common.tip.now') : i18n('common.tip.justNow');
  }
  if (minutes < 60) {
    return `${i18n('common.data.minute', ~~minutes)}${diff > 0 ? i18n('common.tip.later') : i18n('common.tip.ago')}`;
  }
  const relativeIndex = [-1, 0, 1].findIndex(t => {
    const D = new Date();
    const date = D.getDate() + t;
    D.setDate(date);
    D.setHours(0, 0, 0, 0);
    const start = +D;
    D.setDate(date + 1);
    const end = +D;
    return i > start && i <= end;
  });
  if (relativeIndex > -1) {
    const t = formatDate(d, 'hh:mm')
    return [
      i18n('common.tip.yesterday', t),
      t,
      i18n('common.tip.tomorrow', t),
    ][relativeIndex]
  }
  if (d.getFullYear() === D.getFullYear()) {
    return isEN() ? `${d.getDate()} ${monthNamesEn[d.getMonth()]}` : formatDate(d, 'M月d日');
  }
  return formatDate(d);
}

export function safeAccess<T = any>(obj: any, str: string) {
  return str.split('.').reduce((o, k) => (o ? o[k] : undefined), obj) as T;
};

export interface IToTreeProps{
  parent?: any;
  data: any[];
  name?: string;
  type?: string;
  nodeType: TreeNodeType;
  isLeaf?: boolean;
}

export function toTreeList(props:IToTreeProps){
  const {parent,data,name='name',type = 'columnType',nodeType,isLeaf=true} = props
  return data?.map((item,index)=>{
    return {
      key: `${index+1}-${index+1}`,
      dataType: item[type],
      nodeType: nodeType,
      name: item[name],
      isLeaf,
      parent
    }
  })
}

// 生成一个随机数
export function createRandom(minNum:number,maxNum:number){
  return Math.floor(Math.random()*(maxNum-minNum+1)+minNum); 
}

// 
export function createRandomId(length:number){
}

// 模糊匹配树并且高亮
export function approximateTreeNode(treeData: ITreeNode[], target: string, isDelete = true){
  if(target){
    const newTree:ITreeNode[] = JSON.parse(JSON.stringify(treeData));
    newTree.map((item, index) => {
      // 暂时不递归，只搜索datasorce
      // if(item.children?.length){
      //   item.children = approximateTreeNode(item.children, target,false);
      // }
      if(item.name?.toUpperCase()?.indexOf(target?.toUpperCase()) == -1 && isDelete){
        delete newTree[index];
      }else{
        item.name = item.name?.replace(target,`<span style='color:red;'>${target}</span>`);
      }
    })
    return newTree.filter(i=>i)
  }else{
    return treeData
  }
}

/**
 * 获取参数 
 * @returns 
 */
export function getLocationHash(){
    const rightHash = location.hash.split('?')[1]
    const params:any = {}
    if (rightHash) {
      const arr = rightHash.split('&')
      arr.map(item => {
        const splitRes = item.split('=')
        params[splitRes[0]] = splitRes[1]
      })
    }
    return params
}

// 储存当前页面的hash
export function setCurrentPosition(){
  const hash = location.hash
  localStorage.setItem('lastPosition',hash)
}

// 获取上一次页面的hash
export function getLastPosition(){
  return localStorage.getItem('lastPosition')
}

// 获取var变量的值
export const callVar = (css: string) => {
  return getComputedStyle(document.documentElement)
    .getPropertyValue(css)
    .trim();
};

// os is mac or windows
export const OSnow = function():OSType{
  var agent = navigator.userAgent.toLowerCase();
  var isMac = /macintosh|mac os x/i.test(navigator.userAgent);
  if (agent.indexOf("win32") >= 0 || agent.indexOf("wow32") >= 0 || agent.indexOf("win64") >= 0 || agent.indexOf("wow64") >= 0) {
      return OSType.WIN
  } else if(isMac){
    return OSType.MAC
  }else{
    return OSType.RESTS
  }
}()

export const rootScrollingElement = document.scrollingElement as HTMLElement || document.documentElement || document.body;

export function scrollPage(position: number, element?: HTMLElement, timeScale = 0) {
  return new Promise(r => {
    const scrollingElement = element || rootScrollingElement;
    position = Math.max(0, Math.min(position, scrollingElement.scrollHeight - scrollingElement.clientHeight));
    const start = scrollingElement.scrollTop;
    const duration = Math.abs(position - start) ** .3 * 50 * timeScale;
    const startTime = +new Date();
    (function animate() {
      let t = duration ? (+new Date() - startTime) / duration : 1;
      if (t < 1) {
        requestAnimationFrame(animate);
      } else {
        t = 1;
      }
      const newPosition = (Math.sin(Math.PI * (t - .5)) / 2 + .5) * (position - start) + start;
      scrollingElement.scrollTop = newPosition;
      if (t == 1) {
        scrollingElement.scrollTop = position;
        r(undefined);
      }
    })();
  });
}

// 获取地址栏 ? 参数 如果换成历史模式需要改这里
export function qs<T>() {
  const parms:unknown = querystring.parse(location.hash.split('?')[1])
  return parms as T
}

export function deepClone(target:any) {
  const map = new WeakMap()
  
  function isObject(target:any) {
      return (typeof target === 'object' && target ) || typeof target === 'function'
  }

  function clone(data:any) {
      if (!isObject(data)) {
          return data
      }
      if ([Date, RegExp].includes(data.constructor)) {
          return new data.constructor(data)
      }
      if (typeof data === 'function') {
          return new Function('return ' + data.toString())()
      }
      const exist = map.get(data)
      if (exist) {
          return exist
      }
      if (data instanceof Map) {
          const result = new Map()
          map.set(data, result)
          data.forEach((val, key) => {
              if (isObject(val)) {
                  result.set(key, clone(val))
              } else {
                  result.set(key, val)
              }
          })
          return result
      }
      if (data instanceof Set) {
          const result = new Set()
          map.set(data, result)
          data.forEach(val => {
              if (isObject(val)) {
                  result.add(clone(val))
              } else {
                  result.add(val)
              }
          })
          return result
      }
      const keys = Reflect.ownKeys(data)
      const allDesc = Object.getOwnPropertyDescriptors(data)
      const result = Object.create(Object.getPrototypeOf(data), allDesc)
      map.set(data, result)
      keys.forEach(key => {
          const val = data[key]
          if (isObject(val)) {
              result[key] = clone(val)
          } else {
              result[key] = val
          }
      })
      return result
  }

  return clone(target)
}

