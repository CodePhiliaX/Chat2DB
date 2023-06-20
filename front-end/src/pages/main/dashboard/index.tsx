import React, { useEffect, useState } from 'react';
import { connect, Dispatch } from 'umi';
import cs from 'classnames';
import { IChartType, IDashboardItem, IChartDataItem, IChartDataSortItem } from '@/typings/dashboard';
import DraggableContainer from '@/components/DraggableContainer';
import Iconfont from '@/components/Iconfont';
import ChartItem from './chart-item';
import { Button, Form, Input, Modal } from 'antd';
import { ReactSortable, Store } from 'react-sortablejs';
import { GlobalState } from '@/models/global';
import { createDashboard, getDashboardList, updateDashboard } from '@/service/dashboard';
import { EditOutlined } from '@ant-design/icons';
import styles from './index.less';

interface IProps {
  className?: string;
  setting: GlobalState['settings'];
  dispatch: Dispatch;
}

const initChartItemData: IChartDataItem = {
  sqlContext: 'sqlContext',
  sqlData: 'aa',
  chatType: IChartType.Line,
  chatParam: {
    x: '',
    y: '',
  },
};

function Chart(props: IProps) {
  const { className } = props;

  const [dataList, setDataList] = useState<IDashboardItem[]>([]);
  const [curItem, setCurItem] = useState<IDashboardItem>();
  const [openAddDashboard, setOpenAddDashboard] = useState(false);
  const [form] = Form.useForm(); // 创建一个表单实例

  useEffect(() => {
    // 获取列表数据
    queryDashboardList();
    console.log('chart', props);
  }, []);

  const queryDashboardList = async () => {
    let res = await getDashboardList({});
    const { data } = res;
    if (Array.isArray(data) && data.length > 0) {
      setDataList(data);
      setCurItem(data[0]);
    }
  };

  const renderContent = () => {
    const { data, name } = curItem || {};
    if (!data) return;

    const sortData = (data || []).reduce((acc: IChartDataSortItem[], cur, i) => {
      const tmp = (cur || []).map((c, ii) => ({ id: `${i}_${ii}`, ...c }));
      acc.push(...tmp);
      return acc;
    }, []);

    console.log(sortData, 'sortData');
    return (
      <>
        <div className={styles.boxRightTitle}>
          <Iconfont code="&#xe60d;" />
          <div style={{ marginLeft: '8px' }}>{name}</div>
        </div>

        <div className={styles.BoxRightContent}>
          {/* <ReactSortable
            list={sortData}
            setList={(newState: IChatDataSortItem[], sortable: any, store: Store) => {
              // throw new Error('Function not implemented.');
            }}
            onAdd={() => {}}
          > */}
          {data.map((rowData, rowIndex) => (
            <div key={rowIndex} className={styles.boxRightContentRow}>
              {rowData.map((item, colIndex) => (
                <div className={styles.boxRightContentColumn} style={{ width: `${100 / rowData.length}%` }}>
                  <ChartItem
                    id={`${rowIndex}_${colIndex}`}
                    index={colIndex}
                    key={`${rowIndex}_${colIndex}`}
                    data={item}
                    connections={[]}
                    canAddRowItem={rowData.length < 3}
                    addChartTop={() => {
                      data.splice(rowIndex, 0, [initChartItemData]);
                      setDataList([...dataList]);
                    }}
                    addChartBottom={() => {
                      data.splice(rowIndex + 1, 0, [initChartItemData]);
                      setDataList([...dataList]);
                    }}
                    addChartLeft={() => {
                      rowData.splice(colIndex, 0, initChartItemData);
                      setDataList([...dataList]);
                    }}
                    addChartRight={() => {
                      rowData.splice(colIndex + 1, 0, initChartItemData);
                      setDataList([...dataList]);
                    }}
                    onDelete={() => {
                      if (rowData.length === 1) {
                        data.splice(rowIndex, 1);
                        setDataList([...dataList]);
                      } else {
                        rowData.splice(colIndex, 1);
                        setDataList([...dataList]);
                      }
                    }}
                  />
                </div>
              ))}
            </div>
          ))}
          {/* </ReactSortable> */}
        </div>
      </>
    );
  };

  return (
    <>
      <DraggableContainer layout="row" className={cs(styles.box, className)}>
        <div className={styles.boxLeft}>
          <div className={styles.boxLeftTitle}>Dashboard</div>
          <Button className={styles.createDashboardBtn} type="primary" onClick={() => setOpenAddDashboard(true)}>
            Create Dashboard
          </Button>
          {(dataList || []).map((i, index) => (
            <div
              key={index}
              className={cs({ [styles.boxLeftItem]: true, [styles.activeItem]: curItem?.id === i.id })}
              onClick={() => setCurItem(i)}
            >
              <div>{i.name}</div>
              <EditOutlined
                className={styles.boxLeftItemIcon}
                onClick={() => {
                  const { id, name, description } = i;
                  setOpenAddDashboard(true);
                  form.setFieldsValue({
                    id,
                    name,
                    description,
                  });
                }}
              />
            </div>
          ))}
          {/* 
        <Button
          onClick={() => {
            props.dispatch({
              type: 'global/updateSettings',
              payload: {
                theme: 'dark',
                language: 'en',
              },
            });
          }}
        >
          测试dva
        </Button> */}
        </div>
        <div className={styles.box_right}>{renderContent()}</div>
      </DraggableContainer>

      <Modal
        title={form.getFieldValue('id') ? 'Edit Dashboard' : 'Add Dashboard'}
        open={openAddDashboard}
        onOk={async () => {
          try {
            const values = await form.validateFields();
            console.log('Success:', values);
            const formValue = form.getFieldsValue(true);
            const { id } = formValue;

            if (id) {
              await updateDashboard(formValue);
            } else {
              await createDashboard(formValue);
            }
            queryDashboardList();
            setOpenAddDashboard(false);
            form.setFieldsValue({});
          } catch (errorInfo) {
            console.log('Failed:', errorInfo);
          }
        }}
        onCancel={() => {
          setOpenAddDashboard(false);
          form.setFieldsValue({});
        }}
        okText="Confirm"
        cancelText="Cancel"
      >
        <Form form={form} autoComplete={'off'}>
          <Form.Item label={'name'} name={'name'} rules={[{ required: true, message: 'Please input your name' }]}>
            <Input />
          </Form.Item>
          <Form.Item label={'description'} name={'description'}>
            <Input.TextArea />
          </Form.Item>
          {/* <Form.Item>
            <Button type="primary" onClick={onCheck}>
              Check
            </Button>
          </Form.Item> */}
        </Form>
      </Modal>
    </>
  );
}

export default connect(({ global }: { global: GlobalState }) => ({
  settings: global.settings,
}))(Chart);
