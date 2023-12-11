import React, { memo, useEffect, useMemo } from 'react';
import i18n from '@/i18n';
import styles from './index.less';
import classnames from 'classnames';
import { Table, Dropdown, Input, Pagination, ConfigProvider } from 'antd';
import { DatabaseTypeCode, TreeNodeType, OperationColumn, WorkspaceTabType } from '@/constants';
import sqlServer from '@/service/sql';
import type { ColumnsType } from 'antd/es/table';
import { IPageParams } from '@/typings';
import { v4 as uuid } from 'uuid';

// ----- components -----
import Iconfont from '@/components/Iconfont';
import { getRightClickMenu } from '@/blocks/Tree/hooks/useGetRightClickMenu';
import MenuLabel from '@/components/MenuLabel';
import ViewDDL from '@/components/ViewDDL';

// ----- store -----
import { addWorkspaceTab } from '@/pages/main/workspace/store/console';

const { Search } = Input;

interface IProps {
  className?: string;
  uniqueData: {
    dataSourceId: string;
    dataSourceName: string;
    databaseType: DatabaseTypeCode;
    databaseName?: string;
    schemaName?: string;
  };
}

export default memo<IProps>((props) => {
  const { className, uniqueData } = props;
  const [tableData, setTableData] = React.useState<any[] | null>(null);
  const [tableLoading, setTableLoading] = React.useState<boolean>(false);
  const tableBoxRef = React.useRef<HTMLDivElement>(null);
  const [allTableWidth, setAllTableWidth] = React.useState(0);
  const [allTableHeight, setAllTableHeight] = React.useState(0);
  // 选中表
  const [activeId, setActiveId] = React.useState<string>('');
  const [tableDataTotal, setTableDataTotal] = React.useState(0);
  const [currentPageNo, setCurrentPageNo] = React.useState(1);
  const [openDropdown, setOpenDropdown] = React.useState<boolean | undefined>(undefined);
  const [dropdownItems, setDropdownItems] = React.useState<any[]>([]);
  const [viewDDLSql, setViewDDLSql] = React.useState<string>('');

  useEffect(() => {
    getTable({
      pageNo: 1,
      pageSize: 1000,
    });
  }, []);

  useEffect(() => {
    if (openDropdown === false) {
      setOpenDropdown(undefined);
    }
  }, [openDropdown]);

  const getTable = (params: IPageParams) => {
    setCurrentPageNo(params.pageNo);
    setTableLoading(true);
    sqlServer
      .getTableList({
        ...props.uniqueData,
        ...(params || {}),
      } as any)
      .then((res) => {
        setTableDataTotal(res.total);
        const data = res.data.map((t) => {
          const key = uuid();
          return {
            uuid: key,
            name: t.name,
            treeNodeType: TreeNodeType.TABLE,
            key: t.name,
            pinned: t.pinned,
            comment: t.comment,
            extraParams: {
              ...uniqueData,
              tableName: t.name,
            },
          };
        });
        setTableData(data);
      })
      .finally(() => {
        setTableLoading(false);
      });
  };

  const paginationChange = (pageNo: number) => {
    getTable({
      pageNo,
      pageSize: 1000,
    });
  };

  const createTable = () => {
    addWorkspaceTab({
      id: uuid(),
      title: i18n('editTable.button.createTable'),
      type: WorkspaceTabType.CreateTable,
      uniqueData: {
        ...props.uniqueData,
      },
    });
  };

  const getDropdownsItems = (record) => {
    const rightClickMenu = getRightClickMenu({
      treeNodeData: record,
      loadData: () => {},
    });
    const dropdownsItems: any = rightClickMenu.map((item) => {
      return {
        key: item.key,
        type: item.type,
        onClick: () => {
          setOpenDropdown(false);
          item.onClick(record);
        },
        label: <MenuLabel icon={item.labelProps.icon} label={item.labelProps.label} />,
      };
    });

    const excludeList = [
      OperationColumn.OpenTable,
      OperationColumn.CreateConsole,
      // OperationColumn.Pin,
      OperationColumn.ViewDDL,
      OperationColumn.EditTable,
      OperationColumn.CopyName,
    ];

    return dropdownsItems.filter((item) => excludeList.includes(item.type));
  };

  const renderCell = (text, record) => {
    return (
      <div className={classnames(styles.tableCell, { [styles.activeTableCell]: activeId === record.key })}>
        {text}
      </div>
    );
  };

  const columns: ColumnsType<any> = [
    {
      title: 'Table name',
      dataIndex: 'name',
      key: 'name',
      render: renderCell,
    },
    {
      title: 'Comment',
      dataIndex: 'comment',
      key: 'comment',
      render: renderCell,
    },
  ];

  // 监听allTable的高度的变化
  useEffect(() => {
    const resizeObserver = new ResizeObserver((entries) => {
      const { width, height } = entries[0].contentRect;
      setAllTableWidth(width);
      setAllTableHeight(height);
    });
    resizeObserver.observe(tableBoxRef.current!);
  }, []);

  // 监听allTable的宽度的变化
  useEffect(() => {
    const resizeObserver = new ResizeObserver((entries) => {
      const { width, height } = entries[0].contentRect;
      setAllTableWidth(width);
      setAllTableHeight(height);
    });
    resizeObserver.observe(tableBoxRef.current!);
  }, []);

  useEffect(() => {
      const record = tableData?.find((t) => t.key === activeId);
      if (record) {
        sqlServer
          .exportCreateTableSql({
            ...uniqueData,
            tableName: record.name,
          } as any)
          .then((res) => {
            setViewDDLSql(res);
          });
      }
  
  }, [activeId]);

  const onSearch = (value: string) => {
    getTable({
      pageNo: 1,
      pageSize: 1000,
      searchKey: value,
    });
  };

  return (
    // <ConfigProvider
    //   theme={{
    //     token: {
    //       motion: false,
    //     },
    //   }}
    // >
    <div className={classnames(styles.allTable, className)}>
      <div className={styles.headerBox}>
        <div className={styles.headerBoxLeft}>
          <Iconfont code="&#xe726;" box boxSize={24} onClick={createTable} />
          <Iconfont
            onClick={() => {
              getTable({
                pageNo: 1,
                pageSize: 1000,
                refresh: true,
              });
            }}
            code="&#xe668;"
            box
            boxSize={24}
          />
        </div>
        <div className={styles.headerBoxRight}>
          <Search size="small" placeholder={i18n('common.text.search')} onSearch={onSearch} style={{ width: 150 }} />
        </div>
      </div>
      <div className={styles.contentCenter}>
        <Dropdown
          open={openDropdown}
          menu={{
            items: dropdownItems,
          }}
          trigger={['contextMenu']}
          onOpenChange={(_open) => {
            setOpenDropdown(_open);
          }}
        >
          <div ref={tableBoxRef} className={styles.tableBox}>
            <Table
              loading={tableLoading}
              onRow={(row) => {
                return {
                  onClick: () => {
                    setActiveId(row.key);
                  },
                  onContextMenu: (event) => {
                    event.preventDefault();
                    setActiveId(row.key);
                    setOpenDropdown(true);
                    setDropdownItems(getDropdownsItems(tableData?.find((t) => t.key === row.key)));
                  },
                };
              }}
              virtual
              scroll={{ x: allTableWidth - 10, y: allTableHeight - 25 }}
              columns={columns}
              pagination={false}
              dataSource={tableData || []}
            />
          </div>
        </Dropdown>
        <div className={styles.viewDDLBox}>
          <div className={styles.viewDDLHeader}>
            DDL
          </div>
          <ViewDDL className={styles.viewDDL} sql={viewDDLSql} />
        </div>
      </div>
      {/* {tableDataTotal > 1000 && (
      )} */}
      <div className={styles.pagingBox}>
        <Pagination
          onChange={paginationChange}
          showSizeChanger={false}
          current={currentPageNo}
          pageSize={1000}
          total={tableDataTotal}
        />
      </div>
    </div>
  );
});
