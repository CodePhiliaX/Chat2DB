import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Modal, Form, Button, Table, Select, Input, InputNumber, message, Progress } from 'antd';
import { BulbFilled } from '@ant-design/icons';
import { setOpenDataGenerationModal } from '@/pages/main/workspace/store/modal';
import { setPendingAiChat, setCurrentWorkspaceExtend } from '@/pages/main/workspace/store/common';
import { IDataExpressionResult } from '@/pages/main/workspace/store/common';
import createRequest from '@/service/base';
import taskService from '@/service/task';

const { Option } = Select;

export interface IDataGenerationModalParams {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  tableName: string;
}

interface TableInfo {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  tableName: string;
}

interface ColumnConfigVO {
  columnName: string;
  dataType: string;
  expression: string;
  comment?: string;
  nullable: boolean;
  maxLength?: number;
  scale?: number;
}

interface GenerateRequest {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  tableName: string;
  rowCount?: number;
  columnConfigs?: ColumnConfigVO[];
  batchSize?: number;
}

interface GeneratorTemplate {
  label: string;
  category: string;
  expression: string;
  example: string;
  suggestedDataType: string;
}

interface SavedConfig {
  columnName: string;
  dataType: string;
  expression: string;
  comment?: string;
  nullable: boolean;
  autoIncrement: boolean;
  maxLength?: number;
  scale?: number;
}

interface ColumnConfig {
  columnName: string;
  dataType: string;
  comment?: string;
  expression?: string;
  nullable: boolean;
  autoIncrement: boolean;
  maxLength?: number;
  scale?: number;
}

interface PreviewRow {
  [key: string]: any;
}

interface PreviewVO {
  tableName: string;
  previewData: PreviewRow[];
  columns: {
    columnName: string;
    dataType: string;
    comment?: string;
  }[];
}

const loadGeneratorTemplates = createRequest<void, GeneratorTemplate[]>('/api/rdb/table/generate-data/templates', { method: 'get' });

const loadTableColumns = createRequest<TableInfo, ColumnConfig[]>('/api/rdb/table/generate-data/config', { method: 'post' });

const loadSavedConfigs = createRequest<TableInfo, SavedConfig[]>('/api/rdb/table/generate-data/generation-rule/list', { method: 'get' });

const generatePreview = createRequest<GenerateRequest, PreviewVO>('/api/rdb/table/generate-data/preview', { method: 'post' });

const executeGeneration = createRequest<GenerateRequest, number>('/api/rdb/table/generate-data/execute', { method: 'post' });

const groupByCategory = (templates: GeneratorTemplate[]): Record<string, GeneratorTemplate[]> => {
  const groups: Record<string, GeneratorTemplate[]> = {};
  for (const t of templates) {
    if (!groups[t.category]) groups[t.category] = [];
    groups[t.category].push(t);
  }
  return groups;
};

