import React from 'react';
import style from './index.less'

interface IProps{
  className: string;
}

function LayoutBasic(props: IProps) {
  return <div className={style.layoutBasic}>
    <div></div>
  </div>;
}

export default LayoutBasic;
