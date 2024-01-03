import React, { useEffect } from 'react';

function Test() {
  const token = '1'
  useEffect(() => {
    const socket = new WebSocket(`ws://127.0.0.1:10821/api/ws/1`);
    socket.onopen = () => {
      console.log('open');
      socket.send('hello');
    };
  }, []);
  return (false);
}

export default Test;
