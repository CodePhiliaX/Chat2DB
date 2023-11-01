import antdDarkTheme from './background/dark';
import antdDarkDimmedTheme from './background/darkDimmed';
import antdLightTheme from './background/light';
import { ThemeType, PrimaryColorType } from '@/constants';
import { ITheme } from '@/typings/theme';
import lodash from 'lodash';
import { theme } from 'antd';

const antdThemeConfigs = {
  [ThemeType.Dark]: antdDarkTheme,
  [ThemeType.Light]: antdLightTheme,
  [ThemeType.DarkDimmed]: antdDarkDimmedTheme,
};

export function getAntdThemeConfig(_theme: ITheme) {
  const antdThemeConfig = lodash.cloneDeep(antdThemeConfigs[_theme.backgroundColor]);
  antdThemeConfig.token = {
    ...antdThemeConfig.token,
    ...(antdThemeConfig.antdPrimaryColor[_theme.primaryColor as PrimaryColorType] || {}),
  };

  const token = theme.getDesignToken(antdThemeConfig);
  injectThemeVar(token as any, _theme.backgroundColor, _theme.primaryColor);
  return antdThemeConfig;
}

// TODO: 只插入一次
export function injectThemeVar(token: { [key in string]: string }, _theme: ThemeType, primaryColor: PrimaryColorType) {
  let css = '';
  Object.keys(token).map((t) => {
    const attributeName = camelToDash(t);
    let value = token[t];
    // 将需要px的数字带上px
    const joinPxArr = ['fontSize', 'borderRadius', 'borderRadiusLG'];
    if (joinPxArr.includes(t)) {
      value = value + 'px';
    }
    css = css + `--${attributeName}: ${value};\n`;
  });

  const container = `html[theme='${_theme}'],html[primary-color='${primaryColor}']{
    ${css}
  }`;

  const style = document.createElement('style'); // 创建style标签
  style.type = 'text/css';
  style.appendChild(document.createTextNode(container));

  document.head.appendChild(style); // 将style标签插入到head标签中
  window._AppThemePack = token;
}

function camelToDash(str: string) {
  return str.replace(/([A-Z])/g, '-$1').toLowerCase();
}
