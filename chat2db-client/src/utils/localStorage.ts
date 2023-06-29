import { ThemeType, PrimaryColorType, LangType } from '@/constants';
import { ICurWorkspaceParams } from '@/models/workspace';

export function getLang(): LangType {
  return (localStorage.getItem('lang') as LangType) || 'en-us';
}

export function setLang(lang: LangType) {
  return localStorage.setItem('lang', lang);
}

export function getTheme(): ThemeType {
  return (localStorage.getItem('theme') as ThemeType) || ThemeType.Dark;
}

export function setTheme(theme: ThemeType) {
  return localStorage.setItem('theme', theme);
}

export function getPrimaryColor(): PrimaryColorType {
  return (localStorage.getItem('primary-color') as PrimaryColorType) || PrimaryColorType.Polar_Blue;
}

export function setPrimaryColor(primaryColor: PrimaryColorType) {
  return localStorage.setItem('primary-color', primaryColor);
}

export function setCurrentWorkspaceDatabase(value: ICurWorkspaceParams) {
  return localStorage.setItem('current-workspace-database', JSON.stringify(value));
}

export function getCurrentWorkspaceDatabase(): ICurWorkspaceParams {
  const curWorkspaceParams = localStorage.getItem('current-workspace-database');

  if (curWorkspaceParams) {
    return JSON.parse(curWorkspaceParams);
  }
  return {} as ICurWorkspaceParams;
}

export function getCurConnection() {
  const curConnection = localStorage.getItem('cur-connection');
  if (curConnection) {
    return JSON.parse(curConnection);
  }
  return undefined;
}
