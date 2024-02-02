import type { LucideIcon } from 'lucide-react';
import React, { ReactNode } from 'react';

export interface INavItem {
  key: string;
  icon: LucideIcon;
  component?: ReactNode;
  openBrowser?: string;
  iconFontSize?: number;
  isLoad: boolean;
  name: string;
}
