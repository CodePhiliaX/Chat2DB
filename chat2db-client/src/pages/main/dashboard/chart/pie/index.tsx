import React, { ForwardedRef, LegacyRef, useImperativeHandle, useMemo, useRef } from 'react';
import * as charts from 'echarts';
import ReactEcharts from 'echarts-for-react';
import './index.less';
import { Button } from 'antd';
import { forwardRef } from 'react';
type EChartsOption = charts.EChartsOption;

interface IProps {
  data: Array<{ value: number; name: string }>;
}

const PieChart = (props: IProps, ref: ForwardedRef<{ getEchartsInstance: Function }>) => {
  const pieRef = useRef<any>(null);

  const option: EChartsOption = useMemo(
    () => ({
      tooltip: {
        trigger: 'item',
      },
      legend: {
        orient: 'horizontal',
        align: 'auto',
        type: 'scroll', //分页类型
      },


      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          data: props.data,
          // label: {
          //   show: false,
          //   position: 'center'
          // },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
        },
      ],

    }),
    [props.data],
  );

  useImperativeHandle(ref, () => ({
    getEchartsInstance: () => pieRef.current.getEchartsInstance(),
  }));

  return <ReactEcharts ref={pieRef} option={option} opts={{ renderer: 'svg' }} />;
};

export default forwardRef(PieChart);
