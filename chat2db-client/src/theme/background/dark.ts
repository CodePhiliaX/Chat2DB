import { theme } from 'antd';
import { PrimaryColorType } from '@/constants';
import { commonToken } from '../common';

type IAntdPrimaryColor = {
  [key in PrimaryColorType]: any;
};

// 主题色
const antdPrimaryColor: IAntdPrimaryColor = {
  [PrimaryColorType.Polar_Green]: {
    colorPrimary: '#3c8618',
  },
  [PrimaryColorType.Golden_Purple]: {
    colorPrimary: '#7688c9',
  },
  [PrimaryColorType.Polar_Blue]: {
    colorPrimary: '#1677ff',
  },
  [PrimaryColorType.Silver]: {
    colorPrimary: '#c3b7a4',
  },
  [PrimaryColorType.Red]: {
    colorPrimary: '#fd6874',
  },
  [PrimaryColorType.Orange]: {
    colorPrimary: '#ffa940',
  },
  [PrimaryColorType.Blue2]: {
    colorPrimary: '#009cc7',
  },
  [PrimaryColorType.Gold]: {
    colorPrimary: '#b59a6d',
  },
};

const antDarkTheme = {
  algorithm: [theme.darkAlgorithm, theme.compactAlgorithm],
  customName: 'dark',
  antdPrimaryColor,
  token: {
    ...commonToken,
    colorTextBase: 'rgb(241, 241, 244)',
    colorBgBase: '#0a0b0c',
    colorHoverBg: 'hsla(0, 0%, 100%, 0.03)',
    colorBgContainer: '#0a0b0c',
    colorBgElevated: '#131418',
    colorBorder: 'rgba(54, 55, 58,0.4)',
    colorBorderSecondary: 'rgba(54, 55, 58,0.4)',
  },
};

export default antDarkTheme;
