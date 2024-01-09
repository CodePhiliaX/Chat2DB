import { useState, useEffect, useRef } from 'react';

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

let intervalId: NodeJS.Timeout;

/**
 * 轮询请求后端服务
 */
const usePollRequestService = ({ maxAttempts = 200, interval = 200, loopService }: IProps) => {
  const [serviceStatus, setServiceStatus] = useState<ServiceStatus>(ServiceStatus.PENDING);
  const [restart, setRestart] = useState(false);
  const attempts = useRef(0);

  const serviceFn = async () => {
    // 第一次请求失败，启动服务
    if (attempts.current === 1 && ServiceStatus.SUCCESS !== serviceStatus) {
      window.electronApi?.startServerForSpawn();
    }
    if (attempts.current >= maxAttempts) {
      setServiceStatus(ServiceStatus.FAILURE);
      clearInterval(intervalId);
      return;
    }
    try {
      attempts.current = attempts.current + 1;
      await loopService();
      setServiceStatus(ServiceStatus.SUCCESS);
      clearInterval(intervalId);
    } catch (error) {
      // setAttempts(attempts + 1);
    }
  };

  useEffect(() => {
    serviceFn();
    if (serviceStatus !== ServiceStatus.SUCCESS) {
      intervalId = setInterval(serviceFn, interval);
    }
    return () => clearInterval(intervalId);
  }, [maxAttempts, interval, restart]);

  // 新增加的重置函数
  const restartPolling = () => {
    setServiceStatus(ServiceStatus.PENDING);
    attempts.current = 0;
    setRestart(!restart);
  };

  return { serviceStatus, restartPolling };
};

export default usePollRequestService;
