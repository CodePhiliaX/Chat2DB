import React, { useState, useRef, useEffect } from 'react';
import { Modal, Select, message, Button, Progress, Radio, Input } from 'antd';
import i18n from '@/i18n';
import taskService, { ITask } from '@/service/task';
import { setOpenExportDataModal } from '@/pages/main/workspace/store/modal';

export interface IExportDataModalParams {
  tableName: string;
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
}

const ExportDataModal = () => {
  const [open, setOpen] = useState(false);
  const [params, setParams] = useState<IExportDataModalParams | null>(null);
  const [exportType, setExportType] = useState<string>('CSV');
  const [exportProgress, setExportProgress] = useState<number>(0);
  const [exportModalVisible, setExportModalVisible] = useState<boolean>(false);
  const [exporting, setExporting] = useState<boolean>(false);
  const [logs, setLogs] = useState<string[]>([]);
  const pollingRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (!open) {
      setExportProgress(0);
      setLogs([]);
    }
  }, [open]);

  const openExportDataModal = (exportParams: IExportDataModalParams) => {
    setOpen(true);
    setParams(exportParams);
  };

  useEffect(() => {
    setOpenExportDataModal(openExportDataModal);
  }, []);

  const addLog = (log: string) => {
    const timestamp = new Date().toLocaleString();
    setLogs(prev => [...prev, `${timestamp}: ${log}`]);
  };

  const handleExport = async () => {
    if (!params) return;

    setExporting(true);
    setExportModalVisible(true);
    setExportProgress(0);
    setLogs([]);
    addLog('start------');

    try {
      const sql = `SELECT * FROM ${params.tableName}`;
      const taskId = await taskService.exportResultData({
        sql,
        originalSql: sql,
        exportType,
        exportSize: 'ALL',
        dataSourceId: params.dataSourceId,
        databaseName: params.databaseName,
        schemaName: params.schemaName,
      });

      addLog(`Task created: ${taskId}`);
      startExportPolling(taskId);
    } catch (error) {
      addLog(`Error: ${error}`);
      message.error(i18n('workspace.table.export.failed'));
      setExporting(false);
      setExportModalVisible(false);
    }
  };

  const startExportPolling = (taskId: number) => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
    }

    pollingRef.current = setInterval(async () => {
      try {
        const task: ITask = await taskService.getTask({ id: taskId });
        if (task) {
          const processedCount = parseInt(task.taskProgress || '0', 10);
          setExportProgress(processedCount);
          addLog(`Progress: ${processedCount} rows exported`);

          if (task.taskStatus === 'FINISH') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            addLog('Export completed successfully');
            downloadExportFile(taskId);
            message.success(i18n('workspace.table.export.success'));
            setExporting(false);
            setTimeout(() => {
              setExportModalVisible(false);
              setOpen(false);
            }, 2000);
          } else if (task.taskStatus === 'ERROR') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            let errorMsg = i18n('workspace.table.export.failed');
            if (task.content) {
              errorMsg = task.content;
            }
            addLog(`Error: ${errorMsg}`);
            message.error(errorMsg);
            setExporting(false);
          }
        }
      } catch (error) {
        clearInterval(pollingRef.current!);
        pollingRef.current = null;
        addLog(`Polling error: ${error}`);
        message.error(i18n('workspace.table.export.failed'));
        setExporting(false);
      }
    }, 1000);
  };

  const downloadExportFile = (taskId: number) => {
    const downloadUrl = `${window._BaseURL}/api/task/download/${taskId}`;
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleClose = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
    setExportModalVisible(false);
    if (!exporting) {
      setOpen(false);
    }
  };

  const exportTypes = [
    { label: 'CSV', value: 'CSV' },
    { label: 'Excel (XLSX)', value: 'EXCEL' },
    { label: 'INSERT SQL', value: 'INSERT' },
  ];

  return !!params && (
    <>
      <Modal
        title={i18n('workspace.table.export.title')}
        open={open}
        onCancel={() => setOpen(false)}
        footer={[
          <Button key="cancel" onClick={() => setOpen(false)} disabled={exporting}>
            {i18n('workspace.table.export.cancel')}
          </Button>,
          <Button key="start" type="primary" onClick={handleExport} disabled={exporting}>
            {i18n('workspace.table.export.start')}
          </Button>,
        ]}
        width={500}
      >
        <div style={{ padding: '16px 0' }}>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
              {i18n('workspace.table.export.sourceTable')}:
            </label>
            <div style={{ padding: '8px 12px', background: 'var(--color-bg-layout)', borderRadius: 4, color: 'var(--color-text)' }}>
              {params?.tableName}
            </div>
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
              {i18n('workspace.table.export.fileType')}:
            </label>
            <Radio.Group
              value={exportType}
              onChange={(e) => setExportType(e.target.value)}
              disabled={exporting}
            >
              {exportTypes.map(t => (
                <Radio key={t.value} value={t.value}>{t.label}</Radio>
              ))}
            </Radio.Group>
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
              {i18n('workspace.table.export.sql')}:
            </label>
            <Input.TextArea
              value={`SELECT * FROM ${params?.tableName}`}
              readOnly
              autoSize={{ minRows: 2, maxRows: 4 }}
              style={{ background: 'var(--color-bg-layout)', color: 'var(--color-text)' }}
            />
          </div>
        </div>
      </Modal>

      <Modal
        title={i18n('workspace.table.export.progress.log')}
        open={exportModalVisible}
        footer={[
          <Button key="close" onClick={handleClose}>
            {i18n('workspace.table.export.close')}
          </Button>,
        ]}
        width={600}
        closable={false}
      >
        <div style={{ color: 'var(--color-text)' }}>
          <div style={{ marginBottom: 16 }}>
            <div>{i18n('workspace.table.export.progress.taskName')}: Export {params?.tableName}</div>
          </div>
          <div style={{
            maxHeight: '300px',
            overflowY: 'auto',
            background: 'var(--color-bg-layout)',
            padding: '12px',
            borderRadius: '4px',
            marginBottom: '16px',
            fontFamily: 'monospace',
            fontSize: '12px',
          }}>
            {logs.map((log, index) => (
              <div key={index} style={{ marginBottom: '4px' }}>{log}</div>
            ))}
          </div>
          <Progress percent={exportProgress > 0 ? Math.min(exportProgress, 100) : 0} status="active" />
          <div style={{ marginTop: 8 }}>
            {i18n('workspace.table.export.progress.rows')}: {exportProgress}
          </div>
        </div>
      </Modal>
    </>
  );
};

export default ExportDataModal;
