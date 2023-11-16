import React, { memo, useState } from 'react';
import i18n from '@/i18n';
import styles from './index.less';
import { Input } from 'antd';

// ----- constants -----
import { DatabaseTypeCode } from '@/constants';

// ----- components -----
import Iconfont from '@/components/Iconfont';
import CreateDatabase, { ICreateDatabaseRef } from '@/components/CreateDatabase';

// ----- store -----
import { useWorkspaceStore } from '@/store/workspace';

interface IProps {
  searchValue: string;
  setSearchValue: (value: string) => void;
  getTreeData: (refresh?: boolean) => void;
}

// 不支持创建数据库的数据库类型
const notSupportCreateDatabaseType = [DatabaseTypeCode.H2];

// 不支持创建schema的数据库类型
const notSupportCreateSchemaType = [DatabaseTypeCode.ORACLE];

const operationLine = (props: IProps) => {
  const [searchIng, setSearchIng] = useState<boolean>(false);
  const { searchValue, setSearchValue, getTreeData } = props;
  const createDatabaseRef = React.useRef<ICreateDatabaseRef>(null);
  const { currentConnectionDetails } = useWorkspaceStore((state) => {
    return {
      currentConnectionDetails: state.currentConnectionDetails,
    };
  });

  return (
    <>
      <div className={styles.operationLine}>
        <div className={styles.operationLineLeft}>
          {!notSupportCreateDatabaseType.includes(currentConnectionDetails?.type) && (
            <Iconfont
              onClick={() => {
                createDatabaseRef.current?.setOpen(true, 'database');
              }}
              code="&#xe631;"
              box
              boxSize={20}
              size={17}
            />
          )}
          <Iconfont
            onClick={() => {
              getTreeData(true);
            }}
            code="&#xe635;"
            box
            boxSize={20}
            size={13}
          />
          {searchIng ? (
            <Iconfont
              onClick={() => {
                setSearchIng(false);
              }}
              box
              boxSize={20}
              code="&#xe634;"
            />
          ) : (
            <Iconfont
              onClick={() => {
                setSearchIng(true);
              }}
              code="&#xe74a;"
              box
              boxSize={20}
              size={14}
            />
          )}
        </div>
        <div>1</div>
      </div>
      {searchIng && (
        <div className={styles.searchBox}>
          <Input
            size="small"
            prefix={<Iconfont code="&#xe74a;" />}
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            allowClear
            placeholder={i18n('workspace.tree.search.placeholder')}
          />
        </div>
      )}
      <CreateDatabase
        executedCallback={() => {
          getTreeData(true);
        }}
        curWorkspaceParams={{
          dataSourceId: currentConnectionDetails?.id,
          dataSourceName: currentConnectionDetails?.alias,
        }}
        ref={createDatabaseRef}
      />
    </>
  );
};

export default memo(operationLine);
