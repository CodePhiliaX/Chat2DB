import React, { useState, useRef, useEffect, useCallback } from 'react';
import { Modal, Upload, Select, message, Button, Progress, Steps, Table, Spin, Radio, Popconfirm } from 'antd';
import { UploadOutlined, BulbFilled } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import i18n from '@/i18n';
import taskService, { IPreviewHeadersResult } from '@/service/task';
import { setOpenImportDataModal } from '@/pages/main/workspace/store/modal';
import { setPendingAiChat, setCurrentWorkspaceExtend, IFieldMappingResult } from '@/pages/main/workspace/store/common';

const { Dragger } = Upload;
const { Step } = Steps;

export interface IImportDataModalParams {
  tableName: string;
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
  executedCallback?: () => void;
}

export interface IFieldMapping {
  sourceField: string;
  targetField: string;
  primaryKey: boolean;
}

const ImportDataModal = () => {
  const [open, setOpen] = useState(false);
  const [params, setParams] = useState<IImportDataModalParams | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [fileType, setFileType] = useState<string>('CSV');
  const [importProgress, setImportProgress] = useState<number>(0);
  const [importModalVisible, setImportModalVisible] = useState<boolean>(false);
  const [importing, setImporting] = useState<boolean>(false);
  const pollingRef = useRef<NodeJS.Timeout | null>(null);
  const [logs, setLogs] = useState<string[]>([]);
  const executedCallbackRef = useRef<IImportDataModalParams['executedCallback']>();

  // 向导步骤
  const [currentStep, setCurrentStep] = useState<number>(0);

  // 字段映射相关状态
  const [previewLoading, setPreviewLoading] = useState<boolean>(false);
  const [previewData, setPreviewData] = useState<IPreviewHeadersResult | null>(null);
  const [fieldMappings, setFieldMappings] = useState<IFieldMapping[]>([]);
  const [guessLoading, setGuessLoading] = useState<boolean>(false);

  // 导入模式
  const [importMode, setImportMode] = useState<string>('INSERT');

  useEffect(() => {
    if (!open) {
      setFile(null);
      setImportProgress(0);
      setLogs([]);
      setCurrentStep(0);
      setPreviewData(null);
      setFieldMappings([]);
      setImportMode('INSERT');
    }
  }, [open]);

  const openImportDataModal = (importParams: IImportDataModalParams) => {
    setOpen(true);
    setParams(importParams);
    executedCallbackRef.current = importParams.executedCallback;
    setCurrentStep(0);
    setPreviewData(null);
    setFieldMappings([]);
  };

  useEffect(() => {
    setOpenImportDataModal(openImportDataModal);
  }, []);

  const handleFileChange = (info: any) => {
    if (info.file) {
      setFile(info.file.originFileObj || info.file);
    }
  };

  const handleAiGuessMapping = useCallback(() => {
    if (!params || !previewData) {
      message.warning('请先上传文件并预览');
      return;
    }

    setGuessLoading(true);
    setPendingAiChat({
      dataSourceId: params.dataSourceId,
      databaseName: params.databaseName,
      schemaName: params.schemaName,
      tableNames: [params.tableName],
      message: `请为表 ${params.tableName} 推荐字段映射方案`,
      promptType: 'NL_2_FIELD_MAPPING',
      ext: JSON.stringify({
        sourceFields: previewData.headers.map(h => h.name),
      }),
      onMappingGenerated: handleMappingGenerated,
    });
    setCurrentWorkspaceExtend('ai');
    setGuessLoading(false);
    message.success('已切换到 AI 助手，请在 AI 聊天面板中查看推荐结果');
  }, [params, previewData]);

  const handleMappingGenerated = useCallback((result: IFieldMappingResult) => {
    if (!result || !result.mappings || result.mappings.length === 0) {
      message.warning('未获取到映射推荐');
      return;
    }

    const newMappings = fieldMappings.map(m => {
      const matched = result.mappings.find(r => r.sourceField === m.sourceField);
      if (matched && matched.targetField) {
        const targetCol = previewData?.tableColumns.find(col => col.name === matched.targetField);
        return {
          ...m,
          targetField: matched.targetField,
          primaryKey: !!targetCol?.primaryKey,
        };
      }
      return m;
    });

    setFieldMappings(newMappings);
    message.success(`AI 已推荐 ${result.mappings.length} 个字段映射，请查看并确认`);
  }, [fieldMappings, previewData]);

  const addLog = (log: string) => {
    const timestamp = new Date().toLocaleString();
    setLogs(prev => [...prev, `${timestamp}: ${log}`]);
  };

  // 下一步：预览文件表头
  const handleNextToMapping = async () => {
    if (!file || !params) {
      message.error(i18n('workspace.table.import.selectFile'));
      return;
    }

    setPreviewLoading(true);
    try {
      const result = await taskService.previewFileHeaders({
        file,
        tableName: params.tableName,
        fileType,
        dataSourceId: params.dataSourceId,
        databaseName: params.databaseName,
        schemaName: params.schemaName,
      });

      setPreviewData(result);

      // 根据自动匹配结果初始化字段映射
      const mappings: IFieldMapping[] = result.autoMappings.map((mapping) => ({
        sourceField: mapping.sourceField,
        targetField: mapping.targetField || '',
        primaryKey: false,
      }));

      // 设置主键标识
      result.tableColumns.forEach((col) => {
        if (col.primaryKey) {
          const mapping = mappings.find((m) => m.targetField === col.name);
          if (mapping) {
            mapping.primaryKey = true;
          }
        }
      });

      setFieldMappings(mappings);
      setCurrentStep(1);
    } catch (error: any) {
      message.error(error.message || 'Preview failed');
    } finally {
      setPreviewLoading(false);
    }
  };

  // 开始导入
  const handleImport = async () => {
    if (!file || !params) {
      message.error(i18n('workspace.table.import.selectFile'));
      return;
    }

    // 验证映射配置
    const unmappedFields = fieldMappings.filter((m) => !m.targetField);
    if (unmappedFields.length > 0) {
      message.error(`源字段 "${unmappedFields.map((m) => m.sourceField).join(', ')}" 未映射到目标字段`);
      return;
    }

    setImporting(true);
    setImportModalVisible(true);
    setImportProgress(0);
    setLogs([]);
    addLog('start------');

    try {
      const mappingsJson = JSON.stringify(fieldMappings);
      const taskId = await taskService.importData({
        file,
        tableName: params.tableName,
        fileType,
        dataSourceId: params.dataSourceId,
        databaseName: params.databaseName,
        schemaName: params.schemaName,
        fieldMappings: mappingsJson,
        importMode,
      } as any);

      addLog(`Task created: ${taskId}`);
      startImportPolling(taskId);
      setCurrentStep(3);
    } catch (error: any) {
      addLog(`Error: ${error.message}`);
      message.error(i18n('common.text.importFailed'));
      setImporting(false);
      setImportModalVisible(false);
    }
  };

  const startImportPolling = (taskId: number) => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
    }

    pollingRef.current = setInterval(async () => {
      try {
        const task = await taskService.getTask({ id: taskId });
        if (task) {
          const processedCount = parseInt(task.taskProgress || '0', 10);
          setImportProgress(processedCount);
          addLog(`Progress: ${processedCount} rows imported`);

          if (task.taskStatus === 'FINISH') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            addLog('Import completed successfully');
            message.success(i18n('common.text.importSuccess'));
            setImporting(false);
            executedCallbackRef.current?.();
            setTimeout(() => {
              setImportModalVisible(false);
              setOpen(false);
            }, 2000);
          } else if (task.taskStatus === 'ERROR') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            let errorMsg = i18n('common.text.importFailed');
            if (task.content) {
              errorMsg = task.content;
            }
            addLog(`Error: ${errorMsg}`);
            message.error(errorMsg);
            setImporting(false);
          }
        }
      } catch (error: any) {
        clearInterval(pollingRef.current!);
        pollingRef.current = null;
        addLog(`Polling error: ${error.message}`);
        message.error(i18n('common.text.importFailed'));
        setImporting(false);
      }
    }, 1000);
  };

  const handleClose = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
    setImportModalVisible(false);
    if (!importing) {
      setOpen(false);
    }
  };

  const uploadProps = {
    name: 'file',
    multiple: false,
    showUploadList: false,
    onChange: handleFileChange,
    beforeUpload: (uploadFile: File) => {
      setFile(uploadFile);
      return false;
    },
  };

  const fileTypes = [
    { label: i18n('workspace.table.import.fileType.csv'), value: 'CSV' },
    { label: i18n('workspace.table.import.fileType.xlsx'), value: 'XLSX' },
    { label: i18n('workspace.table.import.fileType.xls'), value: 'XLS' },
  ];

  // 字段映射表格列定义
  const mappingColumns: ColumnsType<IFieldMapping> = [
    {
      title: i18n('workspace.table.import.fieldMapping.sourceField'),
      dataIndex: 'sourceField',
      key: 'sourceField',
      width: 180,
    },
    {
      title: i18n('workspace.table.import.fieldMapping.targetField'),
      dataIndex: 'targetField',
      key: 'targetField',
      width: 180,
      render: (text: string, record: IFieldMapping) => (
        <Select
          value={text || undefined}
          placeholder={i18n('workspace.table.import.fieldMapping.pleaseSelect')}
          style={{ width: '100%' }}
          onChange={(value) => {
            const newMappings = [...fieldMappings];
            const index = newMappings.findIndex((m) => m.sourceField === record.sourceField);
            if (index >= 0) {
              newMappings[index].targetField = value;
              // 更新主键标识
              if (previewData) {
                const targetCol = previewData.tableColumns.find((col) => col.name === value);
                newMappings[index].primaryKey = !!targetCol?.primaryKey;
              }
              setFieldMappings(newMappings);
            }
          }}
          options={previewData?.tableColumns.map((col) => ({
            label: `${col.name}${col.primaryKey ? ' (PK)' : ''}`,
            value: col.name,
          }))}
        />
      ),
    },
    {
      title: i18n('workspace.table.import.fieldMapping.primaryKey'),
      dataIndex: 'primaryKey',
      key: 'primaryKey',
      width: 80,
      render: (text: boolean) => (
        <span style={{ textAlign: 'center', display: 'block' }}>
          {text ? '✓' : ''}
        </span>
      ),
    },
  ];

  // 获取步骤标题
  const getStepTitle = () => {
    switch (currentStep) {
      case 0:
        return i18n('workspace.table.import.step.selectFile');
      case 1:
        return i18n('workspace.table.import.step.fieldMapping');
      case 2:
        return i18n('workspace.table.import.step.importMode');
      case 3:
        return i18n('workspace.table.import.step.importProgress');
      default:
        return '';
    }
  };

  // 渲染步骤内容
  const renderStepContent = () => {
    switch (currentStep) {
      case 0:
        return (
          <div style={{ padding: '16px 0' }}>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.targetTable')}:
              </label>
              <div style={{ padding: '8px 12px', background: 'var(--color-bg-layout)', borderRadius: 4, color: 'var(--color-text)' }}>
                {params?.tableName}
              </div>
            </div>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.fileType')}:
              </label>
              <Select
                value={fileType}
                onChange={setFileType}
                options={fileTypes}
                style={{ width: '200px' }}
                disabled={importing}
              />
            </div>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.uploadFile')}:
              </label>
              <Dragger {...uploadProps}>
                <p className="ant-upload-drag-icon">
                  <UploadOutlined />
                </p>
                <p className="ant-upload-text">{i18n('workspace.table.import.uploadFile')}</p>
                <p className="ant-upload-hint">
                  {file ? file.name : i18n('workspace.table.import.uploadHint')}
                </p>
              </Dragger>
            </div>
          </div>
        );

      case 1:
        return (
          <div style={{ padding: '16px 0' }}>
            <div style={{ 
              marginBottom: 16, 
              display: 'flex', 
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <div style={{ color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.fieldMapping.description')}
              </div>
              <Spin spinning={guessLoading}>
                <Button
                  type="primary"
                  icon={<BulbFilled />}
                  onClick={handleAiGuessMapping}
                  disabled={!previewData || previewLoading}
                  size="small"
                >
                  猜一猜
                </Button>
              </Spin>
            </div>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.fieldMapping.source')}:
              </label>
              <div style={{ padding: '8px 12px', background: 'var(--color-bg-layout)', borderRadius: 4, color: 'var(--color-text)' }}>
                {file?.name || '-'}
              </div>
            </div>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 8, color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.fieldMapping.targetTable')}:
              </label>
              <div style={{ padding: '8px 12px', background: 'var(--color-bg-layout)', borderRadius: 4, color: 'var(--color-text)' }}>
                {params?.tableName}
              </div>
            </div>
            {previewLoading ? (
              <div style={{ textAlign: 'center', padding: '40px 0' }}>
                <Spin tip={i18n('workspace.table.import.fieldMapping.loading')} />
              </div>
            ) : (
              <Table
                columns={mappingColumns}
                dataSource={fieldMappings}
                rowKey="sourceField"
                pagination={false}
                size="small"
                scroll={{ y: 300 }}
              />
            )}
          </div>
        );

      case 2:
        {
          const hasPrimaryKey = fieldMappings.some((m) => m.primaryKey);
          return (
            <div style={{ padding: '16px 0' }}>
              <div style={{ marginBottom: 16, color: 'var(--color-text)' }}>
                {i18n('workspace.table.import.mode.description')}
              </div>
              <Radio.Group
                value={importMode}
                onChange={(e) => setImportMode(e.target.value)}
                style={{ display: 'flex', flexDirection: 'column', gap: 12 }}
              >
                <Radio value="INSERT">{i18n('workspace.table.import.mode.insert')}</Radio>
                <Radio value="UPDATE" disabled={!hasPrimaryKey}>
                  {i18n('workspace.table.import.mode.update')}
                  {!hasPrimaryKey && (
                    <span style={{ color: '#999', fontSize: 12, marginLeft: 8 }}>
                      ({i18n('workspace.table.import.mode.pkRequired')})
                    </span>
                  )}
                </Radio>
                <Radio value="UPSERT" disabled={!hasPrimaryKey}>
                  {i18n('workspace.table.import.mode.upsert')}
                  {!hasPrimaryKey && (
                    <span style={{ color: '#999', fontSize: 12, marginLeft: 8 }}>
                      ({i18n('workspace.table.import.mode.pkRequired')})
                    </span>
                  )}
                </Radio>
                <Radio value="INSERT_IGNORE">{i18n('workspace.table.import.mode.insertIgnore')}</Radio>
                <Radio value="DELETE" disabled={!hasPrimaryKey}>
                  {i18n('workspace.table.import.mode.delete')}
                  {!hasPrimaryKey && (
                    <span style={{ color: '#999', fontSize: 12, marginLeft: 8 }}>
                      ({i18n('workspace.table.import.mode.pkRequired')})
                    </span>
                  )}
                </Radio>
                <Radio value="REPLACE">{i18n('workspace.table.import.mode.replace')}</Radio>
              </Radio.Group>
            </div>
          );
        }

      case 3:
        return (
          <div style={{ color: 'var(--color-text)' }}>
            <div style={{ marginBottom: 16 }}>
              <div>{i18n('workspace.table.import.progress.taskName')}: Import {params?.tableName}</div>
            </div>
            <div style={{
              maxHeight: '300px',
              overflowY: 'auto',
              background: 'var(--color-bg-layout)',
              padding: '12px',
              borderRadius: '4px',
              marginBottom: '16px',
              fontFamily: 'monospace',
              fontSize: '12px'
            }}>
              {logs.map((log, index) => (
                <div key={index} style={{ marginBottom: '4px' }}>{log}</div>
              ))}
            </div>
            <Progress percent={importProgress > 0 ? Math.min(importProgress, 100) : 0} status="active" />
            <div style={{ marginTop: 8 }}>
              {i18n('workspace.table.import.progress.rows')}: {importProgress}
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  // 获取底部按钮
  const getFooterButtons = () => {
    if (currentStep === 0) {
      return [
        <Button key="cancel" onClick={() => setOpen(false)} disabled={importing}>
          {i18n('workspace.table.import.cancel')}
        </Button>,
        <Button key="next" type="primary" onClick={handleNextToMapping} disabled={!file || previewLoading}>
          {i18n('workspace.table.import.next')}
        </Button>,
      ];
    } else if (currentStep === 1) {
      return [
        <Button key="previous" onClick={() => setCurrentStep(0)} disabled={importing}>
          {i18n('workspace.table.import.previous')}
        </Button>,
        <Button key="next" type="primary" onClick={() => setCurrentStep(2)} disabled={previewLoading}>
          {i18n('workspace.table.import.next')}
        </Button>,
      ];
    } else if (currentStep === 2) {
      const startButton = importMode === 'REPLACE' ? (
        <Popconfirm
          key="start"
          title={i18n('workspace.table.import.mode.replaceConfirm')}
          onConfirm={handleImport}
          okText={i18n('workspace.table.import.start')}
          cancelText={i18n('workspace.table.import.cancel')}
        >
          <Button type="primary" disabled={previewLoading || importing}>
            {i18n('workspace.table.import.start')}
          </Button>
        </Popconfirm>
      ) : (
        <Button key="start" type="primary" onClick={handleImport} disabled={previewLoading || importing}>
          {i18n('workspace.table.import.start')}
        </Button>
      );
      return [
        <Button key="previous" onClick={() => setCurrentStep(1)} disabled={importing}>
          {i18n('workspace.table.import.previous')}
        </Button>,
        startButton,
      ];
    } else {
      return [
        <Button key="close" onClick={handleClose}>
          {i18n('workspace.table.import.close')}
        </Button>,
      ];
    }
  };

  return !!params && (
    <>
      <Modal
        title={i18n('workspace.table.import.title')}
        open={open}
        onCancel={handleClose}
        footer={getFooterButtons()}
        width={currentStep === 1 ? 700 : 500}
        closable={currentStep !== 3}
      >
        <Steps current={currentStep} style={{ marginBottom: 24 }}>
          <Step title={i18n('workspace.table.import.step.selectFile')} />
          <Step title={i18n('workspace.table.import.step.fieldMapping')} />
          <Step title={i18n('workspace.table.import.step.importMode')} />
          <Step title={i18n('workspace.table.import.step.importProgress')} />
        </Steps>

        {renderStepContent()}
      </Modal>
    </>
  );
};

export default ImportDataModal;
