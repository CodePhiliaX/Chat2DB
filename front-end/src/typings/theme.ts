import { ThemeType, PrimaryColorType } from '@/constants/common';

export interface ITheme {
  backgroundColor: Exclude<ThemeType, ThemeType.FollowOs>;
  primaryColor: PrimaryColorType;
}
