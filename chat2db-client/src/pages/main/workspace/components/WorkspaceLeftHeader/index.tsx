import React, { memo, useMemo } from 'react';
import { Dropdown } from 'antd';
import classnames from 'classnames';
import styles from './index.less';

// ---- store ----
import { useConnectionStore } from '@/pages/main/store/connection';
import { useWorkspaceStore } from '@/pages/main/workspace/store';
import { setCurrentConnectionDetails } from '@/pages/main/workspace/store/common';

// ----- components -----
import Iconfont from '@/components/Iconfont';

// ----- constants/typings -----
import { databaseMap } from '@/constants';

import { IConnectionListItem } from '@/typings/connection';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const { connectionList } = useConnectionStore((state) => {
    return {
      connectionList: state.connectionList,
    };
  });

  const { currentConnectionDetails } = useWorkspaceStore((state) => {
    return {
      currentConnectionDetails: state.currentConnectionDetails,
    };
  });

  const renderConnectionLabel = (item: IConnectionListItem) => {
    return (
      <div className={classnames(styles.menuLabel)}>
        {/* <Tag className={styles.menuLabelTag} color={item.environment.color.toLocaleLowerCase()}>
          {item.environment.shortName}
        </Tag> */}
        <span className={styles.envTag} style={{ background: item.environment.color.toLocaleLowerCase() }} />
        <div className={styles.menuLabelIconBox}>
          <Iconfont className={classnames(styles.menuLabelIcon)} code={databaseMap[item.type]?.icon} />
        </div>
        <div className={styles.menuLabelTitle}>{item.alias}</div>
      </div>
    );
  };

  const connectionItems = useMemo(() => {
    return (
      connectionList?.map((item) => {
        return {
          key: item.id,
          label: renderConnectionLabel(item),
          onClick: () => {
            setCurrentConnectionDetails(item);
          },
        };
      }) || []
    );
  }, [connectionList, currentConnectionDetails]);

  return (
    <div className={classnames(styles.workspaceLeftHeader, className)}>
      <Dropdown menu={{ items: connectionItems }} trigger={['click']} overlayClassName={styles.dropdownOverlay}>
        <div className={styles.selectConnection}>
          {currentConnectionDetails && renderConnectionLabel(currentConnectionDetails)}
          <div className={styles.dropDownArrow}>
            <Iconfont code="&#xe641;" />
          </div>
        </div>
      </Dropdown>
    </div>
  );
});
