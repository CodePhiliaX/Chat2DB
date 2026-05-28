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
  const [fileContent, setFileContent] = useState<string>('');
  const [progress, setProgress] = useState<number>(0);
  const [processedCount, setProcessedCount] = useState<number>(0);
  const [totalStatements, setTotalStatements] = useState<number>(0);
  const [importing, setImporting] = useState<boolean>(false);
  const [logs, setLogs] = useState<string[]>([]);
  const executedCallbackRef = useRef<IExecuteSqlStatementModalParams['executedCallback']>();
  const pollingRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (!open) {
      setFile(null);
      setProgress(0);
      setProcessedCount(0);
      setTotalStatements(0);
      setFileContent('');
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
    setProcessedCount(0);
    const totalCount = estimateStatementCount(fileContent);
    setTotalStatements(totalCount);
    addLog('start------');

    try {
      const taskId = await taskService.executeSqlFile({
        file,
        dataSourceId: params.dataSourceId,
        databaseName: params.databaseName,
        schemaName: params.schemaName,
      });
      addLog(`Task created: ${taskId}`);
      startPolling(taskId, totalCount);
    } catch (error: any) {
      addLog(`Error: ${error.message}`);
      setImporting(false);
      message.error(error.message || i18n('common.text.importFailed'));
    }
  };

  const startPolling = (taskId: number, totalCount: number) => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
    }
    pollingRef.current = setInterval(async () => {
      try {
        const task = await taskService.getTask({ id: taskId });
        const latestProcessedCount = parseInt(task.taskProgress || '0', 10) || 0;
        setProcessedCount(latestProcessedCount);
        setProgress((prev) => {
          if (totalCount > 0) {
            return Math.min(Math.round((latestProcessedCount / totalCount) * 100), 99);
          }
          return prev;
        });
        addLog(
          totalCount > 0
            ? `Progress: ${latestProcessedCount}/${totalCount} statements executed`
            : `Progress: ${latestProcessedCount} statements executed`,
        );
        if (task.taskStatus === 'FINISH') {
          clearInterval(pollingRef.current!);
          pollingRef.current = null;
          setImporting(false);
          setProgress(100);
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

  const estimateStatementCount = (content: string) => {
    if (!content) {
      return 0;
    }
    let count = 0;
    const lines = content.split('\n');
    let current = '';
    for (const line of lines) {
      current += `${line}\n`;
      if (line.trim().endsWith(';')) {
        const sql = current.trim();
        if (sql && !sql.startsWith('--')) {
          count += 1;
        }
        current = '';
      }
    }
    const remaining = current.trim();
    if (remaining && !remaining.startsWith('--')) {
      count += 1;
    }
    return count;
  };

  const readSelectedFileContent = async (uploadFile: File) => {
    setFileContent(await uploadFile.text());
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
            readSelectedFileContent(uploadFile).catch(() => undefined);
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
      <Progress
        percent={Math.min(progress, 100)}
        status={importing ? 'active' : 'normal'}
        showInfo={totalStatements > 0 || !importing}
      />
      <div style={{ marginTop: 6, color: 'var(--color-text-secondary)', fontSize: 12 }}>
        {totalStatements > 0
          ? `${processedCount}/${totalStatements} statements`
          : `${processedCount} statements`}
      </div>
    </Modal>
  );
};

export default ExecuteSqlStatementModal;
