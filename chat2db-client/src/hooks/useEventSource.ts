import React, { useEffect, useMemo, useState } from 'react';
import { uuid } from '@/utils/common';
import { EventSourcePolyfill } from 'event-source-polyfill';

function useEventSource({ url }) {
  const uid = useMemo(() => uuid(), []);
  const [messages, setMessage] = useState('');

  useEffect(() => {
    handleEventSource();
  }, []);

  const handleEventSource = () => {
    const eventSource = new EventSourcePolyfill(url, {
      headers: {
        uid,
      },
    });

    eventSource.onmessage = (event) => {
      setMessage(event.data);
      const isEOF = event.data === '[DONE]';
      if (isEOF) {
        eventSource.close();
      }
    };

    eventSource.onerror = (error) => {
      console.error('EventSourcePolyfill error:', error);
    };
  };

  return messages;
}

export default useEventSource;
