import { ReactNode, memo, useEffect } from 'react';
import { ThemeAppearance } from 'antd-style';
import { NeutralColors, PrimaryColors, ThemeProvider } from '@chat2db/ui';
import { CHAT2DB_THEME_APPEARANCE, CHAT2DB_THEME_NEUTRAL_COLOR, CHAT2DB_THEME_PRIMARY_COLOR } from '@/constants';
import { Button } from 'antd';
import AppTitleBar from './AppTitleBar';
import { useGlobalStore } from '@/store/global';
import { settingSelectors } from '@/store/global/selectors';
import { setCookie } from '@/utils/cookie';

export interface AppThemeProps {
  children?: ReactNode;
}

const AppTheme = memo<AppThemeProps>(({ children }) => {
  const { themeMode, primaryColor, neutralColor } = useGlobalStore((state) =>
    settingSelectors.currentBaseSetting(state),
  );
  const setThemeMode = useGlobalStore((state) => state.setThemeMode);

  return (
    <ThemeProvider
      themeMode={themeMode}
      primaryColor={primaryColor}
      neutralColor={neutralColor}
      onThemeModeChange={(themeMode) => {
        setThemeMode(themeMode);
      }}
      onAppearanceChange={(appearance) => {
        console.log('appearance', appearance);
      }}
    >
      <AppTitleBar />
      {children}
    </ThemeProvider>
  );
});

export default AppTheme;
