import { IChartItem, IChartType, IConnectionDetails } from '@/typings';
import React, { useEffect, useRef, useState } from 'react';
import styles from './index.less';
import addImage from '@/assets/img/add.svg';
import cs from 'classnames';
import Line from '../chart/line';
import Pie from '../chart/pie';
import Bar from '../chart/bar';
import { MoreOutlined } from '@ant-design/icons';
import { Button, Cascader, Dropdown, Form, MenuProps, Select } from 'antd';
import { initChartItem } from '..';
import { deleteChart, getChartById } from '@/service/dashboard';
import { data } from '../../../../../mock/sqlResult.json';
import Console from '@/components/Console';
import Iconfont from '@/components/Iconfont';
import sqlService, { MetaSchemaVO } from '@/service/sql';
import { Option } from '@/typings/common';
import { handleDatabaseAndSchema } from '@/utils/database';

const handleSQLResult2ChartData = (data) => {
  const { headerList, dataList } = data;
  const mockData = headerList.reduce((acc, cur, index) => {
    acc[cur.name] = {
      ...cur,
      data: dataList.map((i: any) => i[index]),
    };
    return acc;
  }, {});
  return mockData;
};

function countArrayElements<T>(arr: T[]): { name: T; value: number }[] {
  const counts = new Map<T, number>();
  // 统计每个元素出现的次数
  arr.forEach((item) => {
    if (counts.has(item)) {
      counts.set(item, counts.get(item)! + 1);
    } else {
      counts.set(item, 1);
    }
  });
  // 转换为数组形式
  const result: { name: T; value: number }[] = [];
  for (const [key, value] of counts.entries()) {
    result.push({ name: key, value });
  }
  return result;
}

interface IChartItemProps {
  id: number;
  canAddRowItem: boolean;
  connectionList: IConnectionDetails[];
  onDelete?: (id: number) => void;
  addChartTop?: () => void;
  addChartBottom?: () => void;
  addChartLeft?: () => void;
  addChartRight?: () => void;
}

