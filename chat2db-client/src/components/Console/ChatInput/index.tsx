import React, { ChangeEvent, useEffect, useState } from 'react';
import styles from './index.less';
import AIImg from '@/assets/img/ai.svg';
import { Button, Checkbox, Dropdown, Input, Modal, Popover, Select, Spin } from 'antd';
import i18n from '@/i18n/';
import Iconfont from '@/components/Iconfont';
import { WarningOutlined } from '@ant-design/icons';
import { AiSqlSourceType, IRemainingUse } from '@/typings/ai';
import { WECHAT_MP_URL } from '@/constants/social';

interface IProps {
  value?: string;
  result?: string;
  tables?: string[];
  selectedTables?: string[];
  remainingUse?: IRemainingUse;
  aiType: AiSqlSourceType;
  remainingBtnLoading: boolean;
  disabled?: boolean;
  onPressEnter: (value: string) => void;
  onSelectTables?: (tables: string[]) => void;
  onClickRemainBtn: Function;
}

function ChatInput(props: IProps) {
  const [value, setValue] = useState(props.value);

  const onPressEnter = (e: any) => {
    if (!e.target.value) {
      return;
    }
    if (e.nativeEvent.isComposing && e.key === 'Enter') {
      e.preventDefault();
      return;
    }
    props.onPressEnter && props.onPressEnter(e.target.value);
  };

  const renderSelectTable = () => {
    const { tables, selectedTables, onSelectTables } = props;
    const options = (tables || []).map((t) => ({ value: t, label: t }));
    return (
      <div className={styles.aiSelectedTable}>
        <span className={styles.aiSelectedTableTips}>
          {/* <WarningOutlined style={{color: 'yellow'}}/> */}
          {i18n('chat.input.remain.tooltip')}
        </span>
        <Select
          showSearch
          mode="multiple"
          allowClear
          options={options}
          placeholder={i18n('chat.input.tableSelect.placeholder')}
          value={selectedTables}
          onChange={(v) => {
            onSelectTables && onSelectTables(v);
          }}
        />
      </div>
    );
  };

  const renderSuffix = () => {
    const remainCnt = props?.remainingUse?.remainingUses ?? '-';
    return (
      <div className={styles.suffixBlock}>
        <Button
          type="primary"
          className={styles.enter}
          onClick={() => {
            if (value) {
              props.onPressEnter && props.onPressEnter(value);
            }
          }}
        >
          <Iconfont code="&#xe643;" className={styles.enterIcon} />
        </Button>
        <div className={styles.tableSelectBlock}>
          <Popover content={renderSelectTable()} placement="bottom">
            <Iconfont code="&#xe618;" />
          </Popover>
        </div>
        {props.aiType === AiSqlSourceType.CHAT2DBAI && (
          <Spin spinning={!!props.remainingBtnLoading} size="small">
            <div
              className={styles.remainBlock}
              onClick={() => {
                // props.onClickRemainBtn && props.onClickRemainBtn();
              }}
            >
              {i18n('chat.input.remain', remainCnt)}
            </div>
          </Spin>
        )}
      </div>
    );
  };

  return (
    <div className={styles.chatWrapper}>
      <img className={styles.chatAi} src={AIImg} />
      <Input
        disabled={props.disabled}
        value={value}
        onChange={(e) => setValue(e.target.value)}
        bordered={false}
        placeholder={i18n('workspace.ai.input.placeholder')}
        onPressEnter={onPressEnter}
        suffix={renderSuffix()}
      />
    </div>
  );
}

export default ChatInput;
