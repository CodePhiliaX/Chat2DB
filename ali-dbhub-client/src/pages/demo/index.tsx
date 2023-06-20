import React, { memo, useCallback, useEffect, useRef, useState } from 'react';
import styles from './index.less'
import Iconfont from '@/components/Iconfont'
import Tabs from '@/components/Tabs';
import miscService from '@/service/misc'

function DemoPage() {
  function miscServicefn() {
    miscService.systemStop().then(res => {
      alert('kill service');
    });
  }

  return <button onClick={miscServicefn}>关机</button>
}

export default DemoPage