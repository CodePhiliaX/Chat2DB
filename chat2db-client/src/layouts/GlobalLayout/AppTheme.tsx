import { ReactNode, memo } from 'react';
import { ThemeProvider } from '@chat2db/ui';
import AppTitleBar from './AppTitleBar';
import { useGlobalStore } from '@/store/global';
import { settingSelectors } from '@/store/global/selectors';

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
