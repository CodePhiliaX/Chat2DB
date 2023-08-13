import React, { useMemo, useState } from 'react';
import { Button, Dropdown, MenuProps, message, Modal, Space } from 'antd';
import { BaseTable, ArtColumn, useTablePipeline, features, SortItem } from 'ali-react-table';
import styled from 'styled-components';
import classnames from 'classnames';
import i18n from '@/i18n';
import { ThemeType } from '@/constants';
import { TableDataType } from '@/constants/table';
import { useTheme } from '@/hooks/useTheme';
import { IManageResultData, IResultConfig } from '@/typings/database';
import { compareStrings } from '@/utils/sort';
import { DownOutlined, UserOutlined } from '@ant-design/icons';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';
import { formatDate } from '@/utils/date';
import { copy } from '@/utils';
import Iconfont from '../../Iconfont';
import StateIndicator from '../../StateIndicator';
import MonacoEditor from '../../Console/MonacoEditor';
import MyPagination from '../Pagination';
import styles from './index.less';

interface ITableProps {
  className?: string;
  data: IManageResultData;
  config: IResultConfig;
  onConfigChange: (config: IResultConfig) => void;
  onSearchTotal: () => Promise<number | undefined>;
  onExport: (sql: string, originalSql: string, exportType: ExportTypeEnum, exportSize: ExportSizeEnum) => void;
}

interface IViewTableCellData {
  name: string;
  value: any;
}

const SupportBaseTable: any = styled(BaseTable)`
  &.supportBaseTable {
    --bgcolor: var(--color-bg-base);
    --header-bgcolor: var(--color-bg-elevated);
    --hover-bgcolor: var(--color-hover-bg);
    --header-hover-bgcolor: var(--color-hover-bg);
    --highlight-bgcolor: var(--color-hover-bg);
    --header-highlight-bgcolor: var(--color-hover-bg);
    --color: var(--color-text);
    --header-color: var(--color-text);
    --lock-shadow: rgb(37 37 37 / 0.5) 0 0 6px 2px;
    --border-color: var(--color-border-secondary);
  }
`;

export default function TableBox(props: ITableProps) {
  const { className, data, config, onConfigChange, onSearchTotal } = props;
  const { headerList, dataList, duration, description, sqlType } = data || {};
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);
  const [appTheme] = useTheme();
  const isDarkTheme = useMemo(() => appTheme.backgroundColor === ThemeType.Dark, [appTheme]);
  const [messageApi, contextHolder] = message.useMessage();

  const handleExport = (exportType: ExportTypeEnum, exportSize: ExportSizeEnum) => {
    props.onExport && props.onExport(data.sql, data.originalSql, exportType, exportSize);
  };

  const items: MenuProps['items'] = useMemo(
    () => [
      {
        label: i18n('workspace.table.export.all.csv'),
        key: '1',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.CSV, ExportSizeEnum.ALL);
        },
      },
      {
        label: i18n('workspace.table.export.all.insert'),
        key: '2',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.INSERT, ExportSizeEnum.ALL);
        },
      },
      {
        label: i18n('workspace.table.export.cur.csv'),
        key: '3',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.CSV, ExportSizeEnum.CURRENT_PAGE);
        },
      },
      {
        label: i18n('workspace.table.export.cur.insert'),
        key: '4',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.INSERT, ExportSizeEnum.CURRENT_PAGE);
        },
      },
    ],
    [data],
  );

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
    copy(data?.value || viewTableCellData?.value);
    messageApi.success(i18n('common.button.copySuccessfully'));
  }

  function handleCancel() {
    setViewTableCellData(null);
  }

  const columns: ArtColumn[] = useMemo(
    () =>
      (headerList || []).map((item, index) => {
        const { dataType, name } = item;
        const isNumber = dataType === TableDataType.NUMERIC;
        const isNumericalOrder = dataType === TableDataType.CHAT2DB_ROW_NUMBER;
        if (isNumericalOrder) {
          return {
            code: 'No.',
            name: 'No.',
            key: name,
            lock: true,
            width: 48,
            features: { sortable: compareStrings },
            render: (value: any) => {
              return (
                <div className={styles.tableItem}>
                  <div>{value}</div>
                </div>
              );
            },
          };
        }
        return {
          code: name,
          name: name,
          key: name,
          width: 120,
          render: (value: any, row: any, rowIndex: number) => {
            return (
              <div className={styles.tableItem}>
                {value}
                <div className={styles.tableHoverBox}>
                  <Iconfont code="&#xe606;" onClick={viewTableCell.bind(null, { name: item.name, value })} />
                  <Iconfont code="&#xeb4e;" onClick={copyTableCell.bind(null, { name: item.name, value })} />
                </div>
              </div>
            );
          },
          // 如果是数字类型，因为后端返回的都是字符串，所以需要调用字符串对比函数来判断
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
            rowData[columns[index].name] = '<null>';
          } else {
            rowData[columns[index].name] = i;
          }
        });
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
        highlightColumnWhenActive: true,
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

  const onPageNoChange = (pageNo: number) => {
    onConfigChange && onConfigChange({ ...config, pageNo });
  };
  const onPageSizeChange = (pageSize: number) => {
    onConfigChange && onConfigChange({ ...config, pageSize, pageNo: 1 });
  };

  const onClickTotalBtn = async () => {
    if (props.onSearchTotal) {
      return await props.onSearchTotal();
    }
  };
  const renderContent = () => {
    const bottomStatus = (
      <div className={styles.statusBar}>
        <span>{`【${i18n('common.text.result')}】${description}.`}</span>
        <span>{`【${i18n('common.text.timeConsuming')}】${duration}ms.`}</span>
        <span>{`【${i18n('common.text.searchRow')}】${tableData.length} ${i18n('common.text.row')}.`}</span>
      </div>
    );

    if (!columns.length) {
      return (
        <>
          <StateIndicator state="success" text={i18n('common.text.successfulExecution')} />
          <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0 }}>{bottomStatus}</div>
        </>
      );
    } else {
      return (
        <>
          <div className={styles.toolBar}>
            <div className={styles.toolBarItem}>
              <MyPagination
                data={config}
                onPageNoChange={onPageNoChange}
                onPageSizeChange={onPageSizeChange}
                onClickTotalBtn={onClickTotalBtn}
              />
            </div>

            <Dropdown menu={{ items }}>
              <Button>
                <Space>
                  {i18n('common.text.export')}
                  <DownOutlined />
                </Space>
              </Button>
            </Dropdown>
          </div>
          <SupportBaseTable
            className={classnames('supportBaseTable', props.className, styles.table)}
            components={{ EmptyContent: () => <h2>{i18n('common.text.noData')}</h2> }}
            isStickyHead
            stickyTop={31}
            {...pipeline.getProps()}
          />
          {bottomStatus}
        </>
      );
    }
  };
  return (
    <div className={classnames(className, styles.tableBox)}>
      {renderContent()}
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
            appendValue={{
              text: viewTableCellData?.value,
              range: 'reset',
            }}
            options={{
              readOnly: true,
            }}
          />
        </div>
      </Modal>
      {contextHolder}
    </div>
  );
}
