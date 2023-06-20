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
        orient: 'vertical',
        right: 'right',
        top: 'center',
      },
      // color:['#45C2E0', '#C1EBDD', '#FFC851','#5A5476','#1869A0','#FF9393'],
      series: [
        {
          // name: 'Access From',
          type: 'pie',
          radius: ['40%', '70%'],
          // avoidLabelOverlap: false,
          // label: {
          //   show: false,
          //   position: 'center',
          // },
          // emphasis: {
          //   label: {
          //     show: true,
          //     fontSize: 20,
          //     fontWeight: 'bold',
          //   },
          // },
          // labelLine: {
          //   show: false,
          // },
          data: props.data,
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
