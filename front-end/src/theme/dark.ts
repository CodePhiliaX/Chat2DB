import { theme } from 'antd';
import { PrimaryColorType } from '@/constants/common';
import { commonToken } from './common';

type IAntdPrimaryColor = {
  [key in PrimaryColorType]: any;
};

// 主题色
const antdPrimaryColor: IAntdPrimaryColor = {
  [PrimaryColorType.Polar_Green]: {
    colorPrimary: '#3c8618',
  },
  [PrimaryColorType.Golden_Purple]: {
    colorPrimary: '#51258f',
  },
  [PrimaryColorType.Polar_Blue]: {
    colorPrimary: '#1677ff',
  },
};

const antDarkTheme = {
  algorithm: [theme.darkAlgorithm, theme.compactAlgorithm],
  customName: 'dark',
  antdPrimaryColor,
  token: {
    ...commonToken,
    wireframe: true,
    colorBgBase: '#0a0b0c',
    borderRadius: 4,
    borderRadiusLG: '12px',
    colorBgContainer: '#0a0b0c',
    colorBgElevated: '#131418',
    colorBorder: '#36373a',
  },
};

export default antDarkTheme;