const DataGenerationModal: React.FC = () => {
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [tableInfo, setTableInfo] = useState<IDataGenerationModalParams | null>(null);
  const [columns, setColumns] = useState<ColumnConfig[]>([]);
  const [loading, setLoading] = useState(false);
  const [templates, setTemplates] = useState<GeneratorTemplate[]>([]);
  const [templateGroups, setTemplateGroups] = useState<Record<string, GeneratorTemplate[]>>({});
  const [previewData, setPreviewData] = useState<PreviewRow[]>([]);
  const [showPreview, setShowPreview] = useState(false);
  const [progressVisible, setProgressVisible] = useState(false);
  const [taskProgress, setTaskProgress] = useState(0);
  const [taskStatus, setTaskStatus] = useState<string>('');
  const [taskName, setTaskName] = useState('');
  const [generating, setGenerating] = useState(false);
  const [logs, setLogs] = useState<string[]>([]);
  const [rowCount, setRowCount] = useState(100);
  const tableInfoRef = useRef<IDataGenerationModalParams | null>(null);
  const pollingRef = useRef<NodeJS.Timeout | null>(null);

  const addLog = (log: string) => {
    const timestamp = new Date().toLocaleTimeString();
    setLogs(prev => [...prev, `${timestamp}: ${log}`]);
  };

  const handleAiGuessExpression = useCallback(() => {
    if (!tableInfo || columns.length === 0) {
      message.warning('请先加载表列信息');
      return;
    }

    setPendingAiChat({
      dataSourceId: tableInfo.dataSourceId,
      databaseName: tableInfo.databaseName,
      schemaName: tableInfo.schemaName,
      tableNames: [tableInfo.tableName],
      message: `请为表 ${tableInfo.tableName} 的字段推荐 datafaker 表达式`,
      promptType: 'NL_2_DATA_EXPRESSION',
      onExpressionGenerated: handleExpressionGenerated,
    });
    setCurrentWorkspaceExtend('ai');
  }, [tableInfo, columns]);

  const handleExpressionGenerated = useCallback((result: IDataExpressionResult) => {
    if (!result || !result.column_expressions || result.column_expressions.length === 0) {
      message.warning('未获取到表达式推荐');
      return;
    }

    const newColumns = columns.map(col => {
      const matched = result.column_expressions.find(e => e.column_name === col.columnName);
      if (matched && matched.expression) {
        return { ...col, expression: matched.expression };
      }
      return col;
    });

    setColumns(newColumns);
    message.success(`AI 已推荐 ${result.column_expressions.length} 个字段表达式，请查看并确认`);
  }, [columns]);

  const openDataGenerationModal = useCallback((params: IDataGenerationModalParams) => {
    setOpen(true);
    setTableInfo(params);
    tableInfoRef.current = params;
    setPreviewData([]);
    setShowPreview(false);
    setProgressVisible(false);
    setTaskProgress(0);
    setLogs([]);
  }, []);

  useEffect(() => {
    setOpenDataGenerationModal(openDataGenerationModal);
  }, [openDataGenerationModal]);

  useEffect(() => {
    if (open && templates.length === 0) {
      loadGeneratorTemplates({})
        .then((res) => {
          if (res) {
            setTemplates(res);
            setTemplateGroups(groupByCategory(res));
          }
        })
        .catch(console.error);
    }
  }, [open]);

  useEffect(() => {
    if (open && tableInfo) {
      fetchTableColumns();
    }
  }, [open, tableInfo]);

  useEffect(() => {
    return () => {
      if (pollingRef.current) {
        clearInterval(pollingRef.current);
        pollingRef.current = null;
      }
    };
  }, []);

  const fetchTableColumns = async () => {
    if (!tableInfo) return;
    setLoading(true);
    try {
      const [columnsRes, savedRes] = await Promise.all([
        loadTableColumns({
          dataSourceId: tableInfo.dataSourceId,
          databaseName: tableInfo.databaseName,
          schemaName: tableInfo.schemaName,
          tableName: tableInfo.tableName,
        }),
        loadSavedConfigs({
          dataSourceId: tableInfo.dataSourceId,
          databaseName: tableInfo.databaseName,
          schemaName: tableInfo.schemaName,
          tableName: tableInfo.tableName,
        }),
      ]);

      if (columnsRes) {
        const savedMap = new Map<string, string>();
        if (savedRes) {
          for (const saved of savedRes) {
            savedMap.set(saved.columnName, saved.expression);
          }
        }
        const merged = columnsRes.map(col => ({
          ...col,
          expression: savedMap.get(col.columnName) || col.expression,
        }));
        setColumns(merged);
      }
    } catch {
      message.error('加载表列信息失败');
    } finally {
      setLoading(false);
    }
  };

  const buildColumnConfigs = (): ColumnConfigVO[] => {
    return columns.map(col => ({
      columnName: col.columnName,
      dataType: col.dataType,
      expression: col.expression || '',
      nullable: col.nullable,
      maxLength: col.maxLength,
      scale: col.scale,
    }));
  };

  const handlePreview = async () => {
    if (!tableInfo) return;
    setLoading(true);
    try {
      const count = form.getFieldValue('rowCount') || 10;
      const res = await generatePreview({
        dataSourceId: tableInfo.dataSourceId,
        databaseName: tableInfo.databaseName,
        schemaName: tableInfo.schemaName,
        tableName: tableInfo.tableName,
        rowCount: count,
        columnConfigs: buildColumnConfigs(),
      });
      if (res) {
        setPreviewData(res.previewData || []);
        setShowPreview(true);
      }
    } catch {
      message.error('生成预览失败');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = async () => {
    if (!tableInfo) return;
    const count = form.getFieldValue('rowCount') || 100;
    setRowCount(count);
    setGenerating(true);
    setProgressVisible(true);
    setTaskProgress(0);
    setTaskStatus('PROCESSING');
    setTaskName('数据生成 - ' + tableInfo.tableName);
    setLogs([]);
    addLog('任务已创建，开始生成数据...');

    try {
      const taskId = await executeGeneration({
        dataSourceId: tableInfo.dataSourceId,
        databaseName: tableInfo.databaseName,
        schemaName: tableInfo.schemaName,
        tableName: tableInfo.tableName,
        rowCount: count,
        columnConfigs: buildColumnConfigs(),
      });
      if (taskId) {
        addLog(`任务ID: ${taskId}`);
        startPolling(taskId);
      }
    } catch {
      addLog('创建任务失败');
      message.error('数据生成失败');
      setGenerating(false);
      setTaskStatus('ERROR');
    }
  };

  const startPolling = (taskId: number) => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
    }

    pollingRef.current = setInterval(async () => {
      try {
        const task = await taskService.getTask({ id: taskId });
        if (task) {
          const progress = parseInt(task.taskProgress || '0', 10);
          setTaskProgress(progress);

          if (task.taskStatus === 'FINISH') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            setTaskStatus('FINISH');
            addLog(`数据生成完成，共 ${rowCount} 行`);
            message.success('数据生成完成');
            setGenerating(false);
            setTimeout(() => {
              setProgressVisible(false);
              setOpen(false);
            }, 2000);
          } else if (task.taskStatus === 'ERROR') {
            clearInterval(pollingRef.current!);
            pollingRef.current = null;
            setTaskStatus('ERROR');
            let errorMsg = '数据生成失败';
            if (task.content) {
              errorMsg = task.content;
            }
            addLog(`错误: ${errorMsg}`);
            message.error(errorMsg);
            setGenerating(false);
          } else {
            addLog(`进度: ${progress}%`);
          }
        }
      } catch {
        addLog('查询任务状态失败');
      }
    }, 1000);
  };

  const handleCloseProgress = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
    if (!generating) {
      setProgressVisible(false);
    }
  };

  const handleTemplateChange = (columnName: string, value: string) => {
    const template = templates.find(t => `${t.category} - ${t.label}` === value);
    if (template) {
      setColumns(prev => prev.map(col =>
        col.columnName === columnName ? { ...col, expression: template.expression } : col
      ));
    }
  };

  const handleExpressionChange = (columnName: string, expression: string) => {
    setColumns(prev => prev.map(col =>
      col.columnName === columnName ? { ...col, expression } : col
    ));
  };

  const getMatchedTemplate = (expression: string | undefined): string | undefined => {
    if (!expression) return undefined;
    const matched = templates.find(t => t.expression === expression);
    return matched ? `${matched.category} - ${matched.label}` : undefined;
  };

  const tableColumns = [
    { title: '列名', dataIndex: 'columnName', key: 'columnName', width: 140 },
    { title: '数据类型', dataIndex: 'dataType', key: 'dataType', width: 100 },
    { title: '注释', dataIndex: 'comment', key: 'comment', width: 100 },
    {
      title: '预设模板',
      key: 'template',
      width: 200,
      render: (_: any, record: ColumnConfig) => {
        if (record.autoIncrement) return <span style={{ color: '#999' }}>自增列(跳过)</span>;
        return (
          <Select
            value={getMatchedTemplate(record.expression)}
            onChange={(value) => handleTemplateChange(record.columnName, value)}
            style={{ width: '100%' }}
            size="small"
            placeholder="选择模板"
            allowClear
          >
            {Object.entries(templateGroups).map(([category, items]) => (
              <Select.OptGroup key={category} label={category}>
                {items.map(item => (
                  <Option key={`${category} - ${item.label}`} value={`${category} - ${item.label}`}>
                    {item.label}
                  </Option>
                ))}
              </Select.OptGroup>
            ))}
          </Select>
        );
      },
    },
    {
      title: '表达式',
      key: 'expression',
      width: 300,
      render: (_: any, record: ColumnConfig) => {
        if (record.autoIncrement) return <span style={{ color: '#999' }}>自增列(跳过)</span>;
        return (
          <Input
            size="small"
            placeholder="#{Name.first_name}"
            value={record.expression || ''}
            onChange={(e) => handleExpressionChange(record.columnName, e.target.value)}
          />
        );
      },
    },
  ];

  const previewColumns = previewData.length > 0
    ? Object.keys(previewData[0]).map(key => ({ title: key, dataIndex: key, key, width: 120, ellipsis: true }))
    : [];

  return (
    <>
      <Modal
        title="生成数据"
        open={open}
        onCancel={() => setOpen(false)}
        width={1100}
        footer={[
          <Button key="cancel" onClick={() => setOpen(false)}>取消</Button>,
          <Button key="preview" onClick={handlePreview} loading={loading}>预览</Button>,
          <Button key="generate" type="primary" onClick={handleGenerate} loading={loading}>确定生成</Button>,
        ]}
      >
      <Form form={form} layout="vertical">
        <Form.Item label="生成行数" name="rowCount" initialValue={100}>
          <InputNumber min={1} max={100000} style={{ width: 200 }} />
        </Form.Item>

        <div style={{ 
          marginBottom: 16, 
          display: 'flex', 
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <h4 style={{ margin: 0 }}>列配置</h4>
          <Button
            type="primary"
            icon={<BulbFilled />}
            onClick={handleAiGuessExpression}
            disabled={columns.length === 0 || loading}
            size="small"
          >
            猜一猜
          </Button>
        </div>

        <Table
          columns={tableColumns}
          dataSource={columns}
          rowKey="columnName"
          pagination={false}
          size="small"
          loading={loading}
          scroll={{ y: 300 }}
        />

          {showPreview && (
            <div style={{ marginTop: 16 }}>
              <h4>预览数据（前10行）</h4>
              <Table
                columns={previewColumns}
                dataSource={previewData}
                rowKey={(record, index) => String(index)}
                pagination={false}
                size="small"
                scroll={{ x: true, y: 150 }}
              />
            </div>
          )}
        </Form>
      </Modal>

      <Modal
        title="生成进度"
        open={progressVisible}
        footer={[
          <Button key="close" onClick={handleCloseProgress} disabled={generating}>
            {generating ? '生成中...' : '关闭'}
          </Button>,
        ]}
        width={600}
        closable={false}
      >
        <div>
          <div style={{ marginBottom: 16 }}>
            <div>任务: {taskName}</div>
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
          <Progress
            percent={Math.min(taskProgress, 100)}
            status={taskStatus === 'ERROR' ? 'exception' : taskStatus === 'FINISH' ? 'success' : 'active'}
          />
          <div style={{ marginTop: 8 }}>
            已生成: {Math.floor(rowCount * taskProgress / 100)} / {rowCount} 行
          </div>
        </div>
      </Modal>
    </>
  );
};

export default DataGenerationModal;
