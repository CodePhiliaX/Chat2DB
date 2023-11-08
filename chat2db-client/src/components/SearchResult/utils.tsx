import { USER_FILLED_VALUE } from './components/TableBox/index';

// 在input中把USER_FILLED_VALUE转换为null
export const transformInputValue = (value: string) => {
  if (value === USER_FILLED_VALUE.DEFAULT) {
    return null;
  }
  return value;
};
