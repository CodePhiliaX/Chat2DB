import { message } from 'antd';

export default () => {
  message.config({
    maxCount: 1,
    duration: 3,
  });
};
