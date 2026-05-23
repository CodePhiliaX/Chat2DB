import React, { useState, useRef, useEffect } from 'react';
import { Modal, message, Button, Radio, Checkbox } from 'antd';
import i18n from '@/i18n';
import taskService, { ITask } from '@/service/task';
import { setOpenExportSchemaDocModal } from '@/pages/main/workspace/store/modal';

export interface IExportSchemaDocModalParams {
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
}

const ExportSchemaDocModal = () => {
  const [open, setOpen] = useState(false);
  const [params, setParams] = useState<IExportSchemaDocModalParams | null>(null);
  const [exportType, setExportType] = useState<string>('SQL');
  const [refreshLatest, setRefreshLatest] = useState<boolean>(false);
  const [exportModalVisible, setExportModalVisible] = useState<boolean>(false);
  const [exporting, setExporting] = useState<boolean>(false);
  const [logs, setLogs] = useState<string[]>([]);
  const pollingRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (!open) {
      setLogs([]);
    }
  }, [open]);

  const openExportSchemaDocModal = (exportParams: IExportSchemaDocModalParams) => {
    setOpen(true);
    setParams(exportParams);
  };

  useEffect(() => {
    setOpenExportSchemaDocModal(openExportSchemaDocModal);
  }, []);

  const addLog = (log: string) => {
    const timestamp = new Date().toLocaleString();
    setLogs(prev => [...prev, `${timestamp}: ${log}`]);
  };

  const handleExport = async () => {
    if (!params) return;

    setExporting(true);
    setExportModalVisible(true);
    setLogs([]);
    addLog('start------');

    try {
      const taskId = await taskService.exportSchemaDoc({
        exportType,
        exportSize: 'ALL',
        dataSourceId: params.dataSourceId,
        databaseName: params.databaseName,
        schemaName: params.schemaName,
        refresh: refreshLatest,
      });

      addLog(`Task created: ${taskId}`);
      startExportPolling(taskId);
    } catch (error) {
      addLog(`Error: ${error}`);
      message.error(i18n('workspace.schemaDoc.export.failed'));
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
          addLog(`Progress: ${task.taskProgress || '0'}%`);

          if (task.taskStatus === 'FINISH') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            addLog('Export completed successfully');
            downloadExportFile(taskId);
            message.success(i18n('workspace.schemaDoc.export.success'));
            setExporting(false);
            setTimeout(() => {
              setExportModalVisible(false);
              setOpen(false);
            }, 2000);
          } else if (task.taskStatus === 'ERROR') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            let errorMsg = i18n('workspace.schemaDoc.export.failed');
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
        message.error(i18n('workspace.schemaDoc.export.failed'));
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
    { label: 'SQL (DDL)', value: 'SQL' },
    { label: 'Markdown', value: 'MARKDOWN' },
    { label: 'Excel (XLSX)', value: 'EXCEL' },
    { label: 'Word (DOCX)', value: 'WORD' },
    { label: 'HTML', value: 'HTML' },
    { label: 'PDF', value: 'PDF' },
  ];

  return !!params && (
    <>
      <Modal
        title={i18n('workspace.schemaDoc.export.title')}
        open={open}
        onCancel={() => setOpen(false)}
        footer={[
          <Button key="cancel" onClick={() => setOpen(false)} disabled={exporting}>
            {i18n('workspace.schemaDoc.export.cancel')}
          </Button>,
          <Button key="start" type="primary" onClick={handleExport} disabled={exporting}>
            {i18n('workspace.schemaDoc.export.start')}
          </Button>,
        ]}
        width={500}
      >
        <div style={{ padding: '16px 0' }}>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
              {i18n('workspace.schemaDoc.export.database')}:
            </label>
            <div style={{ padding: '8px 12px', background: 'var(--color-bg-layout)', borderRadius: 4, color: 'var(--color-text)' }}>
              {params?.databaseName}
            </div>
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
              {i18n('workspace.schemaDoc.export.fileType')}:
            </label>
            <Radio.Group
              value={exportType}
              onChange={(e) => setExportType(e.target.value)}
              disabled={exporting}
            >
              {exportTypes.map(t => (
                <Radio key={t.value} value={t.value} style={{ display: 'block', marginBottom: 8 }}>{t.label}</Radio>
              ))}
            </Radio.Group>
          </div>
          <div style={{ marginBottom: 16 }}>
            <Checkbox checked={refreshLatest} onChange={(e) => setRefreshLatest(e.target.checked)} disabled={exporting}>
              {i18n('workspace.schemaDoc.export.refreshLatest')}
            </Checkbox>
            <div style={{ fontSize: 12, color: 'var(--color-text-secondary)', marginTop: 4 }}>
              {i18n('workspace.schemaDoc.export.refreshLatestTip')}
            </div>
          </div>
        </div>
      </Modal>

      <Modal
        title={i18n('workspace.schemaDoc.export.progress.log')}
        open={exportModalVisible}
        footer={[
          <Button key="close" onClick={handleClose}>
            {i18n('workspace.schemaDoc.export.close')}
          </Button>,
        ]}
        width={600}
        closable={false}
      >
        <div style={{ color: 'var(--color-text)' }}>
          <div style={{ marginBottom: 16 }}>
            <div>{i18n('workspace.schemaDoc.export.progress.taskName')}: Export {params?.databaseName}</div>
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
        </div>
      </Modal>
    </>
  );
};

export default ExportSchemaDocModal;
