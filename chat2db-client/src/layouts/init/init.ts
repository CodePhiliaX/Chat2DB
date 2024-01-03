import { clearOlderLocalStorage } from '@/utils';
import initIndexedDB from './initIndexedDB';
import registerElectronApi from './registerElectronApi';
import registerMessage from './registerMessage';
import registerNotification from './registerNotification';
import { getLang, setLang } from '@/utils/localStorage';
import { LangType } from '@/constants';

const init = () => {
  clearOlderLocalStorage();

  initLang();
  initIndexedDB();
  registerElectronApi();

  registerMessage();
  registerNotification();
};

// 初始化语言
const initLang = () => {
  const lang = getLang();
  if (!lang) {
    setLang(LangType.EN_US);
    document.documentElement.setAttribute('lang', LangType.EN_US);
    const date = new Date('2030-12-30 12:30:00').toUTCString();
    document.cookie = `CHAT2DB.LOCALE=${lang};Expires=${date}`;
  }
};

export default init;
