import React, { useEffect, useRef, useState } from 'react';
import { Button, Modal, Progress, Upload, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import i18n from '@/i18n';
import taskService from '@/service/task';
import { setOpenExecuteSqlStatementModal } from '@/pages/main/workspace/store/modal';

const { Dragger } = Upload;

export interface IExecuteSqlStatementModalParams {
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
  executedCallback?: () => void;
}

const ExecuteSqlStatementModal = () => {
  const [open, setOpen] = useState(false);
  const [params, setParams] = useState<IExecuteSqlStatementModalParams | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [progress, setProgress] = useState<number>(0);
  const [importing, setImporting] = useState<boolean>(false);
  const [logs, setLogs] = useState<string[]>([]);
  const executedCallbackRef = useRef<IExecuteSqlStatementModalParams['executedCallback']>();
  const pollingRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (!open) {
      setFile(null);
      setProgress(0);
      setLogs([]);
      setImporting(false);
    }
  }, [open]);

  useEffect(() => {
    setOpenExecuteSqlStatementModal((modalParams: IExecuteSqlStatementModalParams) => {
      setParams(modalParams);
      executedCallbackRef.current = modalParams.executedCallback;
      setOpen(true);
    });
  }, []);

  const addLog = (log: string) => {
    const timestamp = new Date().toLocaleString();
    setLogs((prev) => [...prev, `${timestamp}: ${log}`]);
  };

  const handleStart = async () => {
    if (!file || !params) {
      message.error(i18n('workspace.table.import.selectFile'));
      return;
    }
    setImporting(true);
    setLogs([]);
    setProgress(0);
    addLog('start------');

    try {
      const taskId = await taskService.executeSqlFile({
        file,
        dataSourceId: params.dataSourceId,
        databaseName: params.databaseName,
        schemaName: params.schemaName,
      });
      addLog(`Task created: ${taskId}`);
      startPolling(taskId);
    } catch (error: any) {
      addLog(`Error: ${error.message}`);
      setImporting(false);
      message.error(error.message || i18n('common.text.importFailed'));
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
        setProgress(processedCount);
        addLog(`Progress: ${processedCount} statements executed`);
        if (task.taskStatus === 'FINISH') {
          clearInterval(pollingRef.current!);
          pollingRef.current = null;
          setImporting(false);
          addLog('Execute completed successfully');
          message.success(i18n('common.text.importSuccess'));
          executedCallbackRef.current?.();
        } else if (task.taskStatus === 'ERROR') {
          clearInterval(pollingRef.current!);
          pollingRef.current = null;
          setImporting(false);
          const errorMsg = task.content || i18n('common.text.importFailed');
          addLog(`Error: ${errorMsg}`);
          message.error(errorMsg);
        }
      } catch (error: any) {
        clearInterval(pollingRef.current!);
        pollingRef.current = null;
        setImporting(false);
        addLog(`Polling error: ${error.message}`);
        message.error(i18n('common.text.importFailed'));
      }
    }, 1000);
  };

  const handleClose = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
    if (!importing) {
      setOpen(false);
    }
  };

  return !!params && (
    <Modal
      title={i18n('workspace.menu.executeSqlStatement')}
      open={open}
      onCancel={handleClose}
      width={560}
      closable={!importing}
      footer={[
        <Button key="cancel" onClick={handleClose} disabled={importing}>
          {i18n('workspace.table.import.cancel')}
        </Button>,
        <Button key="start" type="primary" onClick={handleStart} disabled={!file || importing}>
          {i18n('workspace.table.import.start')}
        </Button>,
      ]}
    >
      <div style={{ marginBottom: 16 }}>
        <Dragger
          name="file"
          multiple={false}
          showUploadList={false}
          beforeUpload={(uploadFile: File) => {
            setFile(uploadFile);
            return false;
          }}
        >
          <p className="ant-upload-drag-icon">
            <UploadOutlined />
          </p>
          <p className="ant-upload-text">{i18n('workspace.table.import.uploadFile')}</p>
          <p className="ant-upload-hint">{file ? file.name : i18n('workspace.table.import.uploadHint')}</p>
        </Dragger>
      </div>
      <div
        style={{
          maxHeight: 220,
          overflowY: 'auto',
          background: 'var(--color-bg-layout)',
          padding: '12px',
          borderRadius: '4px',
          marginBottom: '16px',
          fontFamily: 'monospace',
          fontSize: '12px',
        }}
      >
        {logs.map((log, index) => (
          <div key={index} style={{ marginBottom: '4px' }}>
            {log}
          </div>
        ))}
      </div>
      <Progress percent={progress > 0 ? Math.min(progress, 100) : 0} status={importing ? 'active' : 'normal'} />
    </Modal>
  );
};

export default ExecuteSqlStatementModal;
