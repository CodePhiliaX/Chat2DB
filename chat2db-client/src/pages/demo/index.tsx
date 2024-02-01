import React from 'react';
import { useGlobalStore } from '@/store/global';
import Box from './index1';

function Test() {
  const [bigCat, smallCat, setSmallCat, setBigCat] = useGlobalStore((s) => [
    s.bigCat,
    s.smallCat,
    s.setSmallCat,
    s.setBigCat,
  ]);

  // 创建一个随机数

  return (
    <>
      <div>
        <p>bigCat:{bigCat}</p>
        <p>small:{smallCat}</p>
        <p>随机数：{Math.random()}</p>
      </div>
      <Box />
      <button
        onClick={() => {
          setSmallCat(smallCat + 1);
        }}
      >
        add small
      </button>
      <button
        onClick={() => {
          setBigCat(bigCat + 1);
        }}
      >
        add big
      </button>
    </>
  );
}

export default Test;
