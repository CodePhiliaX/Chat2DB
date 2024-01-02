import React from 'react';
// import styles from './index.less';
import Fingerprint2 from 'fingerprintjs2';

function Test() {
  Fingerprint2.get((com)=>{
    console.log(com);
  })
  return (false);
}

export default Test;
