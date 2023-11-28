import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

import { message } from 'antd';
import i18n from '@/i18n';

import { IAiConfig } from '@/typings/setting';
import { IRemainingUse, AIType } from '@/typings/ai';
import configService from '@/service/config';
import aiService from '@/service/ai';

export interface ISettingState {
  aiConfig: IAiConfig;
  remainingUse?: IRemainingUse;
  hasWhite: boolean;
}

const initSetting = {
  remainingUse: undefined,
  aiConfig: {
    aiSqlSource: AIType.CHAT2DBAI,
  },
  hasWhite: false,
}

export const useSettingStore: UseBoundStoreWithEqualityFn<StoreApi<ISettingState>> = createWithEqualityFn(
  devtools(() => (initSetting)),
  shallow
);

export const setAiConfig = (aiConfig: IAiConfig) => {
  useSettingStore.setState({ aiConfig });
}

export const setRemainUse = (remainingUse?: IRemainingUse) => {
  useSettingStore.setState({ remainingUse });
}

export const setAiWithWhite = (hasWhite: boolean) => {
  useSettingStore.setState({ hasWhite });
}

export const updateAiWithWhite = (apiKey: string) => {
  configService.getAiWhiteAccess({ apiKey: apiKey ?? '' }).then((res) => {
    setAiWithWhite(res);
  });
}

export const getAiSystemConfig = () => {
  configService.getAiSystemConfig({}).then((res) => {
    setAiConfig(res);
    if (res.aiSqlSource === AIType.CHAT2DBAI && res.apiKey) {
      updateAiWithWhite(res.apiKey);
    }
  });
}

export const setAiSystemConfig = (aiConfig) => {
  configService.setAiSystemConfig(aiConfig).then(() => {
    message.success(i18n('common.text.submittedSuccessfully'));
    setAiConfig(aiConfig);
  })
  if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
    updateAiWithWhite(aiConfig?.apiKey);
  } else {
    setAiWithWhite(false);
  }
}

export const fetchRemainingUse = (apiKey)=>{
  const currentState = useSettingStore.getState();
  if (!apiKey || currentState.aiConfig.aiSqlSource !== AIType.CHAT2DBAI) {
    setRemainUse(undefined);
    return;
  }
  aiService.getRemainingUse().then((res) => {
    setRemainUse(res);
  })
}




