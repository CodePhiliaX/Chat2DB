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
    *getAiSystemConfig({}, { put }) {
      const res = (yield configService.getAiSystemConfig({})) as IAiConfig;
      yield put({
        type: 'setAiConfig',
        payload: res,
      });
      if (res.aiSqlSource === AiSqlSourceType.CHAT2DBAI) {
        const res = (yield aiService.getRemainingUse({})) as IRemainingUse;
        yield put({
          type: 'setRemainUse',
          payload: res,
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

        // 如果设置的是Chat2DBAI，需要查询下剩余次数
        if (apiKey && aiSqlSource === AiSqlSourceType.CHAT2DBAI) {
          const res = (yield aiService.getRemainingUse({})) as IRemainingUse;
          yield put({
            type: 'setRemainUse',
            payload: res,
          });
        }
      } catch (error) {}
    },
    *fetchRemainingUse({ payload }, { put }) {
      try {
        const { key } = payload;
        if (!key) {
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
      } catch {}
    },
  },
};

export default AIModel;
