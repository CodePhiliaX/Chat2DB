import React, { useEffect } from 'react';
import OpenScreenAnimation from '@/components/OpenScreenAnimation';

function Test() {
  // const token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpblR5cGUiOiJsb2dpbiIsImxvZ2luSWQiOjIsImRldmljZSI6ImRlZmF1bHQtZGV2aWNlIiwiZWZmIjoxNzA2ODU0NTMwMDI3LCJyblN0ciI6Ik1RcHRPOUVBVlJlbGRQa1RFN01MZUpLeG5KTGVwRFpaIn0.knOw08E6mwWF_GpkeQ8KflQlfQuNu4jd-_Bgh7EnCj4'
  // useEffect(() => {
  //   const socket = new WebSocket(`ws://127.0.0.1:10821/api/ws/${token}`);
  //   socket.onopen = () => {
  //     console.log('open');
  //     socket.send('hello');
  //   };
  // }, []);
  return <OpenScreenAnimation />;
}

export default Test;
