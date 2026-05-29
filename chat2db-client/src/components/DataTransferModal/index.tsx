import React, { memo, useEffect, useRef, useState } from 'react';
import { Alert, Button, Descriptions, Input, Modal, Progress, Select, Steps, Table, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import connectionService from '@/service/connection';
import sqlService from '@/service/sql';
import taskService from '@/service/task';
import { getConnectionList, useConnectionStore } from '@/pages/main/store/connection';
import { IDatabaseItem, ISchemaItem } from '@/typings';
import { IConnectionListItem } from '@/typings/connection';
import { setOpenDataTransferModal } from '@/pages/main/workspace/store/modal';

export interface IDataTransferModalParams {
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
  tableNames?: string[];
  executedCallback?: () => void;
}

interface ITableRow {
  name: string;
  comment?: string;
  rowCount?: number;
}

const DataTransferModal = memo(() => {
  const connectionList = useConnectionStore((s) => s.connectionList);
  const [open, setOpen] = useState(false);
  const [params, setParams] = useState<IDataTransferModalParams | null>(null);
  const [currentStep, setCurrentStep] = useState(0);
  const [sourceTableData, setSourceTableData] = useState<ITableRow[]>([]);
  const [selectedTableNames, setSelectedTableNames] = useState<string[]>([]);
  const [tableLoading, setTableLoading] = useState(false);
  const [searchKey, setSearchKey] = useState('');
  const [targetDataSourceId, setTargetDataSourceId] = useState<number>();
  const [targetDatabaseName, setTargetDatabaseName] = useState<string>();
  const [targetSchemaName, setTargetSchemaName] = useState<string>();
  const [targetDatabases, setTargetDatabases] = useState<IDatabaseItem[]>([]);
  const [targetSchemas, setTargetSchemas] = useState<ISchemaItem[]>([]);
  const [targetDbLoading, setTargetDbLoading] = useState(false);
  const [targetSchemaLoading, setTargetSchemaLoading] = useState(false);
  const [transferring, setTransferring] = useState(false);
  const [transferProgress, setTransferProgress] = useState(0);
  const [logs, setLogs] = useState<string[]>([]);
  const pollingRef = useRef<NodeJS.Timeout | null>(null);
  const lastTaskContentRef = useRef<string>('');
  const executedCallbackRef = useRef<IDataTransferModalParams['executedCallback']>();

  useEffect(() => {
    setOpenDataTransferModal((modalParams: IDataTransferModalParams) => {
      setParams(modalParams);
      executedCallbackRef.current = modalParams.executedCallback;
      setSelectedTableNames(modalParams.tableNames || []);
      setTargetDataSourceId(undefined);
      setTargetDatabaseName(undefined);
      setTargetSchemaName(undefined);
      setTargetDatabases([]);
      setTargetSchemas([]);
      setCurrentStep(modalParams.tableNames?.length ? 1 : 0);
      setLogs([]);
      lastTaskContentRef.current = '';
      setTransferProgress(0);
      setOpen(true);
    });
  }, []);

  useEffect(() => {
    if (!connectionList) {
      getConnectionList();
    }
  }, [connectionList]);

  useEffect(() => {
    if (open && params && currentStep === 0) {
      loadSourceTables();
    }
  }, [open, params, currentStep, searchKey]);

  useEffect(() => {
    if (!targetDataSourceId) {
      setTargetDatabases([]);
      return;
    }
    setTargetDbLoading(true);
    setTargetDatabaseName(undefined);
    setTargetSchemaName(undefined);
    setTargetSchemas([]);
    connectionService.getDatabaseList({ dataSourceId: targetDataSourceId })
      .then((res) => setTargetDatabases(Array.isArray(res) ? res : []))
      .catch(() => message.error('加载目标数据库失败'))
      .finally(() => setTargetDbLoading(false));
  }, [targetDataSourceId]);

  useEffect(() => {
    if (!targetDataSourceId || !targetDatabaseName) {
      setTargetSchemas([]);
      return;
    }
    setTargetSchemaLoading(true);
    setTargetSchemaName(undefined);
    connectionService.getSchemaList({ dataSourceId: targetDataSourceId, databaseName: targetDatabaseName })
      .then((res) => setTargetSchemas(Array.isArray(res) ? res : []))
      .catch(() => message.error('加载目标 Schema 失败'))
      .finally(() => setTargetSchemaLoading(false));
  }, [targetDataSourceId, targetDatabaseName]);

  const loadSourceTables = () => {
    if (!params) {
      return;
    }
    setTableLoading(true);
    sqlService.getTableList({
      dataSourceId: params.dataSourceId,
      databaseName: params.databaseName || '',
      schemaName: params.schemaName,
      searchKey,
      pageNo: 1,
      pageSize: 1000,
    })
      .then((res) => {
        setSourceTableData((res.data || []).map((table) => ({
          name: table.name,
          comment: table.comment,
          rowCount: table.rowCount,
        })));
      })
      .finally(() => setTableLoading(false));
  };

  const addLog = (log: string) => {
    setLogs((prev) => [...prev, `${new Date().toLocaleString()}: ${log}`]);
  };

  const handleStartTransfer = async () => {
    if (!params || !targetDataSourceId || !targetDatabaseName || selectedTableNames.length === 0) {
      message.warning('请选择源表和目标库');
      return;
    }

    setTransferring(true);
    setCurrentStep(3);
    setLogs([]);
    setTransferProgress(0);
    addLog('start------');

    try {
      const taskId = await taskService.transferData({
        sourceDataSourceId: params.dataSourceId,
        sourceDatabaseName: params.databaseName,
        sourceSchemaName: params.schemaName,
        targetDataSourceId,
        targetDatabaseName,
        targetSchemaName,
        tableNames: selectedTableNames,
      });
      addLog(`Task created: ${taskId}`);
      startPolling(taskId);
    } catch (error: any) {
      addLog(`Error: ${error.message}`);
      message.error(error.message || '数据传输失败');
      setTransferring(false);
    }
  };

  const startPolling = (taskId: number) => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
    }

    pollingRef.current = setInterval(async () => {
      try {
        const task = await taskService.getTask({ id: taskId });
        const processedCount = parseInt(task.taskProgress || '0', 10);
        setTransferProgress(processedCount);
        if (task.content && task.content !== lastTaskContentRef.current) {
          lastTaskContentRef.current = task.content;
          addLog(task.content);
        }
        if (task.taskStatus === 'FINISH') {
          clearInterval(pollingRef.current!);
          pollingRef.current = null;
          addLog('Transfer completed successfully');
          message.success('数据传输成功');
          setTransferring(false);
          executedCallbackRef.current?.();
        } else if (task.taskStatus === 'ERROR') {
          clearInterval(pollingRef.current!);
          pollingRef.current = null;
          addLog(`Error: ${task.content || '数据传输失败'}`);
          message.error(task.content || '数据传输失败');
          setTransferring(false);
        }
      } catch (error: any) {
        clearInterval(pollingRef.current!);
        pollingRef.current = null;
        addLog(`Polling error: ${error.message}`);
        message.error('数据传输失败');
        setTransferring(false);
      }
    }, 1000);
  };

  const handleClose = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
    if (!transferring) {
      setOpen(false);
    }
  };

  const columns: ColumnsType<ITableRow> = [
    { title: 'Table name', dataIndex: 'name', key: 'name' },
    { title: 'Row Count', dataIndex: 'rowCount', key: 'rowCount', width: 120 },
    { title: 'Comment', dataIndex: 'comment', key: 'comment' },
  ];

  const renderStepContent = () => {
    if (!params) {
      return null;
    }

    if (currentStep === 0) {
      return (
        <>
          <Input.Search
            placeholder="搜索表"
            allowClear
            onSearch={setSearchKey}
            style={{ marginBottom: 12, width: 260 }}
          />
          <Table
            rowKey="name"
            loading={tableLoading}
            columns={columns}
            dataSource={sourceTableData}
            pagination={false}
            size="small"
            scroll={{ y: 360 }}
            rowSelection={{
              selectedRowKeys: selectedTableNames,
              onChange: (keys) => setSelectedTableNames(keys as string[]),
            }}
          />
        </>
      );
    }

    if (currentStep === 1) {
      return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <Alert message="目标表必须已存在且字段兼容。首版按同名字段追加插入，不会清空目标表。" type="warning" showIcon />
          <Select
            showSearch
            placeholder="选择目标数据源"
            value={targetDataSourceId}
            onChange={setTargetDataSourceId}
            optionFilterProp="label"
            options={(connectionList || []).map((connection: IConnectionListItem) => ({
              label: connection.alias,
              value: connection.id,
            }))}
          />
          <Select
            placeholder="选择目标数据库"
            value={targetDatabaseName}
            onChange={setTargetDatabaseName}
            loading={targetDbLoading}
            options={targetDatabases.map((database) => ({ label: database.name, value: database.name }))}
          />
          {targetSchemas.length > 0 && (
            <Select
              allowClear
              placeholder="选择目标 Schema"
              value={targetSchemaName}
              onChange={setTargetSchemaName}
              loading={targetSchemaLoading}
              options={targetSchemas.map((schema) => ({ label: schema.name, value: schema.name }))}
            />
          )}
        </div>
      );
    }

    if (currentStep === 2) {
      return (
        <Descriptions column={1} bordered size="small">
          <Descriptions.Item label="源库">
            {params.dataSourceId} / {params.databaseName || '-'} / {params.schemaName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="目标库">
            {targetDataSourceId} / {targetDatabaseName || '-'} / {targetSchemaName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="表数量">{selectedTableNames.length}</Descriptions.Item>
          <Descriptions.Item label="写入模式">INSERT 追加插入</Descriptions.Item>
          <Descriptions.Item label="表列表">{selectedTableNames.join(', ')}</Descriptions.Item>
        </Descriptions>
      );
    }

    return (
      <div>
        <div
          style={{
            maxHeight: 300,
            overflowY: 'auto',
            background: 'var(--color-bg-layout)',
            padding: 12,
            borderRadius: 4,
            marginBottom: 16,
            fontFamily: 'monospace',
            fontSize: 12,
          }}
        >
          {logs.map((log, index) => (
            <div key={`${log}-${index}`}>{log}</div>
          ))}
        </div>
        <Progress percent={transferProgress > 0 ? Math.min(transferProgress, 100) : 0} status="active" />
        <div style={{ marginTop: 8 }}>已传输行数: {transferProgress}</div>
      </div>
    );
  };

  const footer = () => {
    if (currentStep === 0) {
      return [
        <Button key="cancel" onClick={handleClose}>取消</Button>,
        <Button key="next" type="primary" disabled={selectedTableNames.length === 0} onClick={() => setCurrentStep(1)}>
          下一步
        </Button>,
      ];
    }
    if (currentStep === 1) {
      return [
        <Button key="previous" disabled={!!params?.tableNames?.length} onClick={() => setCurrentStep(0)}>上一步</Button>,
        <Button
          key="next"
          type="primary"
          disabled={!targetDataSourceId || !targetDatabaseName}
          onClick={() => setCurrentStep(2)}
        >
          下一步
        </Button>,
      ];
    }
    if (currentStep === 2) {
      return [
        <Button key="previous" onClick={() => setCurrentStep(1)}>上一步</Button>,
        <Button key="start" type="primary" onClick={handleStartTransfer}>开始传输</Button>,
      ];
    }
    return [<Button key="close" disabled={transferring} onClick={handleClose}>关闭</Button>];
  };

  return (
    <Modal
      title="数据传输"
      open={open}
      onCancel={handleClose}
      footer={footer()}
      width={currentStep === 0 ? 760 : 620}
      maskClosable={!transferring}
      closable={!transferring}
      destroyOnHidden
    >
      <Steps
        current={currentStep}
        style={{ marginBottom: 24 }}
        items={[
          { title: '选择表' },
          { title: '目标库' },
          { title: '确认' },
          { title: '进度' },
        ]}
      />
      {renderStepContent()}
    </Modal>
  );
});

export default DataTransferModal;
