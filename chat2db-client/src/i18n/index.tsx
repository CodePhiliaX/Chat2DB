import React, { Fragment } from 'react';
import { getLang } from '@/utils/localStorage';
import { LangType } from '@/constants';
import zhCN from './zh-cn';
import enUS from './en-us';

const locale = {
  'en-us': enUS,
  'zh-cn': zhCN,
};

export const currentLang: LangType = getLang() || LangType.EN_US;

export const isEn = currentLang === LangType.EN_US;

export const isZH = currentLang === LangType.ZH_CN;

const langSet: Record<string, string> = locale[currentLang];

function i18n(key: keyof typeof zhCN, ...args: any[]) {
  let result = langSet[key];
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

function i18nElement(key: keyof typeof zhCN, ...args: React.ReactNode[]) {
  let str = langSet[key];
  if (str === undefined) {
    return `[${key}]`;
  } else {
    let result: React.ReactNode[] = [];
    str.split(/(\{\d\})/).forEach((item, i) => {
      if (/^\{\d\}$/.test(item)) {
        result.push(
          <Fragment key={i}>
            {args[parseInt(item.substring(1, item.length - 1)) - 1]}
          </Fragment>,
        );
      } else {
        result.push(
          <Fragment key={i}>
            {item.replace(/\{(.+?)\|(.+?)\}/g, (_, singular, plural) => {
              const n = args[0];
              return n == 1 ? singular : plural;
            })}
          </Fragment>,
        );
      }
    });
    return result;
  }
}

export default i18n;
export { i18n, i18nElement };
