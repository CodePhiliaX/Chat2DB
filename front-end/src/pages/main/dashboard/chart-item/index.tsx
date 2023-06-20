import { IChartData, IChartDataItem, IChartType } from '@/typings/dashboard';
import React, { useRef, useState } from 'react';
import styles from './index.less';
import addImage from '@/assets/img/add.svg';
import cs from 'classnames';
import EchartsTest from '../echart-test';
import Line from '../chart/line';
import Pie from '../chart/pie';
import Bar from '../chart/bar';
import { DashOutlined } from '@ant-design/icons';
import { Dropdown, MenuProps } from 'antd';
interface IChartItemProps {
  id: string;
  index: number;
  data: IChartDataItem;
  connections: Array<any>;
  canAddRowItem: boolean;

  onDelete?: () => void;
  addChartTop?: () => void;
  addChartBottom?: () => void;
  addChartLeft?: () => void;
  addChartRight?: () => void;
}

const defaultData: IChartDataItem = {
  sqlContext: '',
  sqlData: '',
  chartType: IChartType.Line,
  chartParam: {},
};

function ChartItem(props: IChartItemProps) {
  const [data, setData] = useState<IChartDataItem>(defaultData);
  const [isEditing, setIsEditing] = useState();
  const chartRef = useRef<any>();

  const renderLeftAndRightPlusIcon = () => {
    return (
      props.canAddRowItem && (
        <>
          <div onClick={props.addChartLeft} className={styles.left_overlay_add}>
            <div className={styles.add_chart_icon}>
              <img className={styles.add_chart_plus_icon} src={addImage} alt="Add chart" />
            </div>
          </div>

          <div onClick={props.addChartRight} className={styles.right_overlay_add}>
            <div className={styles.add_chart_icon}>
              <img className={styles.add_chart_plus_icon} src={addImage} alt="Add chart" />
            </div>
          </div>
        </>
      )
    );
  };

  const renderTopAndBottomPlusIcon = () => {
    return (
      <>
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
    const { chartType } = data;
    switch (chartType) {
      case IChartType.Pie:
        return (
          <Pie
            ref={chartRef}
            data={[
              {
                value: 6,
                name: '男',
              },
              {
                value: 4,
                name: '女',
              },
            ]}
          />
        );
      case IChartType.Line:
        return <Line ref={chartRef} />;
      case IChartType.Column:
        return <Bar ref={chartRef} />;
      default:
        return null;
    }
  };

  const export2Image = () => {
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

  return (
    <div className={styles.container}>
      {renderLeftAndRightPlusIcon()}
      {renderTopAndBottomPlusIcon()}
      <div className={styles.title_bar}>
        <div className={styles.title}>{IChartType[data?.chartType]}</div>

        <Dropdown
          menu={{
            items: [
              {
                key: '1',
                label: 'Export to image',
                onClick: export2Image,
              },
            ],
          }}
          placement="bottomLeft"
        >
          <DashOutlined className={styles.edit} />
        </Dropdown>
      </div>

      <div>{renderChart()}</div>

      <div>数据区块{props.id}</div>

      <div></div>
    </div>
  );
}

export default ChartItem;
