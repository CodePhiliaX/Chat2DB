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
    colorPrimary: '#8276c9',
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

const antdLightTheme = {
  algorithm: [theme.darkAlgorithm, theme.compactAlgorithm],
  customName: 'dark-dimmed',
  antdPrimaryColor,
  token: {
    ...commonToken,
    colorTextBase: 'rgb(241, 241, 244)',
    colorBgBase: 'rgb(28, 33, 40)',
    colorHoverBg: 'hsla(0, 0%, 100%, 0.03)',
    colorBgContainer: 'rgb(28, 33, 40)',
    colorBgElevated: 'rgb(34, 39, 46)',
    colorBorder: 'rgba(55, 62, 71, 0.4)',
    colorBorderSecondary: 'rgba(55, 62, 71, 0.4)',
    controlItemBgActive: 'rgba(241, 241, 244, 0.08);',
    // ...commonToken,
    // colorText: "rgb(241, 241, 244)",
    // colorBgBase: '#191a23',
    // colorHoverBg: 'hsla(0, 0%, 100%, 0.03)',
    // colorBgContainer: '#191a23',
    // colorBgElevated: '#202123',
    // colorBorder: 'rgba(231, 235, 254, 0.075)',
    // colorBorderSecondary: 'rgba(231, 235, 254, 0.075)',
  },
};

export default antdLightTheme;
