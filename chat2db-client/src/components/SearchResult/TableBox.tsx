import React, { useEffect, useMemo, useState } from 'react';
import { TableDataType } from '@/constants/table';
import { IManageResultData, ITableHeaderItem } from '@/typings/database';
import { formatDate } from '@/utils/date';
import { Button, message, Modal, Table } from 'antd';
import antd from 'antd';
import { BaseTable, ArtColumn, useTablePipeline, features, SortItem } from 'ali-react-table';
import Iconfont from '../Iconfont';
import classnames from 'classnames';
import StateIndicator from '../StateIndicator';
import MonacoEditor from '../Console/MonacoEditor';
import { useTheme } from '@/hooks/useTheme';
import styled from 'styled-components';
import styles from './TableBox.less';
import { ThemeType } from '@/constants';
import i18n from '@/i18n';
import { compareStrings } from '@/utils/sort';

interface ITableProps {
  className?: string;
  data: IManageResultData;
}

interface IViewTableCellData {
  name: string;
  value: any;
}
// --bgcolor: #333;
// --header-bgcolor: #45494f;
const DarkSupportBaseTable: any = styled(BaseTable)`
  &.dark {
    --bgcolor: #131418;
    --header-bgcolor: #0a0b0c;
    --hover-bgcolor: #46484a;
    --header-hover-bgcolor: #606164;
    --highlight-bgcolor: #191a1b;
    --header-highlight-bgcolor: #191a1b;
    --color: #dadde1;
    --header-color: #dadde1;
    --lock-shadow: rgb(37 37 37 / 0.5) 0 0 6px 2px;
    --border-color: transparent;
  }
`;

export default function TableBox(props: ITableProps) {
  const { className, data } = props;
  const { headerList, dataList, duration, description } = data || {};
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);
  const [appTheme] = useTheme();
  const isDarkTheme = useMemo(() => appTheme.backgroundColor === ThemeType.Dark, [appTheme]);
  // const [sorts, onChangeSorts] = useState<SortItem[]>([]);

  // useEffect(() => {
  //   const sorts: SortItem[] = (headerList || []).map((item) => ({
  //     code: item.name,
  //     order: 'none',
  //   }));
  //   onChangeSorts(sorts);
  // }, [headerList]);

  const defaultSorts: SortItem[] = useMemo(
    () =>
      (headerList || []).map((item) => ({
        code: item.name,
        order: 'none',
      })),
    [headerList],
  );

  function viewTableCell(data: IViewTableCellData) {
    setViewTableCellData(data);
  }

  function copyTableCell(data: IViewTableCellData) {
    navigator.clipboard.writeText(data?.value || viewTableCellData?.value);
    message.success(i18n('common.button.copySuccessfully'));
  }

  function handleCancel() {
    setViewTableCellData(null);
  }

  const columns: ArtColumn[] = useMemo(
    () =>
      (headerList || []).map((item, index) => {
        const { dataType, name } = item;
        const isFirstLine = index === 0;
        const isNumber = dataType === TableDataType.NUMERIC;
        return {
          code: name,
          name: name,
          key: name,
          lock: isFirstLine,
          width: 120,
          render: (value: any, row: any, rowIndex: number) => {
            return (
              <div className={styles.tableItem}>
                <div>{value}</div>
                <div className={styles.tableHoverBox}>
                  <Iconfont code="&#xe606;" onClick={viewTableCell.bind(null, { name: item.name, value })} />
                  <Iconfont code="&#xeb4e;" onClick={copyTableCell.bind(null, { name: item.name, value })} />
                </div>
              </div>
            );
          },
          features: { sortable: isNumber ? compareStrings : true },
        };
      }),
    [headerList],
  );

  const tableData = useMemo(() => {
    if (!columns?.length) {
      return [];
    } else {
      return (dataList || []).map((item, rowIndex) => {
        const rowData: any = {};
        item.map((i: string | null, index: number) => {
          const { dataType: type } = headerList[index] || {};
          if (type === TableDataType.DATETIME && i) {
            rowData[columns[index].name] = formatDate(i, 'yyyy-MM-dd hh:mm:ss');
          } else if (i === null) {
            rowData[columns[index].name] = '[null]';
          } else {
            rowData[columns[index].name] = i;
          }
        });
        rowData.key = rowIndex;
        return rowData;
      });
    }
  }, [dataList, columns]);

  const pipeline = useTablePipeline()
    .input({ dataSource: tableData, columns })
    .use(
      features.sort({
        mode: 'single',
        defaultSorts,
        // sorts,
        // onChangeSorts,
      }),
    )
    .use(
      features.columnResize({
        fallbackSize: 120,
        minSize: 60,
        maxSize: 1080,
        // handleBackground: '#ddd',
        // handleHoverBackground: '#aaa',
        // handleActiveBackground: '#89bff7',
      }),
    );

  return (
    <div className={classnames(className, styles.tableBox)}>
      {columns.length ? (
        // <Table pagination={false} columns={columns} dataSource={tableData} scroll={{ y: '100vh' }} size="small" />
        <>
          <DarkSupportBaseTable
            style={{
              '--border-color': 'transparent',
            }}
            className={classnames({ dark: isDarkTheme }, props.className, styles.table)}
            {...pipeline.getProps()}
          />
          <div className={styles.statusBar}>{`${i18n('common.text.result')}：${description}. ${i18n('common.text.timeConsuming')}：${duration}ms`}</div>
        </>
      ) : (
        <StateIndicator state="success" text={i18n('common.text.successfulExecution')} />
      )}
      <Modal
        title={viewTableCellData?.name}
        open={!!viewTableCellData?.name}
        onCancel={handleCancel}
        width="60vw"
        maskClosable={false}
        footer={
          <>
            {
              <Button onClick={copyTableCell.bind(null, viewTableCellData!)} className={styles.cancel}>
                {i18n('common.button.copy')}
              </Button>
            }
          </>
        }
      >
        <div className={styles.monacoEditor}>
          <MonacoEditor
            id="view_table-Cell_data"
            appendValue={
              {
                text: viewTableCellData?.value,
                range: 'reset',
              }
            }
            options={{
              readOnly: true,
            }}
          />
        </div>
      </Modal>
    </div>
  );
}
