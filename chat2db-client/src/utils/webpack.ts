// .umirc里无法识别到@/xxxx 所以单独开了webpack.ts，这个文件只可以写函数，不可以引入其他组件

// 分割出yarn启动命令里添加的参数
export function extractYarnConfig(argv: string[]){
  const newArgv = argv.slice(2)
  const yarn_config:{[k in string]: string} = {}
  newArgv.forEach(t=>{
    if(t && t.startsWith("--")){
      const regex = /--(.+?)=(.+)/;
      const matches = t.match(regex);
      if (matches) {
        const key = matches[1];
        const value = matches[2];
        yarn_config[key] = value
      }
    }
  })
  return yarn_config
}

export function formatDate(date: any, fmt = 'yyyy-MM-dd') {
  if (!date) {
    return '';
  }
  if (typeof date == 'number' || typeof date == 'string') {
    date = new Date(date);
  }
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return '';
  }
  var o: any = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    S: date.getMilliseconds(),
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp('(' + k + ')').test(fmt))
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
}

// 带有时区的时间戳转换为0时区时间戳
export function transitionTimezoneTimestamp(timestamp: number) {
  const timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000
  return timestamp + timezoneOffset
}
