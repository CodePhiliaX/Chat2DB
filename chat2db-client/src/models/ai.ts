import aiService from '@/service/ai';
import configService from '@/service/config';
import { IAiConfig } from '@/typings/setting';
import { AiSqlSourceType, IRemainingUse } from '@/typings/ai';
import { Effect, EffectsCommandMap, Reducer } from 'umi';
import { message } from 'antd';
import i18n from '@/i18n';

export interface IAIState {
  aiConfig: IAiConfig;
  remainingUse?: IRemainingUse;
}

export interface IAIModelType {
  namespace: 'ai';
  state: IAIState;
  reducers: {
    setRemainUse: Reducer<IAIState>;
    setAiConfig: Reducer<IAIState>;
  };
  effects: {
    getAiSystemConfig: Effect;
    setAiSystemConfig: Effect;
    fetchRemainingUse: Effect;
  };
}

const AIModel: IAIModelType = {
  namespace: 'ai',
  state: {
    remainingUse: undefined,
    aiConfig: {
      aiSqlSource: AiSqlSourceType.CHAT2DBAI,
    },
  },
  reducers: {
    setAiConfig(state, { payload }) {
      return {
        ...state,
        aiConfig: payload,
      };
    },
    setRemainUse(state, { payload }) {
      return {
        ...state,
        remainingUse: payload,
      };
    },
  },
  effects: {
    *getAiSystemConfig({ }, { put }) {
      const res = (yield configService.getAiSystemConfig({})) as IAiConfig;
      yield put({
        type: 'setAiConfig',
        payload: res,
      });
      if (res?.aiSqlSource === AiSqlSourceType.CHAT2DBAI) {
        yield put({
          type: 'fetchRemainingUse',
          payload: { apiKey: res.apiKey },
        });
      }
    },
    // *setAiSystemConfig({ payload }: { type: string; payload: { aiConfig: IAiConfig } }, { put }: EffectsCommandMap) {
    *setAiSystemConfig({ payload }: { type: any; payload?: IAiConfig }, { put }: EffectsCommandMap) {
      const aiConfig = payload;
      const { aiSqlSource, apiKey } = aiConfig || {};
      try {
        (yield configService.setAiSystemConfig(aiConfig!)) as void;
        message.success(i18n('common.text.submittedSuccessfully'));
        yield put({
          type: 'setAiConfig',
          payload: aiConfig,
        });

        yield put({
          type: 'fetchRemainingUse',
          payload: { apiKey: aiConfig?.apiKey },
        });
      } catch (error) { }
    },
    *fetchRemainingUse({ payload }: { type: any; payload?: { apiKey?: string } }, { put, select }) {
      const currentState = (yield select((state: any) => state.ai)) as IAIState;
      const { apiKey } = payload || {};
      try {
        if (!apiKey || currentState.aiConfig.aiSqlSource !== AiSqlSourceType.CHAT2DBAI) {
          yield put({
            type: 'setRemainUse',
            payload: undefined,
          });
          return;
        }
        const res = (yield aiService.getRemainingUse({})) as IRemainingUse;
        yield put({
          type: 'setRemainUse',
          payload: res,
        });
        return res;
      } catch { }
    },
  },
};

export default AIModel;