function ChartItem(props: IChartItemProps) {
  const { connectionList } = props;
  const [cascaderOption, setCascaderOption] = useState<Option[]>([]);
  const [curConnection, setCurConnection] = useState<IConnectionDetails>();
  const [consoleParams, setConsoleParams] = useState({});

  const [chartData, setChartData] = useState<IChartItem>();
  const [chartMetaData, setChartMetaData] = useState<any>();
  const [consoleValue, setConsoleValue] = useState<string>();
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [toggle, setToggle] = useState(false);
  const [form] = Form.useForm(); // 创建一个表单实例
  const chartRef = useRef<any>();
  // const consoleParams = useRef({});
  const { id } = props;

  useEffect(() => {
    queryChartData();
  }, [id]);

  useEffect(() => {
    if (connectionList && connectionList.length > 0) {
      setCurConnection(connectionList[0]);
      setCascaderOption(
        (connectionList || []).map((c) => ({
          value: c.id,
          label: c.alias,
          isLeaf: false,
        })),
      );
    }
  }, [connectionList]);

  useEffect(() => {
    if (!curConnection) {
      return;
    }
    setConsoleParams({
      ...consoleParams,
      dataSourceId: curConnection.id,
      type: curConnection.type,
    });

    queryDatabaseAndSchemaList(curConnection.id);
  }, [curConnection]);

  const queryDatabaseAndSchemaList = async (dataSourceId: number) => {
    const res = await sqlService.getDatabaseSchemaList({ dataSourceId });
    const dataSource = (cascaderOption || []).find((c) => c.value === dataSourceId);
    if (!dataSource) return;

    dataSource.children = handleDatabaseAndSchema(res);
    setCascaderOption([...cascaderOption]);
  };

  const queryChartData = async () => {
    const { id } = props;
    let res = await getChartById({ id });
    setChartData(res);
  };

  const renderPlusIcon = () => {
    return (
      <>
        {props.canAddRowItem && (
          <div onClick={props.addChartLeft} className={styles.left_overlay_add}>
            <div className={styles.add_chart_icon}>
              <img className={styles.add_chart_plus_icon} src={addImage} alt="Add chart" />
            </div>
          </div>
        )}
        {props.canAddRowItem && (
          <div onClick={props.addChartRight} className={styles.right_overlay_add}>
            <div className={styles.add_chart_icon}>
              <img className={styles.add_chart_plus_icon} src={addImage} alt="Add chart" />
            </div>
          </div>
        )}
        <div onClick={props.addChartTop} className={styles.top_overlay_add}>
          <div className={cs(styles.add_chart_icon, styles.add_chart_icon_y)}>
            <img className={styles.add_chart_plus_icon} src={addImage} alt="Add chart" />
          </div>
        </div>
        <div onClick={props.addChartBottom} className={styles.bottom_overlay_add}>
          <div className={cs(styles.add_chart_icon, styles.add_chart_icon_y)}>
            <img className={styles.add_chart_plus_icon} src={addImage} alt="Add chart" />
          </div>
        </div>
      </>
    );
  };

  const renderChart = () => {
    // const { chartType } = chartData || {};
    // const { chartType } = JSON.parse(schema) || {};
    const { chartType } = form.getFieldsValue(true);
    switch (chartType) {
      case IChartType.Pie:
        return <Pie ref={chartRef} data={chartMetaData} />;
      case IChartType.Line:
        return <Line ref={chartRef} data={chartMetaData} />;
      case IChartType.Column:
        return <Bar ref={chartRef} data={chartMetaData} />;
      default:
        return (
          <div style={{ height: '120px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <Iconfont code="&#xe638;" className={styles.emptyDataImage} />
          </div>
        );
    }
  };

  const onExport2Image = () => {
    const echartInstance = chartRef.current.getEchartsInstance();
    let img = new Image();
    img.src = echartInstance.getDataURL({
      type: 'png',
      devicePixelRatio: 4,
      backgroundColor: '#FFF',
    });
    img.onload = function () {
      let canvas = document.createElement('canvas');
      canvas.width = img.width;
      canvas.height = img.height;
      let ctx = canvas.getContext('2d');
      ctx?.drawImage(img, 0, 0);
      let dataURL = canvas.toDataURL('image/png');

      var a = document.createElement('a');
      let event = new MouseEvent('click');
      a.download = 'image.png';
      a.href = dataURL;
      a.dispatchEvent(event);
    };
  };

  const onDeleteChart = () => {
    const { id } = props;
    deleteChart({ id });
    props.onDelete && props.onDelete(id);
  };

  const renderEmptyBlock = () => {
    return (
      <div className={styles.emptyChartBlock}>
        <Iconfont code="&#xe638;" className={styles.emptyDataImage} />

        <div className={styles.emptyDataText}>No date selected</div>
        <Button
          type="primary"
          onClick={() => {
            setIsEditing(true);
          }}
        >
          Add Data
        </Button>
      </div>
    );
  };

  const renderEditorBlock = () => {
    const { sqlData = {} } = chartData || {};
    const options = Object.keys(sqlData).map((i) => ({ label: i, value: i }));

    return (
      <div className={styles.editorBlock}>
        <div className={styles.editor}>
          <Console
            executeParams={consoleParams}
            hasAiChat={true}
            hasAi2Lang={false}
            hasSaveBtn={false}
            value={consoleValue}
            onExecuteSQL={(result) => {
              console.log('onExecuteSQL', result);
              setChartData({
                ...chartData,
                sqlData: handleSQLResult2ChartData(result[0]),
              });
              // setResultData(result);
            }}
          />

          <Cascader
            options={cascaderOption}
            onChange={(value, selectedOptions) => {
              console.log('onChange', selectedOptions);
              (selectedOptions || []).forEach((o) => {
                if (o.type) {
                  setConsoleParams({
                    ...consoleParams,
                    [`${o.type}Name`]: o.value,
                  });
                }
              });
            }}
            className={styles.dataSourceSelect}
            // style={{ width: '100%' }}
          />
        </div>
        <Form
          form={form}
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 12 }}
          layout="horizontal"
          className={styles.chartParamsForm}
          autoComplete="off"
          onValuesChange={handleChartConfigChange}
        >
          <Form.Item label={'Chart Type'} name={'chartType'}>
            <Select
              options={[
                { label: 'Line', value: 'Line' },
                { label: 'Pie', value: 'Pie' },
                { label: 'Column', value: 'Column' },
              ]}
            />
          </Form.Item>
          <Form.Item label={'xAxis'} name={'xAxis'}>
            <Select options={options} />
          </Form.Item>
          <Form.Item label={'yAxis'} name={'yAxis'} hidden={form.getFieldValue('chartType') === IChartType.Pie}>
            <Select options={options} />
          </Form.Item>
        </Form>
      </div>
    );
  };

  const handleChartConfigChange = () => {
    const { sqlData = {} } = chartData || {};
    const { chartType, xAxis, yAxis } = form.getFieldsValue(true);
    // let xAxisOptions: Array<{ label: string; value: string }> = [];
    // let yAxisOptions: Array<{ label: string; value: string }> = [];

    if (chartType === IChartType.Pie) {
      const dimension = sqlData[xAxis];
      const { data = [] } = dimension || {};
      const finallyData = countArrayElements(data);
      setChartMetaData(finallyData);
    } else if (chartType === IChartType.Line) {
      const dimensionX = sqlData[xAxis]?.data;
      const dimensionY = sqlData[yAxis]?.data;
      setChartMetaData({
        xAxis: dimensionX,
        yAxis: dimensionY,
      });
    } else if (chartType === IChartType.Column) {
      const dimensionX = sqlData[xAxis]?.data;
      const dimensionY = sqlData[yAxis]?.data;
      setChartMetaData({
        xAxis: dimensionX,
        yAxis: dimensionY,
      });
    } else {
    }
  };

  const handleToggleData = () => {
    if (toggle) {
      setChartData({
        ...chartData,
        sqlData: undefined,
      });
    } else {
      const mockData = handleSQLResult2ChartData(data[0]);
      setChartData({
        ...chartData,
        sqlData: mockData,
      });
    }

    setToggle(!toggle);
  };

  return (
    <div className={styles.container}>
      {renderPlusIcon()}
      <div className={styles.titleBar}>
        <div className={styles.title}>{chartData?.name}</div>
        <div>
          {/* <Button type="text" onClick={handleToggleData}>
            {toggle ? '去掉数据' : '添加数据'}
          </Button> */}
          <Dropdown
            menu={{
              items: [
                {
                  key: 'Export',
                  label: 'Export to image',
                  onClick: onExport2Image,
                },
                {
                  key: 'delete',
                  label: 'Delete',
                  onClick: onDeleteChart,
                },
              ],
            }}
            placement="bottomLeft"
          >
            <MoreOutlined className={styles.edit} />
          </Dropdown>
        </div>
      </div>
      {chartData?.sqlData || isEditing ? renderChart() : renderEmptyBlock()}
      {isEditing && renderEditorBlock()}
      <div>{props.id}</div>
    </div>
  );
}

export default ChartItem;
