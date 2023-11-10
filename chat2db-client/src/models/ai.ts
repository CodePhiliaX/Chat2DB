import aiService from '@/service/ai';
import configService from '@/service/config';
import { IAiConfig } from '@/typings/setting';
import { AIType, IRemainingUse } from '@/typings/ai';
import { Effect, EffectsCommandMap, Reducer } from 'umi';
import { message } from 'antd';
import i18n from '@/i18n';

export interface IAIState {
  aiConfig: IAiConfig;
  remainingUse?: IRemainingUse;
  hasWhite: boolean;
}

export interface IAIModelType {
  namespace: 'ai';
  state: IAIState;
  reducers: {
    setRemainUse: Reducer<IAIState>;
    setAiConfig: Reducer<IAIState>;
    setAiWithWhite: Reducer<IAIState>;
  };
  effects: {
    updateAiWithWhite: Effect;
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
      aiSqlSource: AIType.CHAT2DBAI,
    },
    hasWhite: false,
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
    setAiWithWhite(state, { payload }) {
      return {
        ...state,
        hasWhite: payload,
      };
    },
  },
  effects: {
    *updateAiWithWhite({ payload }: { type: any; payload?: { apiKey: string } }, { put }: EffectsCommandMap) {
      const hasAiAccess = yield configService.getAiWhiteAccess({ apiKey: payload?.apiKey ?? '' });
      yield put({
        type: 'setAiWithWhite',
        payload: hasAiAccess,
      });
    },
    *getAiSystemConfig(_, { put }) {
      const res = (yield configService.getAiSystemConfig({})) as IAiConfig;
      yield put({
        type: 'setAiConfig',
        payload: res,
      });
      if (res?.aiSqlSource === AIType.CHAT2DBAI) {
        // yield put({
        //   type: 'fetchRemainingUse',
        //   payload: { apiKey: res.apiKey },
        // });
        yield put({
          type: 'updateAiWithWhite',
          payload: { apiKey: res.apiKey },
        });
      }
    },
    // *setAiSystemConfig({ payload }: { type: string; payload: { aiConfig: IAiConfig } }, { put }: EffectsCommandMap) {
    *setAiSystemConfig({ payload }: { type: any; payload?: IAiConfig }, { put }: EffectsCommandMap) {
      const aiConfig = payload;
      try {
        (yield configService.setAiSystemConfig(aiConfig!)) as void;
        message.success(i18n('common.text.submittedSuccessfully'));
        yield put({
          type: 'setAiConfig',
          payload: aiConfig,
        });

        // yield put({
        //   type: 'fetchRemainingUse',
        //   payload: { apiKey: aiConfig?.apiKey },
        // });

        if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
          yield put({
            type: 'updateAiWithWhite',
            payload: { apiKey: aiConfig?.apiKey },
          });
        } else {
          yield put({
            type: 'setAiWithWhite',
            payload: false,
          });
        }
      } catch (error) {}
    },

    *fetchRemainingUse({ payload }: { type: any; payload?: { apiKey?: string } }, { put, select }) {
      const currentState = (yield select((state: any) => state.ai)) as IAIState;
      const { apiKey } = payload || {};
      try {
        if (!apiKey || currentState.aiConfig.aiSqlSource !== AIType.CHAT2DBAI) {
          yield put({
            type: 'setRemainUse',
            payload: undefined,
          });
          return;
        }
        const res = (yield aiService.getRemainingUse()) as IRemainingUse;
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
