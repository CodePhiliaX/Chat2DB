export function getLinkBasedOnTimezone(): string {
  // 获取当前时区
  const timezone = new Intl.DateTimeFormat().resolvedOptions().timeZone;

  // 定义中国时区的链接和非中国时区的链接
  const chinaLink = "https://chat2db-ai.com";
  const nonChinaLink = "https://chat2db.ai";

  // 判断时区是否为中国的时区，这里简化为检查是否为"Asia/Shanghai"或"Asia/Chongqing"
  // 你也可以根据需要检查时区偏移是否为UTC+8
  if (timezone === "Asia/Shanghai" || timezone === "Asia/Chongqing") {
    return chinaLink;
  } else {
    return nonChinaLink;
  }
}
