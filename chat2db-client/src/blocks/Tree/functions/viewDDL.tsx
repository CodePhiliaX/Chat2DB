// 置顶表格
import React from 'react';
import mysqlService from '@/service/sql';

import { openModal } from '@/store/common/components';

import MonacoEditor from '@/components/MonacoEditor';

export const viewDDL = (treeNodeData) => {
  openModal({
    title: '查看DDL',
    width: '60%',
    footer: false,
    content: <MonacoEditor id="edit-dialog" />,
  });
};
