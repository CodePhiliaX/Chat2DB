import React from 'react';
import styles from './index.less';
import classnames from 'classnames';
// import i18n from '@/i18n';
import { Popover } from 'antd';
import Iconfont from '@/components/Iconfont';
import {extendConfig} from '../config';

import { setCurrentWorkspaceExtend } from '@/pages/main/workspace/store/common';
import { useWorkspaceStore } from '@/pages/main/workspace/store';

interface IToolbar {
  code: string;
  title: string;
  icon: string;
  components: any;
}

interface IProps {
  className?: any;
}

export default (props:IProps) => {
  const { className } = props;
  const { currentWorkspaceExtend } = useWorkspaceStore((state) => {
    return {
      currentWorkspaceExtend: state.currentWorkspaceExtend,
    };
  });

  const changeExtend = (item: IToolbar) => {
    if (currentWorkspaceExtend === item.code) {
      setCurrentWorkspaceExtend(null);
      return;
    }
    setCurrentWorkspaceExtend(item.code);
  };

  return (
    <div className={classnames(className,styles.workspaceExtendNav)}>
      {extendConfig.map((item, index) => {
        return (
          <Popover mouseEnterDelay={0.8} key={index} placement="left" content={item.title}>
            <div className={styles.rightBarFront} onClick={changeExtend.bind(null, item)}>
              <Iconfont code={item.icon} box size={18} active={currentWorkspaceExtend === item.code} />
            </div>
          </Popover>
        );
      })}
    </div>
  );

};
