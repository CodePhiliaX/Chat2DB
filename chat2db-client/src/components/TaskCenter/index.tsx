import React, { useEffect, useState, useRef } from 'react';
import { Table, Tag, Button, Progress, Tooltip, Empty } from 'antd';
import { DownloadOutlined, SyncOutlined } from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';

import taskService, { ITask } from '@/service/task';
import i18n from '@/i18n';

import styles from './index.less';

const statusMap: Record<string, { color: string; text: string }> = {
  INIT: { color: 'default', text: i18n('workspace.taskCenter.status.pending') },
  RUNNING: { color: 'processing', text: i18n('workspace.taskCenter.status.running') },
  FINISH: { color: 'success', text: i18n('workspace.taskCenter.status.finish') },
  ERROR: { color: 'error', text: i18n('workspace.taskCenter.status.error') },
};

const typeMap: Record<string, string> = {
  DOWNLOAD_TABLE_DATA: i18n('workspace.taskCenter.type.export'),
  UPLOAD_TABLE_DATA: i18n('workspace.taskCenter.type.import'),
  EXECUTE_SQL: i18n('workspace.taskCenter.type.executeSql'),
  GENERATE_DATA: i18n('workspace.taskCenter.type.generateData'),
  DOWNLOAD_TABLE_STRUCTURE: i18n('workspace.taskCenter.type.exportSchema'),
};

const TaskCenter: React.FC = () => {
  const [tasks, setTasks] = useState<ITask[]>([]);
  const [loading, setLoading] = useState(false);
  const pollingRef = useRef<NodeJS.Timeout | null>(null);

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const result: any = await taskService.getTaskList({});
      if (result?.data && Array.isArray(result.data)) {
        setTasks(result.data);
      } else if (Array.isArray(result)) {
        setTasks(result);
      } else {
        setTasks([]);
      }
    } catch (error) {
      console.error('Failed to fetch tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
    pollingRef.current = setInterval(fetchTasks, 3000);
    return () => {
      if (pollingRef.current) {
        clearInterval(pollingRef.current);
      }
    };
  }, []);

  const handleDownload = (taskId: number) => {
    const downloadUrl = `${window._BaseURL}/api/task/download/${taskId}`;
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const columns: ColumnsType<ITask> = [
    {
      title: i18n('workspace.taskCenter.table'),
      dataIndex: 'taskName',
      key: 'taskName',
      ellipsis: true,
      render: (text) => (
        <Tooltip title={text}>
          <span className={styles.taskName}>{text || '-'}</span>
        </Tooltip>
      ),
    },
    {
      title: i18n('workspace.taskCenter.type'),
      dataIndex: 'taskType',
      key: 'taskType',
      width: 120,
      render: (type) => <Tag color="blue">{typeMap[type] || type}</Tag>,
    },
    {
      title: i18n('workspace.taskCenter.status'),
      dataIndex: 'taskStatus',
      key: 'taskStatus',
      width: 100,
      render: (status) => {
        const config = statusMap[status] || { color: 'default', text: status };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: i18n('workspace.taskCenter.progress'),
      dataIndex: 'taskProgress',
      key: 'taskProgress',
      width: 150,
      render: (progress: string, record: ITask) => {
        const percent = record.taskStatus === 'FINISH' ? 100 : parseInt(progress || '0', 10);
        return (
          <div className={styles.progressCell}>
            <Progress
              percent={isNaN(percent) ? 0 : Math.min(percent, 100)}
              size="small"
              status={record.taskStatus === 'ERROR' ? 'exception' : record.taskStatus === 'FINISH' ? 'success' : 'active'}
            />
          </div>
        );
      },
    },
    {
      title: i18n('workspace.taskCenter.database'),
      dataIndex: 'databaseName',
      key: 'databaseName',
      width: 120,
      ellipsis: true,
      render: (text) => text || '-',
    },
    {
      title: i18n('workspace.taskCenter.dataSource'),
      dataIndex: 'dataSourceId',
      key: 'dataSourceId',
      width: 100,
      render: (id) => id ? `#${id}` : '-',
    },
    {
      title: i18n('workspace.taskCenter.download'),
      key: 'action',
      width: 100,
      render: (_, record: ITask) => (
        <Button
          type="link"
          size="small"
          icon={<DownloadOutlined />}
          disabled={record.taskStatus !== 'FINISH' || !record.downloadUrl}
          onClick={() => handleDownload(record.id)}
        >
          {i18n('workspace.taskCenter.download')}
        </Button>
      ),
    },
  ];

  return (
    <div className={styles.taskCenter}>
      <div className={styles.header}>
        <h3 className={styles.title}>{i18n('workspace.taskCenter.title')}</h3>
        <Button
          type="text"
          size="small"
          icon={<SyncOutlined spin={loading} />}
          onClick={fetchTasks}
          disabled={loading}
        >
          {i18n('workspace.taskCenter.refresh')}
        </Button>
      </div>
      <div className={styles.content}>
        <Table
          columns={columns}
          dataSource={tasks}
          rowKey="id"
          loading={loading}
          size="small"
          pagination={false}
          locale={{
            emptyText: (
              <Empty
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description={i18n('workspace.taskCenter.empty')}
              />
            ),
          }}
        />
      </div>
    </div>
  );
};

export default TaskCenter;
