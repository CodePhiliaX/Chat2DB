import React, { useState } from 'react';
import styles from './index.less';
// import classnames from 'classnames';
import { Popover } from 'antd';
import Iconfont from '@/components/Iconfont';
import Output from '@/components/Output';
import SaveList from '../SaveList';
import { useWorkspaceStore } from '@/pages/main/workspace/store';
import i18n from '@/i18n';

interface IToolbar {
  code: string;
  title: string;
  icon: string;
  components: any;
}

export const useWorkspaceExtend = () => {
  const [activeExtend, setActiveExtend] = useState<IToolbar | null>(null);
  const { panelRight } = useWorkspaceStore((state) => state.layout);

  const toolbarConfig: IToolbar[] = [
    // {
    //   code: 'ai',
    //   title: 'AI',
    //   icon: '\ue8ad',
    //   components: <div>ai</div>,
    // },
    {
      code: 'executiveLog',
      title: i18n('common.title.executiveLogging'),
      icon: '\ue8ad',
      components: <Output />,
    },
    {
      code: 'saveList',
      title: i18n('workspace.title.savedConsole'),
      icon: '\ue619',
      components: <SaveList />,
    },
  ];

  const changeExtend = (item: IToolbar) => {
    if (activeExtend?.code === item.code) {
      setActiveExtend(null);
      return;
    }
    setActiveExtend(item);
  };

  // return (
  //   <div
  //     className={classnames(styles.workspaceExtend, className, {
  //       [styles.workspaceExtendActive]: panelRight,
  //     })}
  //   >
  //   </div>
  // );

  const extendBody = (
    <>{activeExtend && <div className={styles.workspaceExtendMain}>{activeExtend?.components}</div>}</>
  );
  const extendNav = (
    <div className={styles.workspaceExtendBar}>
      {toolbarConfig.map((item, index) => {
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

  return [extendBody, extendNav];
};
