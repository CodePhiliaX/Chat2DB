import React, { ChangeEvent, useEffect, useState } from 'react';
import styles from './index.less';
import AIImg from '@/assets/img/ai.svg';
import { Input } from 'antd';
import i18n from '@/i18n';

interface IProps {
  value?: string;
  result?: string;
  onPressEnter: (value: string) => void;
}

function ChatInput(props: IProps) {
  const onPressEnter = (e: any) => {
    console.log('press enter', e.target.value);
    if (e.target.value) {
      props.onPressEnter && props.onPressEnter(e.target.value);
    }
  };

  return (
    <div className={styles.chat_wrapper}>
      <img className={styles.chat_ai} src={AIImg} />
      <Input
        defaultValue={props.value}
        bordered={false}
        placeholder={i18n('workspace.ai.input.placeholder')}
        onPressEnter={onPressEnter}
      />
    </div>
  );
}

export default ChatInput;
