import React, { memo, useEffect, useState, useCallback } from 'react';
import i18n from '@/i18n';
import styles from './index.less';
import classnames from 'classnames';
import { Table, Dropdown, Input, Pagination, Button, Form, Modal, message, Spin } from 'antd';
import { DatabaseTypeCode, TreeNodeType, OperationColumn, WorkspaceTabType } from '@/constants';
import sqlServer, { IBatchModifyTableSqlParams } from '@/service/sql';
import type { ColumnsType } from 'antd/es/table';
import { IPageParams } from '@/typings';
import { v4 as uuid } from 'uuid';
import ExecuteSQL from '@/components/ExecuteSQL';

// ----- components -----
import Iconfont from '@/components/Iconfont';
import { getRightClickMenu } from '@/blocks/Tree/hooks/useGetRightClickMenu';
import MenuLabel from '@/components/MenuLabel';
import { setCurrentWorkspaceGlobalExtend, setPendingAiChat, setCurrentWorkspaceExtend, IBatchTableCommentResult } from '@/pages/main/workspace/store/common';
import { deprecatedTable } from '@/blocks/Tree/functions/deprecatedTable';

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
  const { className, uniqueData, } = props;
  const [tableData, setTableData] = useState<any[] | null>(null);
  const [tableLoading, setTableLoading] = useState<boolean>(false);
  const tableBoxRef = React.useRef<HTMLDivElement>(null);
  const [allTableWidth, setAllTableWidth] = useState(0);
  const [allTableHeight, setAllTableHeight] = useState(0);
  const [activeId, setActiveId] = useState<string>('');
  const [tableDataTotal, setTableDataTotal] = useState(0);
  const [currentPageNo, setCurrentPageNo] = useState(1);
  const [openDropdown, setOpenDropdown] = useState<boolean | undefined>(undefined);
  const [dropdownItems, setDropdownItems] = useState<any[]>([]);
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [viewSqlModal, setViewSqlModal] = useState<boolean>(false);
  const [appendValue, setAppendValue] = useState<string>('');
  const [guessLoading, setGuessLoading] = useState<boolean>(false);
  const [form] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

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
            rowCount: t.rowCount,
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
      loadData: () => {
        getTable({
          pageNo: currentPageNo,
          pageSize: 1000,
        });
      },
    });
    const buildMenuItem = (item: any): any => {
      const menuItem: any = {
        key: item.key,
        type: item.type,
        label: <MenuLabel icon={item.labelProps.icon} label={item.labelProps.label} />,
      };
      if (item.children && item.children.length > 0) {
        menuItem.children = item.children.map(buildMenuItem);
      } else {
        menuItem.onClick = () => {
          setOpenDropdown(false);
          item.onClick(record);
        };
      }
      return menuItem;
    };
    const dropdownsItems: any = rightClickMenu.map(buildMenuItem);

    const excludeList: any[] = [
      OperationColumn.OpenTable,
      OperationColumn.CreateConsole,
      // OperationColumn.Pin,
      OperationColumn.ViewDDL,
      OperationColumn.EditTable,
      OperationColumn.CopyName,
      OperationColumn.DeprecatedTable,
      OperationColumn.TruncateTable,
      'dataOperation', // 数据操作（二级菜单：导入/导出/生成数据）
    ];

    return dropdownsItems.filter((item) => excludeList.includes(item.type));
  };

  const renderCell = (text, record, dataIndex) => {
    if (dataIndex === 'name') {
      return (
        <div className={classnames(styles.tableCell, { [styles.activeTableCell]: activeId === record.key })}>
          {text}
        </div>
      );
    }

    if (dataIndex === 'rowCount') {
      return (
        <div className={classnames(styles.tableCell, { [styles.activeTableCell]: activeId === record.key })}>
          {formatRowCount(text)}
        </div>
      );
    }

    return isEditing ? (
      <Form.Item
        name={[record.key, dataIndex]}
        style={{ margin: 0 }}
        rules={dataIndex === 'comment' ? [] : [{ required: true, message: `${dataIndex} is required.` }]}
      >
        <Input />
      </Form.Item>
    ) : (
      <div className={classnames(styles.tableCell, { [styles.activeTableCell]: activeId === record.key })}>{text}</div>
    );
  };

  const formatRowCount = (count?: number) => {
    if (count == null || count < 0) return '-';
    if (count >= 1000000) return `${(count / 1000000).toFixed(1)}M`;
    if (count >= 1000) return `${(count / 1000).toFixed(1)}K`;
    return count.toString();
  };

  const columns: ColumnsType<any> = [
    {
      title: 'Table name',
      dataIndex: 'name',
      key: 'name',
      sorter: true,
      render: (text, record) => renderCell(text, record, 'name'),
    },
    {
      title: 'Row Count',
      dataIndex: 'rowCount',
      key: 'rowCount',
      width: 120,
      sorter: true,
      render: (text, record) => renderCell(text, record, 'rowCount'),
    },
    {
      title: 'Comment',
      dataIndex: 'comment',
      key: 'comment',
      render: (text, record) => renderCell(text, record, 'comment'),
    },
  ];

  const startEditing = () => {
    form.setFieldsValue(
      tableData?.reduce((acc, record) => {
        acc[record.key] = record;
        return acc;
      }, {})
    );
    setIsEditing(true);
  };

  const cancelEditing = () => {
    setIsEditing(false);
  };

  const saveAll = async () => {
    try {
      if (tableData && uniqueData.databaseName) {
        const values = await form.validateFields();
        const newData = tableData.map((item) => ({
          ...item,
          ...values[item.key],
        }));
        setIsEditing(false);
        const params: IBatchModifyTableSqlParams = {
          databaseName: uniqueData.databaseName,
          dataSourceId: uniqueData.dataSourceId,
          schemaName: uniqueData.schemaName,
          refresh: true,
          oldTables: tableData,
          newTables: newData,
        };
        // 调用批量获取修改表的SQL语句的API
        sqlServer.getBatchModifyTableSql(params).then(res => {
          setViewSqlModal(true); // 显示SQL预览Modal
          setAppendValue(res?.join('\n'));
        })
      }

    } catch (errInfo) {
      console.log('Validate Failed:', errInfo);
    }
  };
  const executeSuccessCallBack = () => {
    setViewSqlModal(false);
    message.success(i18n('common.text.successfulExecution'));
    // 保存成功后，刷新左侧树
    getTable({
      refresh: true,
      pageNo: currentPageNo,
      pageSize: 1000,
    });
  };

  // 监听tableBox的尺寸变化
  useEffect(() => {
    const resizeObserver = new ResizeObserver((entries) => {
      const { width, height } = entries[0].contentRect;
      setAllTableWidth(width);
      setAllTableHeight(height);
    });
    if (tableBoxRef.current) {
      resizeObserver.observe(tableBoxRef.current);
    }
    return () => resizeObserver.disconnect();
  }, []);

  const onSearch = (value: string) => {
    getTable({
      pageNo: 1,
      pageSize: 1000,
      searchKey: value,
    });
  };

  const handleTableChange = (pagination: any, filters: any, sorter: any) => {
    const params: any = {
      pageNo: 1,
      pageSize: 1000,
    };
    
    if (sorter.field) {
      params.sortField = sorter.field;
      params.sortOrder = sorter.order === 'ascend' ? 'ascend' : 'descend';
    }
    
    getTable(params);
  };

  const batchDeprecatedTable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning(i18n('common.viewAllTable.noSelectedTables'));
      return;
    }

    Modal.confirm({
      title: i18n('common.viewAllTable.batchDeprecatedConfirmTitle'),
      content: i18n('common.viewAllTable.batchDeprecatedConfirmContent', selectedRowKeys.length),
      okText: i18n('common.button.confirm'),
      cancelText: i18n('common.button.cancel'),
      onOk: async () => {
        const selectedTables = tableData?.filter((item) => selectedRowKeys.includes(item.key)) || [];
        let successCount = 0;
        let failCount = 0;

        for (const table of selectedTables) {
          try {
            await sqlServer.deprecatedTable({
              dataSourceId: uniqueData.dataSourceId,
              databaseName: uniqueData.databaseName,
              schemaName: uniqueData.schemaName,
              tableName: table.name,
            });
            successCount++;
          } catch (error) {
            failCount++;
            console.error(`Failed to deprecate table ${table.name}:`, error);
          }
        }

        setSelectedRowKeys([]);
        
        if (failCount === 0) {
          message.success(i18n('common.viewAllTable.batchDeprecatedSuccess', successCount));
        } else {
          message.warning(i18n('common.viewAllTable.batchDeprecatedPartialSuccess', successCount, failCount));
        }

        getTable({
          pageNo: currentPageNo,
          pageSize: 1000,
        });
      },
    });
  };

  const batchOptimizeTable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning(i18n('common.viewAllTable.noSelectedTablesForOptimize'));
      return;
    }

    Modal.confirm({
      title: i18n('common.viewAllTable.batchOptimizeConfirmTitle'),
      content: i18n('common.viewAllTable.batchOptimizeConfirmContent', selectedRowKeys.length),
      okText: i18n('common.button.confirm'),
      cancelText: i18n('common.button.cancel'),
      onOk: async () => {
        const selectedTables = tableData?.filter((item) => selectedRowKeys.includes(item.key)) || [];
        const tableNames = selectedTables.map(t => t.name);

        try {
          const results = await sqlServer.batchOptimizeTables({
            dataSourceId: Number(uniqueData.dataSourceId),
            databaseName: uniqueData.databaseName,
            schemaName: uniqueData.schemaName,
            tableNames,
          });

          const successCount = results.filter((r: any) => r.success).length;
          const failCount = results.length - successCount;

          setSelectedRowKeys([]);
          
          if (failCount === 0) {
            message.success(i18n('common.viewAllTable.batchOptimizeSuccess', successCount));
          } else {
            message.warning(i18n('common.viewAllTable.batchOptimizePartialSuccess', successCount, failCount));
          }

          getTable({
            pageNo: currentPageNo,
            pageSize: 1000,
          });
        } catch (error) {
          message.error(i18n('common.viewAllTable.batchOptimizePartialSuccess', 0, tableNames.length));
          console.error('Failed to optimize tables:', error);
        }
      },
    });
  };

  const batchAnalyzeTable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning(i18n('common.viewAllTable.noSelectedTablesForAnalyze'));
      return;
    }

    Modal.confirm({
      title: i18n('common.viewAllTable.batchAnalyzeConfirmTitle'),
      content: i18n('common.viewAllTable.batchAnalyzeConfirmContent', selectedRowKeys.length),
      okText: i18n('common.button.confirm'),
      cancelText: i18n('common.button.cancel'),
      onOk: async () => {
        const selectedTables = tableData?.filter((item) => selectedRowKeys.includes(item.key)) || [];
        const tableNames = selectedTables.map(t => t.name);

        try {
          const results = await sqlServer.batchAnalyzeTables({
            dataSourceId: Number(uniqueData.dataSourceId),
            databaseName: uniqueData.databaseName,
            schemaName: uniqueData.schemaName,
            tableNames,
          });

          const successCount = results.filter((r: any) => r.success).length;
          const failCount = results.length - successCount;

          setSelectedRowKeys([]);
          
          if (failCount === 0) {
            message.success(i18n('common.viewAllTable.batchAnalyzeSuccess', successCount));
          } else {
            message.warning(i18n('common.viewAllTable.batchAnalyzePartialSuccess', successCount, failCount));
          }

          getTable({
            pageNo: currentPageNo,
            pageSize: 1000,
          });
        } catch (error) {
          message.error(i18n('common.viewAllTable.batchAnalyzePartialSuccess', 0, tableNames.length));
          console.error('Failed to analyze tables:', error);
        }
      },
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: React.Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
    columnWidth: 32,
    getCheckboxProps: (record: any) => ({
      disabled: false,
    }),
  };

  const pendingBatchesRef = React.useRef<string[][]>([]);

  const handleBatchCommentGenerated = useCallback((result: IBatchTableCommentResult) => {
    if (result.tables && result.tables.length > 0) {
      message.success(i18n('common.text.aiCommentGenerated'));
      
      const commentMap = new Map<string, string>();
      result.tables.forEach((t) => {
        commentMap.set(t.table_name, t.table_comment);
      });
      
      setTableData((prevData) => {
        if (!prevData) return prevData;
        const newData = prevData.map((item) => {
          const newComment = commentMap.get(item.name);
          if (newComment) {
            return { ...item, comment: newComment };
          }
          return item;
        });
        
        form.setFieldsValue(
          newData.reduce((acc, record) => {
            acc[record.key] = record;
            return acc;
          }, {})
        );
        
        return newData;
      });
    }

    if (pendingBatchesRef.current.length > 0) {
      const nextBatch = pendingBatchesRef.current.shift()!;
      setTimeout(() => {
        setPendingAiChat({
          dataSourceId: Number(uniqueData.dataSourceId),
          databaseName: uniqueData.databaseName,
          schemaName: uniqueData.schemaName,
          tableNames: nextBatch,
          message: '请为这些表生成合适的中文注释',
          promptType: 'NL_2_COMMENT_BATCH',
          onBatchCommentGenerated: handleBatchCommentGenerated,
        });
        setCurrentWorkspaceExtend('ai');
      }, 500);
    }
  }, [form, uniqueData]);

  const openAiChatForGuess = useCallback(() => {
    if (!tableData || tableData.length === 0) {
      message.warning(i18n('common.text.noTables'));
      return;
    }
    const tableNamesWithoutComment = tableData
      .filter((t) => !t.comment || !t.comment.trim())
      .map((t) => t.name);
    if (tableNamesWithoutComment.length === 0) {
      message.warning(i18n('common.text.allTablesHaveComments'));
      return;
    }
    const BATCH_SIZE = 20;
    const batches: string[][] = [];
    for (let i = 0; i < tableNamesWithoutComment.length; i += BATCH_SIZE) {
      batches.push(tableNamesWithoutComment.slice(i, i + BATCH_SIZE));
    }
    pendingBatchesRef.current = batches.slice(1);
    const firstBatch = batches[0];

    setGuessLoading(true);
    setPendingAiChat({
      dataSourceId: Number(uniqueData.dataSourceId),
      databaseName: uniqueData.databaseName,
      schemaName: uniqueData.schemaName,
      tableNames: firstBatch,
      message: '请为这些表生成合适的中文注释',
      promptType: 'NL_2_COMMENT_BATCH',
      onBatchCommentGenerated: handleBatchCommentGenerated,
    });
    setCurrentWorkspaceExtend('ai');
    setGuessLoading(false);
    message.success('已切换到 AI 助手，请在 AI 聊天面板中查看推荐结果');
  }, [tableData, uniqueData, handleBatchCommentGenerated]);

  return (
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
          {isEditing ? (
            <>
              <Spin spinning={guessLoading}>
                <Button onClick={openAiChatForGuess} style={{ marginLeft: 8 }}>
                  {i18n('common.button.guess')}
                </Button>
              </Spin>
              <Button onClick={saveAll} style={{ marginLeft: 8 }}>
                {i18n('common.button.saveAll')}
              </Button>
              <Button onClick={cancelEditing} style={{ marginLeft: 8 }}>
                {i18n('common.button.cancel')}
              </Button>
            </>
          ) : (
            <>
              <Button
                onClick={batchDeprecatedTable}
                disabled={selectedRowKeys.length === 0}
                style={{ marginLeft: 8 }}
              >
                {i18n('common.viewAllTable.batchDeprecated')}
              </Button>
              <Button
                onClick={batchOptimizeTable}
                disabled={selectedRowKeys.length === 0}
                style={{ marginLeft: 8 }}
              >
                {i18n('common.viewAllTable.batchOptimize')}
              </Button>
              <Button
                onClick={batchAnalyzeTable}
                disabled={selectedRowKeys.length === 0}
                style={{ marginLeft: 8 }}
              >
                {i18n('common.viewAllTable.batchAnalyze')}
              </Button>
              <Button onClick={startEditing} style={{ marginLeft: 8 }}>
                {i18n('common.button.editAll')}
              </Button>
            </>
          )}
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
            <Form form={form} component={false}>
              <Table
                loading={tableLoading}
                rowSelection={rowSelection}
                onChange={handleTableChange}
                onRow={(row) => {
                  return {
                    onClick: () => {
                      setActiveId(row.key);
                      setCurrentWorkspaceGlobalExtend({
                        code: 'viewDDL',
                        uniqueData: {
                          ...uniqueData,
                          tableName: row.name,
                        }
                      });
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
            </Form>
          </div>
        </Dropdown>
      </div>
      <div className={styles.pagingBox}>
        <Pagination
          onChange={paginationChange}
          showSizeChanger={false}
          current={currentPageNo}
          pageSize={1000}
          total={tableDataTotal}
        />
      </div>
      <Modal
        title={i18n('editTable.title.sqlPreview')}
        open={!!viewSqlModal} // 控制Modal显示
        onCancel={() => setViewSqlModal(false)} // 关闭Modal
        width="60vw"
        maskClosable={false}
        footer={false}
        destroyOnHidden={true}
      >
        <ExecuteSQL
          initSql={appendValue}
          databaseName={uniqueData.databaseName}
          dataSourceId={uniqueData.dataSourceId}
          schemaName={uniqueData.schemaName}
          databaseType={uniqueData.databaseType}
          executeSuccessCallBack={executeSuccessCallBack}
        />
      </Modal>
    </div>
  );
});
