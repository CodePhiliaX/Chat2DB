import React, { useEffect, useState, useRef } from 'react';
import { Dropdown, Form, Input, Modal, message } from 'antd';
import cs from 'classnames';
import { IChartItem, IDashboardItem } from '@/typings';
import DraggableContainer from '@/components/DraggableContainer';
import Iconfont from '@/components/Iconfont';
import ChartItem from './chart-item';
// import { ReactSortable, Store } from 'react-sortablejs';
import {
  createChart,
  createDashboard,
  deleteDashboard,
  getDashboardById,
  getDashboardList,
  updateDashboard,
} from '@/service/dashboard';
import i18n from '@/i18n';
import styles from './index.less';
// import { IConnectionModelState } from '@/models/connection';
// import { IWorkspaceModelState } from '@/models/workspace';
// import { IAIState } from '@/models/ai';
import { useConnectionStore } from '../store/connection';
import { useSettingStore } from '@/store/setting';

interface IProps {
  className?: string;
}

export const initChartItem: IChartItem = {
  name: '',
  description: '',
  // chatType: IChartType.Line,
  schema: '{"chatType":"","xAxis":"","yAxis":""}',
};

function Chart(props: IProps) {
  const { className } = props;
  const [dashboardList, setDashboardList] = useState<IDashboardItem[]>([]);
  const [curDashboard, setCurDashboard] = useState<IDashboardItem>();
  const [openAddDashboard, setOpenAddDashboard] = useState(false);
  const connectionList = useConnectionStore((state) => state.connectionList);
  const remainingUse = useSettingStore((state) => state.remainingUse);

  const [form] = Form.useForm(); // 创建一个表单实例
  const [messageApi] = message.useMessage();
  const draggableRef = useRef<any>();

  useEffect(() => {
    // 获取列表数据
    queryDashboardList();
  }, []);

  useEffect(() => {
    const { chartIds } = curDashboard || {};
    if (!curDashboard) {
      return;
    }
    if (!chartIds || !chartIds.length) {
      initCreateChart(curDashboard);
    }
  }, [curDashboard]);

  const queryDashboardList = async () => {
    const res = await getDashboardList({});
    const { data } = res;
    if (Array.isArray(data) && data.length > 0) {
      setDashboardList(data);
      const _curDashboard = await getDashboardById({ id: data[0].id });
      setCurDashboard(_curDashboard);
    }
  };

  const initCreateChart = async (dashboard?: IDashboardItem) => {
    if (!dashboard) return;

    const chartId = await createChart({});
    const newDashboard = {
      ...dashboard,
      schema: JSON.stringify([[chartId]]),
      chartIds: [chartId],
    };
    updateDashboard(newDashboard);
    setCurDashboard(newDashboard);
  };

  const onClickDashboardItem = async (dashboard: IDashboardItem) => {
    const { id } = dashboard;
    if (curDashboard?.id === id) {
      return;
    }
    const res = await getDashboardById({ id });
    setCurDashboard(res);
  };

  const renderLeft = () =>
    (dashboardList || []).map((i, index) => (
      <div
        key={index}
        className={cs({ [styles.boxLeftItem]: true, [styles.activeItem]: curDashboard?.id === i.id })}
        onClick={() => onClickDashboardItem(i)}
      >
        <div className={styles.itemTitle}>
          <Iconfont code="&#xe60d;" style={{ marginRight: '8px' }} />
          {i.name}
        </div>
        <Dropdown
          menu={{
            items: [
              {
                key: 'Edit',
                label: i18n('dashboard.edit'),
                onClick: () => {
                  const { id, name, description } = i;
                  setOpenAddDashboard(true);
                  form.setFieldsValue({
                    id,
                    name,
                    description,
                  });
                },
              },
              {
                key: 'Delete',
                label: i18n('dashboard.delete'),
                onClick: async () => {
                  const { id } = i;
                  await deleteDashboard({ id });
                  messageApi.open({
                    type: 'success',
                    content: 'delete dashboard success.',
                  });
                  queryDashboardList();
                },
              },
            ],
          }}
        >
          <div className={styles.moreButton}>
            <Iconfont code="&#xe601;" />
          </div>
        </Dropdown>
      </div>
    ));

  const onAddChart = async (type: 'top' | 'bottom' | 'left' | 'right', rowIndex: number, colIndex: number) => {
    const { id, schema, chartIds = [] } = curDashboard || {};

    const chartList: number[][] = JSON.parse(schema || '') || [[]];
    const chartId = await createChart({});
    switch (type) {
      case 'top':
        chartList.splice(rowIndex, 0, [chartId]);
        break;
      case 'bottom':
        chartList.splice(rowIndex + 1, 0, [chartId]);
        break;
      case 'left':
        chartList[rowIndex].splice(colIndex, 0, chartId);
        break;
      case 'right':
        chartList[rowIndex].splice(colIndex + 1, 0, chartId);
        break;
      default:
        break;
    }

    const newDashboard = {
      ...curDashboard,
      id: id!,
      schema: JSON.stringify(chartList),
      chartIds: [...chartIds, chartId],
    };
    await updateDashboard(newDashboard);
    setCurDashboard(newDashboard);
  };

  const onDeleteChart = async (chartId: number, rowIndex: number, colIndex: number) => {
    const { id, schema, chartIds } = curDashboard || {};

    const chartList: number[][] = JSON.parse(schema || '') || [[]];
    if (chartList[rowIndex].length === 1) {
      chartList.splice(rowIndex, 1);
    } else {
      chartList[rowIndex].splice(colIndex, 1);
    }

    const newDashboard = {
      id: id!,
      ...curDashboard,
      schema: JSON.stringify(chartList),
      chartIds: chartIds?.filter((_id) => _id !== chartId),
    };
    await updateDashboard(newDashboard);
    setCurDashboard(newDashboard);
  };

  const renderContent = () => {
    const { schema, name } = curDashboard || {};
    if (!schema) return;

    const chartList = JSON.parse(schema);

    return (
      <>
        <div className={styles.boxRightTitle}>
          <Iconfont code="&#xe60d;" />
          <div style={{ marginLeft: '8px' }}>{name}</div>
        </div>

        <div className={styles.BoxRightContent}>
          {chartList.map((rowData: number[], rowIndex: number) => (
            <div key={rowIndex} className={styles.boxRightContentRow}>
              {rowData.map((chartId: number, colIndex: number) => (
                <div
                  key={colIndex}
                  className={styles.boxRightContentColumn}
                  style={{ width: `${100 / rowData.length}%` }}
                >
                  <ChartItem
                    id={chartId}
                    key={chartId}
                    canAddRowItem={rowData.length < 3}
                    addChartTop={() => onAddChart('top', rowIndex, colIndex)}
                    addChartBottom={() => onAddChart('bottom', rowIndex, colIndex)}
                    addChartLeft={() => onAddChart('left', rowIndex, colIndex)}
                    addChartRight={() => onAddChart('right', rowIndex, colIndex)}
                    onDelete={(id: number) => onDeleteChart(id, rowIndex, colIndex)}
                    connectionList={connectionList || []}
                    remainingUse={remainingUse}
                  />
                </div>
              ))}
            </div>
          ))}
        </div>
      </>
    );
  };

  return (
    <>
      <DraggableContainer className={cs(styles.box, className)}>
        <div ref={draggableRef} className={styles.dragBox}>
          <div className={styles.boxLeft}>
            <div className={styles.boxLeftTitle}>
              <div>{i18n('dashboard.title')}</div>
              <Iconfont code="&#xe631;" className={styles.plusIcon} onClick={() => setOpenAddDashboard(true)} />
            </div>
            {renderLeft()}
          </div>
        </div>
        <div className={styles.boxRight}>{renderContent()}</div>
      </DraggableContainer>

      <Modal
        title={form.getFieldValue('id') ? i18n('dashboard.modal.editTitle') : i18n('dashboard.modal.addTitle')}
        open={openAddDashboard}
        onOk={async () => {
          try {
            await form.validateFields();
            const formValue = form.getFieldsValue(true);
            const { id } = formValue;

            if (id) {
              await updateDashboard(formValue);
            } else {
              await createDashboard(formValue);
            }
            queryDashboardList();
            setOpenAddDashboard(false);
            form.resetFields();
          } catch (errorInfo) {
            form.resetFields();
          }
        }}
        onCancel={() => {
          setOpenAddDashboard(false);
          form.resetFields();
        }}
        okText={i18n('common.button.confirm')}
        cancelText={i18n('common.button.cancel')}
      >
        <Form labelCol={{ span: 4 }} wrapperCol={{ span: 20 }} form={form} autoComplete={'off'}>
          <Form.Item
            label={'name'}
            name={'name'}
            rules={[{ required: true, message: i18n('dashboard.modal.name.placeholder') }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label={'description'} name={'description'}>
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}

export default Chart;
