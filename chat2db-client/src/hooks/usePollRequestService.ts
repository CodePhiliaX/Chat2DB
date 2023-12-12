import { useState, useEffect } from 'react';

interface IProps {
  /** 最大请求次数 */
  maxAttempts?: number;
  /** 请求间隔时间ms */
  interval?: number;
  /** 请求服务 */
  loopService: (...rest) => Promise<boolean>;
}

export enum ServiceStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE',
}

/**
 * 轮询请求后端服务
 */
const usePollRequestService = ({ maxAttempts = 200, interval = 200, loopService }: IProps) => {
  const [serviceStatus, setServiceStatus] = useState<ServiceStatus>(ServiceStatus.PENDING);
  const [attempts, setAttempts] = useState(0);
  const [restart, setRestart] = useState(false);

  useEffect(() => {
    let intervalId: NodeJS.Timeout;

    const serviceFn = async () => {
      if (attempts >= maxAttempts) {
        setServiceStatus(ServiceStatus.FAILURE);
        clearInterval(intervalId);
        return;
      }
      try {
        setAttempts(attempts + 1);
        await loopService();
        setServiceStatus(ServiceStatus.SUCCESS);
        clearInterval(intervalId);
      } catch (error) {
        // setAttempts(attempts + 1);
      }
    };

    serviceFn();

    if (serviceStatus !== ServiceStatus.SUCCESS) {
      intervalId = setInterval(serviceFn, interval);
    }

    return () => clearInterval(intervalId);
  }, [maxAttempts, interval, restart]);

  // 新增加的重置函数
  const restartPolling = () => {
    setServiceStatus(ServiceStatus.PENDING);
    setAttempts(0);
    setRestart(!restart);
  };

  return { serviceStatus, restartPolling };
};

export default usePollRequestService;
