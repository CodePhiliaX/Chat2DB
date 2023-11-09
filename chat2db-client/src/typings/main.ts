import React from 'react';

export interface INavItem {
  key: string;
  icon: string;
  component?: React.ReactNode;
  openBrowser?: string;
  iconFontSize?: number;
  isLoad: boolean;
  name: string;
}
