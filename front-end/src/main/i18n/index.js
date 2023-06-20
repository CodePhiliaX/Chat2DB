const zhCN = require('./zh-cn');
const en = require('./en');

// TODO: 需要获取渲染进程的语言环境
const isZH = false;

const locale = isZH ? zhCN : en;

const i18n = (key) => locale[key] || key;

module.exports = i18n;
