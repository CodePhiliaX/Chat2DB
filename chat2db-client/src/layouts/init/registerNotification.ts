import { notification } from 'antd';

export default () => {
  notification.config({
    placement: 'topRight',
    maxCount: 2,
    duration: 3,
  });
};
