import React, { memo, useRef, useState, createContext, useEffect } from 'react';
import { Button, Modal, message } from 'antd';
import i18n from '@/i18n';
import lodash from 'lodash';
import styles from './index.less';
import classnames from 'classnames';
import BaseInfo, { ISequenceInfoRef } from './BaseInfo';
import sqlService, { IModifySequenceSqlParams, IModifyTableSqlParams } from '@/service/sql';
import ExecuteSQL from '@/components/ExecuteSQL';
import { ISequenceInfo, IWorkspaceTab, IColumnTypes } from '@/typings';
import { DatabaseTypeCode, WorkspaceTabType } from '@/constants';
import LoadingContent from '@/components/Loading/LoadingContent';
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
  const [databaseSupportField, setDatabaseSupportField] = useState<IDatabaseSupportField>({
    columnTypes: [],
    charsets: [],
    collations: [],
    indexTypes: [],
    defaultValues: [],
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    if (tableName) {
      getSequenceDetails();
    }
    // getDatabaseFieldTypeList();
  }, []);

  // // 获取数据库字段类型列表
  // const getDatabaseFieldTypeList = () => {
  //   sqlService
  //     .getDatabaseFieldTypeList({
  //       dataSourceId,
  //       databaseName,
  //     })
  //     .then((res) => {
  //       const columnTypes =
  //         res?.columnTypes?.map((i) => {
  //           return {
  //             ...i,
  //             value: i.typeName,
  //             label: i.typeName,
  //           };
  //         }) || [];

  //       const charsets =
  //         res?.charsets?.map((i) => {
  //           return {
  //             value: i.charsetName,
  //             label: i.charsetName,
  //           };
  //         }) || [];

  //       const collations =
  //         res?.collations?.map((i) => {
  //           return {
  //             value: i.collationName,
  //             label: i.collationName,
  //           };
  //         }) || [];

  //       const indexTypes =
  //         res?.indexTypes?.map((i) => {
  //           return {
  //             value: i.typeName,
  //             label: i.typeName,
  //           };
  //         }) || [];

  //       const defaultValues =
  //         res?.defaultValues?.map((i) => {
  //           return {
  //             value: i.defaultValue,
  //             label: i.defaultValue,
  //           };
  //         }) || [];

  //       setDatabaseSupportField({
  //         columnTypes,
  //         charsets,
  //         collations,
  //         indexTypes,
  //         defaultValues,
  //       });
  //     });
  // };

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
        databaseType,
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
