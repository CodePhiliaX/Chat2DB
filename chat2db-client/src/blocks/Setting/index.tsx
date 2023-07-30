import React, { useEffect, useMemo, useState } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import { Modal } from 'antd';
import i18n from '@/i18n';
import BaseSetting from './BaseSetting';
import AISetting from './AiSetting';
import ProxySetting from './ProxySetting';
import About from './About';
import { connect } from 'umi';
import { IAIState } from '@/models/ai';
import TestVersion from '@/components/TestVersion';
import { IAiConfig } from '@/typings';

import styles from './index.less';

interface IProps {
  aiConfig: IAiConfig;
  className?: string;
  text?: string;
  dispatch: Function;
}

function Setting(props: IProps) {
  const { className, text, dispatch } = props;
  const [isModalVisible, setIsModalVisible] = useState(false);

  const [currentMenu, setCurrentMenu] = useState(0);

  useEffect(() => {
    if (isModalVisible) {
      getAiSystemConfig();
    }
  }, [isModalVisible]);

  useEffect(() => {
    getAiSystemConfig();
  }, []);

  const getAiSystemConfig = () => {
    /** 获取ai相关配置 */
    dispatch({
      type: 'ai/getAiSystemConfig',
    });
  };

  const showModal = () => {
    setIsModalVisible(true);
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  function changeMenu(t: any) {
    setCurrentMenu(t);
  }

  const handleApplyAiConfig = (aiConfig: IAiConfig) => {
    dispatch({
      type: 'ai/setAiSystemConfig',
      payload: aiConfig,
    });
  };

  const menusList = [
    {
      label: i18n('setting.nav.basic'),
      icon: '\ue795',
      body: <BaseSetting />,
    },
    {
      label: i18n('setting.nav.customAi'),
      icon: '\ue646',
      body: <AISetting aiConfig={props.aiConfig} handleApplyAiConfig={handleApplyAiConfig} />,
    },
    {
      label: i18n('setting.nav.proxy'),
      icon: '\ue63f',
      body: <ProxySetting />,
    },
    {
      label: i18n('setting.nav.aboutUs'),
      icon: '\ue60c',
      body: <About />,
    },
  ];

  return (
    <>
      <div className={classnames(className, styles.box)} onClick={showModal}>
        {text ? (
          <span className={styles.setText}>{text}</span>
        ) : (
          <Iconfont className={styles.settingIcon} code="&#xe630;"></Iconfont>
        )}
      </div>
      <TestVersion />
      <Modal
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        footer={false}
        width={800}
        maskClosable={false}
      >
        <div className={styles.modalBox}>
          <div className={styles.menus}>
            <div className={classnames(styles.menusTitle)}>{i18n('setting.title.setting')}</div>
            {menusList.map((t, index) => {
              return (
                <div
                  key={index}
                  onClick={changeMenu.bind(null, index)}
                  className={classnames(styles.menuItem, {
                    [styles.activeMenu]: t.label === menusList[currentMenu].label,
                  })}
                >
                  <Iconfont code={t.icon} />
                  {t.label}
                </div>
              );
            })}
          </div>
          <div className={styles.menuContent}>
            <div className={classnames(styles.menuContentTitle)}>{menusList[currentMenu].label}</div>
            {menusList[currentMenu].body}
          </div>
        </div>
      </Modal>
    </>
  );
}

export default connect(({ ai }: { ai: IAIState }) => ({
  aiConfig: ai.aiConfig,
}))(Setting);
