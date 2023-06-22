import React, { forwardRef, useImperativeHandle, useMemo, useRef } from 'react';
import * as charts from 'echarts';
import ReactEcharts from 'echarts-for-react';
import './index.less';
type EChartsOption = charts.EChartsOption;

interface IProps {
  data: {
    xAxis: string[];
    yAxis: any[];
  };
}

const LineChart = (props: IProps, ref) => {
  const lineRef = useRef<any>(null);
  const option: EChartsOption = useMemo(
    () => ({
      xAxis: {
        type: 'category',
        data: props?.data?.xAxis ?? [],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          data: props?.data?.yAxis ?? [],
          type: 'line',
        },
      ],
      tooltip: {
        trigger: 'axis',
      },
    }),
    [props.data],
  );

  useImperativeHandle(ref, () => ({
    getEchartsInstance: () => lineRef.current.getEchartsInstance(),
  }));

  return <ReactEcharts ref={lineRef} option={option} opts={{ renderer: 'svg' }} />;
};

export default forwardRef(LineChart);
