import React, { useEffect, useState } from 'react';
import styles from './index.less';
import MonacoEditor from '@/components/Console/MonacoEditor';
import { Tabs } from 'antd';

export enum CustomerTypeEnum {
  visitor = 'visitor',
  person = 'person',
}

export enum CustomerTypeEnum2 {
  visitor = 'visitor',
  person = 'person',
}

const list = [
  "select if((`performance_schema`.`accounts`.`HOST` is null),'background',`performance_schema`.`accounts`.`HOST`) AS `host`,sum(`sys`.`stmt`.`total`) AS `statements`,format_pico_time(sum(`sys`.`stmt`.`total_latency`)) AS `statement_latency`,format_pico_time(ifnull((sum(`sys`.`stmt`.`total_latency`) / nullif(sum(`sys`.`stmt`.`total`),0)),0)) AS `statement_avg_latency`,sum(`sys`.`stmt`.`full_scans`) AS `table_scans`,sum(`sys`.`io`.`ios`) AS `file_ios`,format_pico_time(sum(`sys`.`io`.`io_latency`)) AS `file_io_latency`,sum(`performance_schema`.`accounts`.`CURRENT_CONNECTIONS`) AS `current_connections`,sum(`performance_schema`.`accounts`.`TOTAL_CONNECTIONS`) AS `total_connections`,count(distinct `performance_schema`.`accounts`.`USER`) AS `unique_users`,format_bytes(sum(`sys`.`mem`.`current_allocated`)) AS `current_memory`,format_bytes(sum(`sys`.`mem`.`total_allocated`)) AS `total_memory_allocated` from (((`performance_schema`.`accounts` join `sys`.`x$host_summary_by_statement_latency` `stmt` on((`performance_schema`.`accounts`.`HOST` = `sys`.`stmt`.`host`))) join `sys`.`x$host_summary_by_file_io` `io` on((`performance_schema`.`accounts`.`HOST` = `sys`.`io`.`host`))) join `sys`.`x$memory_by_host_by_current_bytes` `mem` on((`performance_schema`.`accounts`.`HOST` = `sys`.`mem`.`host`))) group by if((`performance_schema`.`accounts`.`HOST` is null),'background',`performance_schema`.`accounts`.`HOST`)",
  'Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！Bug: chatglm3 不支持！',
  '建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名建议: 鼠标移上去显示表全名，目前表名太长，看不全表名',
];

export type CustomerType1 = CustomerTypeEnum | CustomerTypeEnum2;

const App: React.FC = () => {
  const items = [
    {
      key: '1',
      label: 'Tab 1',
      forceRender: true,
      children:<div className={styles.page}>
        <MonacoEditor id="001" defaultValue={list[0]} />
      </div>
    },
    {
      key: '2',
      label: 'Tab 2',
      forceRender: true,
      children: <div className={styles.page}>
        <MonacoEditor id="002" defaultValue={list[1]} />
      </div>
    },
    {
      key: '3',
      label: 'Tab 3',
      forceRender: true,
      children: 
      <div className={styles.page}>
        <MonacoEditor id="003" defaultValue={list[2]} />
      </div>
    },
  ];

  return (
   
      <Tabs items={items} />
  );
};

export default App;
