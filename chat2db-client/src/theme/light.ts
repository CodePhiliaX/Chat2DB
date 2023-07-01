import { theme } from 'antd';
import { PrimaryColorType } from '@/constants';
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
    colorPrimary: '#597EF7',
  },
};

const antdLightTheme = {
  algorithm: [theme.defaultAlgorithm, theme.compactAlgorithm],
  customName: 'light',
  antdPrimaryColor,
  token: {
    ...commonToken,
    colorText: "#232429",
    colorBgBase: '#fff',
    colorHoverBg: '#eee',
    colorBgContainer: '#fff',
    colorBgElevated: '#F8F9FA',
    colorBorder: '#d3d3d4',
  },
};

export default antdLightTheme;
