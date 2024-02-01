import {
  ColorProps,
  NeutralColors,
  PrimaryColors,
  Swatches,
  ThemeProvider,
  neutralColors,
  primaryColors,
} from '@chat2db/ui';
import { Button, Flex, Select } from 'antd';
import React, { useState } from 'react';
import Demo from './demo';
import { useGlobalStore } from '@/store/global';
import { settingSelectors } from '@/store/global/selectors';

const defaultAppearance = 'light';

const primaryColorsSwatches = Object.keys(primaryColors).map((k) => ({
  label: k,
  value: primaryColors[k as keyof typeof primaryColors],
}));

const neutralColorsSwatches = Object.keys(neutralColors).map((k) => ({
  label: k,
  value: neutralColors[k as keyof typeof neutralColors],
}));

export default function HomePage() {
  const { themeMode, primaryColor, neutralColor } = useGlobalStore((state) =>
    settingSelectors.currentBaseSetting(state),
  );
  const [setThemeMode, setPrimaryColor, setNeutralColor] = useGlobalStore((state) => [
    state.setThemeMode,
    state.setPrimaryColor,
    state.setNeutralColor,
  ]);

  const activePrmiaryColor = primaryColorsSwatches.find((c) => c.label === primaryColor);
  const activeNeutralColor = neutralColorsSwatches.find((c) => c.label === neutralColor);


  return (
    <Flex vertical align="center" justify="center" gap={8}>
      <Select
        value={themeMode}
        onChange={(v) => {
          setThemeMode(v);
        }}
        options={[
          {
            label: 'auto',
            value: 'auto',
          },
          {
            label: 'light',
            value: 'light',
          },
          {
            label: 'dark',
            value: 'dark',
          },
        ]}
      />
      <Swatches
        activeColor={activePrmiaryColor}
        colors={primaryColorsSwatches}
        onSelect={(c) => {
          console.log('primaryColors', c);
          setPrimaryColor(c?.label as PrimaryColors);
        }}
      />
      <Swatches
        activeColor={activeNeutralColor}
        colors={neutralColorsSwatches}
        onSelect={(c) => {
          console.log('neutralColors', c);
          setNeutralColor(c?.label as NeutralColors);
        }}
      />

      <Demo />
    </Flex>
  );
}
