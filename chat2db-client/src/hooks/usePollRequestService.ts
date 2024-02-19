import { useState, useEffect, useRef } from 'react';
import { useGlobalStore } from '@/store/global';

interface IProps {
  /** Maximum number of requests */
  maxAttempts?: number;
  /** Request interval ms */
  interval?: number;
  /** demand service */
  loopService: (...rest) => Promise<boolean>;
}

export enum ServiceStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE',
}

/**
 * Polling request back-end service
 */
const usePollRequestService = ({ maxAttempts = 200, interval = 200, loopService }: IProps) => {
  const [serviceStatus, setServiceStatus] = useState<ServiceStatus>(ServiceStatus.PENDING);
  const [restart, setRestart] = useState(false);
  const attempts = useRef(0);
  const startupDate = useRef(new Date().getTime());

  const serviceFn = async () => {
    // The first request fails. Start the service
    if (attempts.current === 1 && ServiceStatus.SUCCESS !== serviceStatus) {
      const child =  window.electronApi?.startServerForSpawn();

      useGlobalStore.getState().setJavaServer(child);

      child?.stdout?.on('data', (buffer) => {
        const data = JSON.parse(buffer.toString('utf8'));
        if (data.status === 'success'){
          useGlobalStore.getState().commandLineRequestList[data.id]?.resolve(data);
        }else {
          useGlobalStore.getState().commandLineRequestList[data.id]?.reject(data);
        }
      });

      // child?.stderr?.on('data', (data) => {
      //   console.error(`stderr: ${data}`);
      // });

      // child?.on('close', (code) => {
      //   console.log(`child process exited with code ${code}`);
      // });
    }
    if (attempts.current >= maxAttempts) {
      setServiceStatus(ServiceStatus.FAILURE);
      return;
    }
    attempts.current = attempts.current + 1;
    loopService().then((res) => {
      if (res) {
        const now = new Date().getTime();
        setTimeout(() => {
          setServiceStatus(ServiceStatus.SUCCESS);
        }, startupDate.current + 1000 - now);
      }
    })
    .catch(() => {
      setTimeout(serviceFn, interval);
    });
   
  };

  useEffect(() => {
    serviceFn();
  }, [maxAttempts, interval, restart]);

  // Newly added reset function
  const restartPolling = () => {
    setServiceStatus(ServiceStatus.PENDING);
    attempts.current = 0;
    setRestart(!restart);
  };

  return { serviceStatus, restartPolling };
};

export default usePollRequestService;
