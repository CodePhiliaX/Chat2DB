import aiService from '@/service/ai';
import { AiSqlSourceType, IRemainingUse } from '@/typings/ai';
import { Effect, Reducer } from 'umi';

export interface IAIState {
  keyAndAiType: {
    key: string;
    aiType: AiSqlSourceType;
  };
  remainingUse?: IRemainingUse;
}

export interface IAIModelType {
  namespace: 'ai';
  state: IAIState;
  reducers: {
    setRemainUse: Reducer<IAIState>;
    setKeyAndAiType: Reducer<IAIState>;
  };
  effects: {
    fetchRemainingUse: Effect;
  };
}

const AIModel: IAIModelType = {
  namespace: 'ai',
  state: {
    remainingUse: undefined,
    keyAndAiType: {
      key: '',
      aiType: AiSqlSourceType.CHAT2DB,
    },
  },
  reducers: {
    setKeyAndAiType(state, { payload }) {
      return {
        ...state,
        keyAndAiType: payload,
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
    *fetchRemainingUse({ payload }, { put }) {
      const res = (yield aiService.getRemainingUse(payload)) as IRemainingUse;
      yield put({
        type: 'setRemainUse',
        payload: res,
      });
    },
  },
};

export default AIModel;
