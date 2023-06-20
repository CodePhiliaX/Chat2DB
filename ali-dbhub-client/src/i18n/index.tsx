import zhCN from './zh-cn';
import en from './en';
import React, { Fragment } from 'react';

const locale = {
  en,
  'zh-cn': zhCN,
};

const _isEN = (localStorage.getItem('lang') || '').toLowerCase().indexOf('en') > -1;
export function isEN() {
  return _isEN;
}
const langSet:any = locale[_isEN ? 'en' : 'zh-cn'];

export type I18nKey = keyof typeof zhCN;
function i18n(key: keyof typeof zhCN, ...args: any[]): string;
function i18n(key: string, ...args: any[]) {
  let result: string = langSet[key];
  if (result === undefined) {
    return `[${key}]`;
  } else {
    args.forEach((arg, i) => {
      result = result.replace(new RegExp(`\\{${i + 1}\\}`, 'g'), arg);
    });
    if (args.length) {
      result = result.replace(/\{(.+?)\|(.+?)\}/g, (_, singular, plural) => {
        const n = args[0];
        return n == 1 ? singular : plural;
      });
    }
    return result;
  }
}

function i18nElement(key: keyof typeof zhCN, ...args: React.ReactNode[]): React.ReactNode[];
function i18nElement(key: string, ...args: React.ReactNode[]) {
  let str: string = langSet[key];
  if (str === undefined) {
    return `[${key}]`;
  } else {
    let result: React.ReactNode[] = [];
    str.split(/(\{\d\})/).forEach((item, i) => {
      if (/^\{\d\}$/.test(item)) {
        result.push(<Fragment key={i}>{args[parseInt(item.substring(1, item.length - 1)) - 1]}</Fragment>);
      } else {
        result.push(<Fragment key={i}>{item.replace(/\{(.+?)\|(.+?)\}/g, (_, singular, plural) => {
          const n = args[0];
          return n == 1 ? singular : plural;
        })}</Fragment>);
      }
    });
    return result;
  }
}

export default i18n;
export { i18nElement };
