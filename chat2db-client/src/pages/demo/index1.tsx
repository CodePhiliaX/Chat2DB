import React,{memo} from 'react';
import { useGlobalStore } from '@/store/global';

const Test =  memo(()  =>{
  const [smallCat] = useGlobalStore((s) => [
    s.smallCat,
  ]);
  // 创建一个随机数

  return (
    <>
      <div style={{color:'red'}}>
        <p>small:{smallCat}</p>
        <p>随机数{Math.random()}</p>
      </div>
    </>
  );
})

export default Test;
