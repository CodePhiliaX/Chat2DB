import React, { memo, useMemo, useState } from 'react';
import i18n from '@/i18n';
import styles from './index.less';
import { Input } from 'antd';

// ----- constants -----
import { DatabaseTypeCode } from '@/constants';

// ----- components -----
import Iconfont from '@/components/Iconfont';

// ----- store -----
import { useWorkspaceStore } from '@/pages/main/workspace/store';

interface IProps {
  searchValue: string;
  setSearchValue: (value: string) => void;
  getTreeData: (refresh?: boolean) => void;
}

// 不支持创建数据库的数据库类型
const notSupportCreateDatabaseType = [DatabaseTypeCode.H2];

// 不支持创建schema的数据库类型
const notSupportCreateSchemaType = [DatabaseTypeCode.ORACLE];

const OperationLine = (props: IProps) => {
  const [searchIng, setSearchIng] = useState<boolean>(false);
  const { searchValue, setSearchValue, getTreeData } = props;
  const { currentConnectionDetails, openCreateDatabaseModal } = useWorkspaceStore((state) => {
    return {
      currentConnectionDetails: state.currentConnectionDetails,
      openCreateDatabaseModal: state.openCreateDatabaseModal,
    };
  });

  const handelOpenCreateDatabaseModal = () => {
    const type = currentConnectionDetails?.supportDatabase ? 'database' : 'schema';

    openCreateDatabaseModal?.({
      type,
      relyOnParams: {
        databaseType: currentConnectionDetails!.type!,
        dataSourceId: currentConnectionDetails!.id!,
      },
      executedCallback: () => {
        getTreeData(true);
      },
    });
  };

  const showCreate = useMemo(() => {
    if (currentConnectionDetails?.supportDatabase) {
      return !notSupportCreateDatabaseType.includes(currentConnectionDetails!.type!);
    }
    if (currentConnectionDetails?.supportSchema) {
      return !notSupportCreateSchemaType.includes(currentConnectionDetails!.type!);
    }
  }, [currentConnectionDetails]);

  return (
    <>
      <div className={styles.operationLine}>
        <div className={styles.operationLineLeft}>
          {showCreate && (
            <Iconfont onClick={handelOpenCreateDatabaseModal} code="&#xeb78;" box boxSize={20} size={15} />
          )}
          <Iconfont
            onClick={() => {
              getTreeData(true);
            }}
            code="&#xe668;"
            box
            boxSize={20}
            size={14}
          />
          {searchIng ? (
            <Iconfont
              onClick={() => {
                setSearchIng(false);
                setSearchValue('');
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
              code="&#xe888;"
              box
              boxSize={20}
              size={14}
            />
          )}
        </div>
        {/* <div>1</div> */}
      </div>
      {searchIng && (
        <div className={styles.searchBox}>
          <Input
            size="small"
            prefix={<Iconfont code="&#xe888;" />}
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            allowClear
            placeholder={i18n('workspace.tree.search.placeholder')}
          />
        </div>
      )}
    </>
  );
};

export default memo(OperationLine);
