import { ThemeType, PrimaryColorType } from '@/constants';

export interface ITheme {
  backgroundColor: Exclude<ThemeType, ThemeType.FollowOs>;
  primaryColor: PrimaryColorType;
}
