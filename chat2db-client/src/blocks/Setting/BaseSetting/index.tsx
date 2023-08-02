import { LangType, ThemeType } from '@/constants';
import i18n, { currentLang } from '@/i18n';
import React, { useState } from 'react';
import classnames from 'classnames';
import themeDarkImg from '@/assets/img/theme-dark.png';
import themeLightImg from '@/assets/img/theme-light.png';
import themeAutoImg from '@/assets/img/theme-auto.png';
import { Radio, Select } from 'antd';
import Iconfont from '@/components/Iconfont';
import { setLang as setLangLocalStorage } from '@/utils/localStorage';
import { useTheme } from '@/hooks/useTheme';

import styles from './index.less';

const { Option } = Select;

const themeList = [
  {
    code: ThemeType.Dark,
    name: i18n('setting.text.dark'),
    img: themeDarkImg,
  },
  {
    code: ThemeType.Light,
    name: i18n('setting.text.light'),
    img: themeLightImg,
  },
  {
    code: ThemeType.FollowOs,
    name: i18n('setting.text.followOS'),
    img: themeAutoImg,
  },
  // {
  //   code: 'eyeshield',
  //   name: '护眼',
  //   img: 'https://img.alicdn.com/imgextra/i1/O1CN01KGCqY21uJpuFjEQW2_!!6000000006017-2-tps-181-135.png'
  // },
];

const colorList = [
  {
    code: 'polar-blue',
    name: i18n('setting.label.blue'),
    color: '#1a90ff',
  },
  {
    code: 'polar-green',
    name: i18n('setting.label.green'),
    color: '#1d3712',
  },
  {
    code: 'golden-purple',
    name: i18n('setting.label.violet'),
    color: '#301c4d',
  },
  // {
  //   code: 'sunset-orange',
  //   name: '日暮',
  //   color: "#593815"
  // },
];

// baseBody 基础设置
export default function BaseSetting() {
  const [lang, setLang] = useState(currentLang);
  const [currentTheme, setCurrentTheme] = useState<ThemeType>(localStorage.getItem('theme'));
  const [currentPrimaryColor, setCurrentPrimaryColor] = useState(localStorage.getItem('primary-color'));
  const [appTheme, setAppTheme] = useTheme();

  const changePrimaryColor = (item: any) => {
    const html = document.documentElement;
    html.setAttribute('primary-color', item.code);
    localStorage.setItem('primary-color', item.code);
    setCurrentPrimaryColor(item.code);
    setAppTheme({
      ...appTheme,
      primaryColor: item.code,
    });
  };

  function changeLang(e: any) {
    setLangLocalStorage(e.target.value);
    location.reload();
  }

  function handleChangeTheme(theme: ThemeType) {
    setAppTheme({
      ...appTheme,
      backgroundColor: theme,
    });
    setCurrentTheme(theme);
  }

  return (
    <>
      <div className={styles.title}>{i18n('setting.title.backgroundColor')}</div>
      <ul className={styles.backgroundList}>
        {themeList.map((t) => {
          return (
            <div key={t.code} className={styles.themeItemBox}>
              <div
                className={classnames({ [styles.current]: currentTheme == t.code }, styles.themeImg)}
                onClick={handleChangeTheme.bind(null, t.code)}
                style={{ backgroundImage: `url(${t.img})` }}
              />
              <div className={styles.themeName}>{t.name}</div>
            </div>
          );
        })}
      </ul>
      <div className={styles.title}>{i18n('setting.title.language')}</div>
      <div className={styles.langBox}>
        <Radio.Group onChange={changeLang} value={lang}>
          <Radio value={LangType.ZH_CN}>简体中文</Radio>
          <Radio value={LangType.EN_US}>English</Radio>
        </Radio.Group>
      </div>
      <div className={styles.title}>{i18n('setting.title.themeColor')}</div>
      <ul className={styles.primaryColorList}>
        {colorList.map((item) => {
          return (
            <div key={item.code} className={styles.themeColorItem}>
              <div
                className={styles.colorLump}
                key={item.code}
                onClick={changePrimaryColor.bind(null, item)}
                style={{ backgroundColor: item.color }}
              >
                {currentPrimaryColor == item.code && <Iconfont code="&#xe617;" />}
              </div>
              <div className={styles.colorName}>{item.name}</div>
            </div>
          );
        })}
      </ul>
    </>
  );
}
