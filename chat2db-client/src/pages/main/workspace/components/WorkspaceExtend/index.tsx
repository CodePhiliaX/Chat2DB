import React, { memo, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Popover } from 'antd';
import Iconfont from '@/components/Iconfont';
import Output from '@/components/Output';

interface IProps {
  className?: string;
}

const data = [
  {
    code: 'ai',
    title: 'AI',
    icon: '\ue8ad',
    components: <div>ai</div>,
  },
  {
    code: 'executiveLog',
    title: '执行记录',
    icon: '\ue8ad',
    components: <Output />,
  },
];

export default memo<IProps>((props) => {
  const { className } = props;
  const [activeExtend, setActiveExtend] = useState<any>(null);

  const changeExtend = (item: any) => {
    if (activeExtend) {
      setActiveExtend(null);
      return;
    }
    setActiveExtend(item);
  };

  return (
    <div className={classnames(styles.workspaceExtend, className)}>
      {activeExtend && <div className={styles.workspaceExtendMain}>{activeExtend?.components}</div>}
      <div className={styles.workspaceExtendBar}>
        {data.map((item, index) => {
          return (
            <Popover key={index} placement="left" content={item.title}>
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
