import { useState, useEffect, useCallback } from 'react';

const useClickAndDoubleClick = (singleClickCallback, doubleClickCallback, delay = 250) => {
  const [clickCount, setClickCount] = useState(0);
  const [eventData, setEventData] = useState(null);

  const handleClick = useCallback((data) => {
    setEventData(data);
    setClickCount((prev) => prev + 1);
  }, []);

  useEffect(() => {
    if (clickCount === 1) {
      const singleClickTimer = setTimeout(() => {
        singleClickCallback(eventData);
        setClickCount(0);
      }, delay);
      return () => clearTimeout(singleClickTimer);
    } else if (clickCount === 2) {
      doubleClickCallback(eventData);
      setClickCount(0);
    }
  }, [clickCount, eventData, singleClickCallback, doubleClickCallback, delay]);

  return handleClick;
};

export default useClickAndDoubleClick;
