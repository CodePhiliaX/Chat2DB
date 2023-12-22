import React, { useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
// import i18n from '@/i18n';
import { Popover } from 'antd';
import Iconfont from '@/components/Iconfont';
import {extendConfig} from '../config';

// import { useWorkspaceStore } from '@/pages/main/workspace/store';

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
  const [activeExtend, setActiveExtend] = useState<IToolbar | null>(null);

  const changeExtend = (item: IToolbar) => {
    if (activeExtend?.code === item.code) {
      setActiveExtend(null);
      return;
    }
    setActiveExtend(item);
  };

  return (
    <div className={classnames(className,styles.workspaceExtendNav)}>
      {extendConfig.map((item, index) => {
        return (
          <Popover mouseEnterDelay={0.8} key={index} placement="left" content={item.title}>
            <div className={styles.rightBarFront} onClick={changeExtend.bind(null, item)}>
              <Iconfont code={item.icon} box size={18} active={activeExtend?.code === item.code} />
            </div>
          </Popover>
        );
      })}
    </div>
  );

};
