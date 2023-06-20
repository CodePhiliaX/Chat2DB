import React, { memo, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import { Modal, Radio, Input, message, Select, Button } from 'antd';
import configService, { IChatGPTConfig } from '@/service/config';
import BrandLogo from '@/components/BrandLogo';
import themeDarkImg from '@/assets/img/theme-dark.png';
import themeLightImg from '@/assets/img/theme-light.png';
import themeAutoImg from '@/assets/img/theme-auto.png';
import { getOsTheme } from '@/utils';
import i18n, { currentLang } from '@/i18n';
import { ThemeType } from '@/constants/common';
import { LangType } from '@/constants/common';
import { useTheme } from '@/hooks';
import { APP_NAME, GITHUB_URL } from '@/constants/appConfig';
import { setLang as setLangLocalStorage } from '@/utils/localStorage'

const { Option } = Select;


interface IProps {
  className?: string;
  text?: string;
}

export enum AiSqlSourceType {
  OPENAI = 'OPENAI',
  RESTAI = 'RESTAI',
}

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

const menusList = [
  {
    label: i18n('setting.nav.basic'),
    icon: '\ue795',
    body: <BaseBody />,
  },
  {
    label: i18n('setting.nav.customAi'),
    icon: '\ue646',
    body: <SettingAI />,
  },
  {
    label: i18n('setting.nav.proxy'),
    icon: '\ue63f',
    body: <ProxyBody />,
  },
  {
    label: i18n('setting.nav.aboutUs'),
    icon: '\ue60c',
    body: <AboutUs />,
  },
];

export default memo<IProps>(function Setting({ className, text }) {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentMenu, setCurrentMenu] = useState(menusList[0]);
  const [appTheme, setAppTheme] = useTheme();

  const showModal = () => {
    setIsModalVisible(true);
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  function changeMenu(t: any) {
    setCurrentMenu(t);
  }

  return (
    <>
      <div className={classnames(className, styles.box)} onClick={showModal}>
        {text ? (
          <span className={styles.setText}>{text}</span>
        ) : (
            <Iconfont code="&#xe795;"></Iconfont>
          )}
      </div>
      <Modal
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        footer={false}
        width={800}
        maskClosable={false}
      >
        <div className={styles.modalBox}>
          <div className={styles.menus}>
            <div className={classnames(styles.menusTitle)}>
              {i18n('setting.title.setting')}
            </div>
            {menusList.map((t, index) => {
              return (
                <div
                  key={index}
                  onClick={changeMenu.bind(null, t)}
                  className={classnames(styles.menuItem, {
                    [styles.activeMenu]: t.label === currentMenu.label,
                  })}
                >
                  <Iconfont code={t.icon} />
                  {t.label}
                </div>
              );
            })}
          </div>
          <div className={styles.menuContent}>
            <div className={classnames(styles.menuContentTitle)}>
              {currentMenu.label}
            </div>
            {currentMenu.body}
          </div>
        </div>
      </Modal>
    </>
  );
});

// openAI 的设置项
export function SettingAI() {
  const [chatgptConfig, setChatgptConfig] = useState<IChatGPTConfig>({
    apiKey: '',
    httpProxyHost: '',
    httpProxyPort: '',
    restAiUrl: '',
    apiHost: '',
    restAiStream: true,
    aiSqlSource: '',
  });

  useEffect(() => {
    configService.getChatGptSystemConfig().then((res: any) => {
      setChatgptConfig({
        ...res,
        restAiStream: res.restAiStream || true,
        aiSqlSource: res.aiSqlSource || AiSqlSourceType.OPENAI,
      });
    });
  }, []);

  function changeChatgptApiKey() {
    // if (!chatgptKey) {
    //   message.error('请输入ChatGPT-apiKey')
    //   return
    // }
    // apiHost的最后必须为/
    const newChatgptConfig = { ...chatgptConfig };
    if (newChatgptConfig.apiHost && !newChatgptConfig.apiHost?.endsWith('/')) {
      newChatgptConfig.apiHost = newChatgptConfig.apiHost + '/';
    }
    configService.setChatGptSystemConfig(newChatgptConfig).then((res) => {
      message.success(i18n('common.message.successfulConfig'));
    });
  }

  return (
    <>
      <div className={styles.aiSqlSource}>
        <div className={styles.aiSqlSourceTitle}>
          {i18n('setting.title.aiSource')}:
        </div>
        <Radio.Group
          onChange={(e) => {
            setChatgptConfig({ ...chatgptConfig, aiSqlSource: e.target.value });
          }}
          value={chatgptConfig.aiSqlSource}
        >
          <Radio value={AiSqlSourceType.OPENAI}>Open Ai</Radio>
          <Radio value={AiSqlSourceType.RESTAI}>
            {i18n('setting.tab.custom')}
          </Radio>
        </Radio.Group>
      </div>
      {chatgptConfig.aiSqlSource === AiSqlSourceType.OPENAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.apiKey')}
              value={chatgptConfig.apiKey}
              onChange={(e) => {
                setChatgptConfig({ ...chatgptConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Api Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.apiHost')}
              value={chatgptConfig.apiHost}
              onChange={(e) => {
                setChatgptConfig({ ...chatgptConfig, apiHost: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>HTTP Proxy Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.httpsProxy', 'host')}
              value={chatgptConfig.httpProxyHost}
              onChange={(e) => {
                setChatgptConfig({
                  ...chatgptConfig,
                  httpProxyHost: e.target.value,
                });
              }}
            />
          </div>
          <div className={styles.title}>HTTP Proxy Prot</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.httpsProxy', 'port')}
              value={chatgptConfig.httpProxyPort}
              onChange={(e) => {
                setChatgptConfig({
                  ...chatgptConfig,
                  httpProxyPort: e.target.value,
                });
              }}
            />
          </div>
        </div>
      )}
      {chatgptConfig.aiSqlSource === AiSqlSourceType.RESTAI && (
        <div>
          <div className={styles.title}>
            {i18n('setting.label.customAiUrl')}
          </div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.customUrl')}
              value={chatgptConfig.restAiUrl}
              onChange={(e) => {
                setChatgptConfig({
                  ...chatgptConfig,
                  restAiUrl: e.target.value,
                });
              }}
            />
          </div>
          <div className={styles.title}>
            {i18n('setting.label.isStreamOutput')}
          </div>
          <div className={classnames(styles.content)}>
            <Radio.Group
              onChange={(e) => {
                setChatgptConfig({
                  ...chatgptConfig,
                  restAiStream: e.target.value,
                });
              }}
              value={chatgptConfig.restAiStream}
            >
              <Radio value={true}>{i18n('common.text.is')}</Radio>
              <Radio value={false}>{i18n('common.text.no')}</Radio>
            </Radio.Group>
          </div>
        </div>
      )}
      <div className={styles.bottomButton}>
        <Button type="primary" onClick={changeChatgptApiKey}>
          {i18n('setting.button.use')}
        </Button>
      </div>
    </>
  );
}

// baseBody 基础设置
export function BaseBody() {
  const [lang, setLang] = useState(currentLang);
  const [currentTheme, setCurrentTheme] = useState(
    localStorage.getItem('theme'),
  );
  const [currentPrimaryColor, setCurrentPrimaryColor] = useState(
    localStorage.getItem('primary-color'),
  );
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
    setLangLocalStorage(e.target.value)
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
      <div className={styles.title}>
        {i18n('setting.title.backgroundColor')}
      </div>
      <ul className={styles.backgroundList}>
        {themeList.map((t) => {
          return (
            <div key={t.code} className={styles.themeItemBox}>
              <div
                className={classnames(
                  { [styles.current]: currentTheme == t.code },
                  styles.themeImg,
                )}
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
                {currentPrimaryColor == item.code && (
                  <Iconfont code="&#xe617;"></Iconfont>
                )}
              </div>
              <div className={styles.colorName}>{item.name}</div>
            </div>
          );
        })}
      </ul>
    </>
  );
}

// 代理设置
export function ProxyBody() {
  const [apiPrefix, setApiPrefix] = useState(window._BaseURL);

  function updateApi(e: any) {
    setApiPrefix(e.target.value);
  }

  function affirmUpdateApi() {
    if (!apiPrefix) {
      return;
    }
    try {
      const xhr = new XMLHttpRequest();
      xhr.withCredentials = true;
      xhr.open('GET', `${apiPrefix}/api/system/get-version-a`);
      xhr.onload = function () {
        if (xhr.status === 200) {
          localStorage.setItem('_BaseURL', apiPrefix);
          location.reload();
        } else {
          message.error(i18n('setting.message.urlTestError'));
        }
      };
      xhr.send();
    } catch {
      message.error(i18n('setting.message.urlTestError'));
    }
  }

  return (
    <>
      <div className={styles.title}>{i18n('setting.label.serviceAddress')}</div>
      <div className={classnames(styles.content, styles.chatGPTKey)}>
        <Input value={apiPrefix} onChange={updateApi} />
      </div>
      <div className={styles.bottomButton}>
        <Button type="primary" onClick={affirmUpdateApi}>
          {i18n('setting.button.use')}
        </Button>
      </div>
    </>
  );
}

// 关于我们
function AboutUs() {
  return (
    <div className={styles.aboutUs}>
      <BrandLogo size={60} className={styles.brandLogo} />
      <div className={styles.brief}>
        <div className={styles.appName}>{APP_NAME}</div>
        <div className={styles.env}>
          {i18n('setting.text.currentEnv')}:{window._ENV}
        </div>
        <div className={styles.version}>
          {i18n('setting.text.currentVersion')}:v{__APP_VERSION__} build
          {__BUILD_TIME__}
        </div>
        <a target="blank" href={GITHUB_URL} className={styles.log}>
          {i18n('setting.text.viewingUpdateLogs')}
        </a>
      </div>
    </div>
  );
}
