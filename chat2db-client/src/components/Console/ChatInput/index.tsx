import React, { ChangeEvent, useEffect, useState } from 'react';
import styles from './index.less';
import AIImg from '@/assets/img/ai.svg';
import { Input } from 'antd';
import i18n from '@/i18n/';

interface IProps {
  value?: string;
  result?: string;
  onPressEnter: (value: string) => void;
}

function ChatInput(props: IProps) {
  const onPressEnter = (e: any) => {
    if (e.target.value) {
      props.onPressEnter && props.onPressEnter(e.target.value);
    }
  };

  const renderSuffix = () => {
    const remainCnt = 10;
    return (
      <div className={styles.suffixBlock}>
        <div className={styles.remainBlock}>
          {i18n('chat.input.remain', remainCnt)}
        </div>
      </div>
    );
  };

  return (
    <div className={styles.chat_wrapper}>
      <img className={styles.chat_ai} src={AIImg} />
      <Input
        defaultValue={props.value}
        bordered={false}
        placeholder={i18n('workspace.ai.input.placeholder')}
        onPressEnter={onPressEnter}
        suffix={renderSuffix()}
      />
    </div>
  );
}

export default ChatInput;
