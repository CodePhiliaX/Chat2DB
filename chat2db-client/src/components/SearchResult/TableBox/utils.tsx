// 在input中把USER_FILLED_VALUE转换为null
import { USER_FILLED_VALUE } from './index';
export const transformInputValue = (value: string) => {
  if (value === USER_FILLED_VALUE.DEFAULT) {
    return null;
  }
  return value;
};
