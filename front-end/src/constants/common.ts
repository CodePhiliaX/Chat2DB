export enum ThemeType {
  Light = 'light',
  Dark = 'dark',
  FollowOs = 'followOs'
}

export enum PrimaryColorType {
  Polar_Green = 'polar-green',
  Golden_Purple = 'golden-purple',
  Polar_Blue = 'polar-blue'
}

export enum LangType {
  EN_US = 'en-us',
  ZH_CN = 'zh-cn'
} 

export enum TabOpened {
  IS_OPEN = 'y',
  NOT_OPEN = 'n',
}

export enum ConsoleStatus {
  DRAFT = 'DRAFT',
  RELEASE = 'RELEASE',
}

/** console顶部注释 */
export const consoleTopComment = `-- Chat2DB自然语言转SQL等AI功能 >> https://github.com/alibaba/Chat2DB/blob/main/CHAT2DB_AI_SQL.md\n\n`;