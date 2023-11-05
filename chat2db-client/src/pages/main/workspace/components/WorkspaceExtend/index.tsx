import React, { memo, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Popover } from 'antd';
import Iconfont from '@/components/Iconfont';
import Output from '@/components/Output';
import { useWorkspaceStore } from '@/store/workspace';
import i18n from '@/i18n';

interface IProps {
  className?: string;
  curWorkspaceParams: any;
}

interface IToolbar {
  code: string;
  title: string;
  icon: string;
  components: any;
}

export default memo<IProps>((props) => {
  const { className } = props;
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
      components: <Output curWorkspaceParams={props.curWorkspaceParams} />,
    },
  ];

  const changeExtend = (item: IToolbar) => {
    if (activeExtend?.code === item.code) {
      setActiveExtend(null);
      return;
    }
    setActiveExtend(item);
  };

  return (
    <div
      className={classnames(styles.workspaceExtend, className, {
        [styles.workspaceExtendActive]: panelRight,
      })}
    >
      {activeExtend && <div className={styles.workspaceExtendMain}>{activeExtend?.components}</div>}
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
    </div>
  );
});
