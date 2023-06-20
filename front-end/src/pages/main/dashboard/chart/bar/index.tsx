import React, { forwardRef, useImperativeHandle, useRef } from 'react';
import * as charts from 'echarts';
import ReactEcharts from 'echarts-for-react';
import './index.less';
type EChartsOption = charts.EChartsOption;

const BarChart = (props, ref) => {
  const barRef = useRef<any>(null);
  const getOption = () => {
    const option: EChartsOption = {
      xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          data: [120, 200, 150, 80, 70, 110, 130],
          type: 'bar',
        },
      ],
    };
    return option;
  };
  useImperativeHandle(ref, () => ({
    getEchartsInstance: () => barRef.current.getEchartsInstance(),
  }));
  return <ReactEcharts ref={barRef} option={getOption()} opts={{ renderer: 'svg' }} />;
};

export default forwardRef(BarChart);
