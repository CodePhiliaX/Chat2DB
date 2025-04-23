import React, { memo, useRef, useState, createContext, useEffect } from 'react';
import { Button, Modal, message } from 'antd';
import i18n from '@/i18n';
import lodash from 'lodash';
import styles from './index.less';
import classnames from 'classnames';
import BaseInfo, { ISequenceInfoRef } from './BaseInfo';
import sqlService, { IModifySequenceSqlParams } from '@/service/sql';
import ExecuteSQL from '@/components/ExecuteSQL';
import { ISequenceInfo, IWorkspaceTab, IColumnTypes } from '@/typings';
import { DatabaseTypeCode, WorkspaceTabType } from '@/constants';
import LoadingContent from '@/components/Loading/LoadingContent';
import { useWorkspaceStore } from '@/pages/main/workspace/store';
interface IProps {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string | null;
  tableName?: string;
  databaseType: DatabaseTypeCode;
  changeTabDetails: (data: IWorkspaceTab) => void;
  tabDetails: IWorkspaceTab;
  submitCallback: () => void;
}

interface IContext extends IProps {
  sequenceDetails: ISequenceInfo;
  sequenceInfoRef: React.RefObject<ISequenceInfoRef>;
}

export const Context = createContext<IContext>({} as any);

interface IOption {
  label: string;
  value: string | number | null;
}

// 列字段类型，select组件的options需要的数据结构
interface IColumnTypesOption extends IColumnTypes {
  label: string;
  value: string | number | null;
}
export interface IDatabaseSupportField {
  columnTypes: IColumnTypesOption[];
  charsets: IOption[];
  collations: IOption[];
  indexTypes: IOption[];
  defaultValues: IOption[];
}

export default memo((props: IProps) => {
  const {
    databaseName,
    dataSourceId,
    tableName,
    schemaName,
    changeTabDetails,
    tabDetails,
    databaseType,
    submitCallback,
  } = props;
  const [sequenceDetails, setSequenceDetails] = useState<ISequenceInfo>({} as any);
  const [oldSequenceDetails, setOldSequenceDetails] = useState<ISequenceInfo>({} as any);
  const [viewSqlModal, setViewSqlModal] = useState<boolean>(false);
  const sequenceInfoRef = useRef<ISequenceInfoRef>(null);
  const [appendValue, setAppendValue] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const user = useWorkspaceStore.getState().currentConnectionDetails

  useEffect(() => {
    if (tableName) {
      getSequenceDetails();
    }else{
      const newSequenceDetails = { ...sequenceDetails, rolname: useWorkspaceStore.getState().currentConnectionDetails?.user ?? '' };
      setSequenceDetails(newSequenceDetails)
    }
    // getDatabaseFieldTypeList();
  }, []);

  const getSequenceDetails = (myParams?: { sequenceNameProps?: string }) => {
    const { sequenceNameProps } = myParams || {};
    const mySequenceName = sequenceNameProps || tableName;
    if (mySequenceName) {
      const params = {
        databaseName,
        dataSourceId,
        sequenceName: mySequenceName,
        schemaName,
        refresh: true,
      };
      setIsLoading(true);
      sqlService
        .getSequenceDetails(params)
        .then((res) => {
          const newSequenceDetails = lodash.cloneDeep(res);
          setSequenceDetails(newSequenceDetails || {});
          setOldSequenceDetails(res);
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  };

  function submit() {
    const sequenceInfo = sequenceInfoRef.current.getSequenceInfo();
    if (sequenceInfo.seqmin > sequenceInfo.seqstart) {
      message.error("最小值不能大于起始值"); // 或其他提示逻辑
      return;
    }
    if (sequenceInfoRef.current) {
      const newSequence = {
        ...oldSequenceDetails,
        ...sequenceInfoRef.current.getSequenceInfo(),
      };
      newSequence.nspname = props?.schemaName as String;
      const params: IModifySequenceSqlParams = {
        databaseName,
        dataSourceId,
        schemaName,
        refresh: true,
        newSequence,
      };

      if (tableName) {
        // params.tableName = tableName;
        params.oldSequence = oldSequenceDetails;
      }
      sqlService.getModifySequenceSql(params).then((res) => {
        setViewSqlModal(true);
        setAppendValue(res?.[0].sql);
      });
    }
  }

  const executeSuccessCallBack = () => {
    setViewSqlModal(false);
    message.success(i18n('common.text.successfulExecution'));
    const newTableName = sequenceInfoRef.current?.getSequenceInfo().nspname;
    // getTableDetails({ tableNameProps: newTableName });
    if (!tableName) {
      changeTabDetails({
        ...tabDetails,
        title: `${newTableName}`,
        type: WorkspaceTabType.EditSequence,
        uniqueData: {
          ...(tabDetails.uniqueData || {}),
          tableName: newTableName,
        },
      });
    }
    // 保存成功后，刷新左侧树
    submitCallback?.();
  };

  return (
    <Context.Provider
      value={{
        ...props,
        sequenceDetails,
        sequenceInfoRef,
        dataSourceId,
        databaseName,
        schemaName,
      }}
    >
      <LoadingContent coverLoading isLoading={isLoading} className={classnames(styles.box)}>
        <div className={styles.header}>
          <div className={styles.saveButton}>
            <Button type="primary" onClick={submit}>
              {i18n('common.button.save')}
            </Button>
          </div>
        </div>
        <div className={styles.main}>
          <BaseInfo ref={sequenceInfoRef} />
        </div>
      </LoadingContent>

      <Modal
        title={i18n('editSequence.title.sqlPreview')}
        open={!!viewSqlModal}
        onCancel={() => {
          setViewSqlModal(false);
        }}
        width="60vw"
        maskClosable={false}
        footer={false}
        destroyOnClose={true}
      >
        <ExecuteSQL
          initSql={appendValue}
          databaseName={databaseName}
          dataSourceId={dataSourceId}
          tableName={tableName}
          schemaName={schemaName}
          databaseType={databaseType}
          executeSuccessCallBack={executeSuccessCallBack}
        />
      </Modal>
    </Context.Provider>
  );
});
