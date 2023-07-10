import { notification } from 'antd';

export default () => {
  notification.config({
    placement: 'BottomRight',
    maxCount: 2,
    duration: 0,
  });
};
