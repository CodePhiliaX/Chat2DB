import React, { FC } from 'react';
import { Outlet } from 'umi';
import AppTheme, { AppThemeProps } from './AppTheme';
interface GlobalLayoutProps extends AppThemeProps {}

const GlobalLayout: FC<GlobalLayoutProps> = ({}) => {
  return (
    <AppTheme>
      <Outlet />
    </AppTheme>
  );
};

export default GlobalLayout;
