import React, { memo, useState, useEffect, useRef, useContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Cascader, Divider } from 'antd';
import connectionService from '@/service/connection';
import historyService from '@/service/history';
import { treeConfig } from '../Tree/treeConfig';
import Tree from '../Tree';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { TreeNodeType } from '@/constants/tree';
import { ITreeNode } from '@/typings/tree';
import { useReducerContext } from '../../index';
import { workspaceActionType } from '../../context';
import i18n from '@/i18n';
import { IConsole } from '@/typings/common'
interface IProps {
  className?: string;
}

export default memo<IProps>(function WorkspaceLeft(props) {
  const { className } = props;

  return (
    <div className={classnames(styles.box, className)}>
      <div className={styles.header}>
        <RenderSelectDatabase />
      </div>
      <RenderSaveBox></RenderSaveBox>
      <Divider />
      <RenderTableBox />
    </div>
  );
});

interface Option {
  value: number;
  label: string;
  children?: Option[];
}

function RenderSelectDatabase() {
  const [options, setOptions] = useState<Option[]>();
  const { state, dispatch } = useReducerContext();
  const { currentWorkspaceData } = state;

  useEffect(() => {
    getDataSource();
  }, []);

  function getDataSource() {
    let p = {
      pageNo: 1,
      pageSize: 999,
    };
    treeConfig[TreeNodeType.DATA_SOURCES].getChildren!(p).then((res) => {
      let newOptions: any = res.map((t) => {
        return {
          label: t.name,
          value: t.key,
          type: TreeNodeType.DATA_SOURCE,
          isLeaf: false,
          databaseType: t.extraParams?.databaseType,
        };
      });
      setOptions(newOptions);
    });
  }

  const onChange: any = (valueArr: any, selectedOptions: any) => {
    let labelArr: string[] = [];
    labelArr = selectedOptions.map((t: any) => {
      return t.label;
    });

    const currentWorkspaceData = {
      dataSourceId: valueArr[0],
      databaseSourceName: labelArr[0],
      databaseName: labelArr[1],
      schemaName: labelArr[2],
      databaseType: selectedOptions[0].databaseType,
    };

    dispatch({
      type: workspaceActionType.CURRENT_WORKSPACE_DATA,
      payload: currentWorkspaceData,
    });
  };

  // 及联loadData
  const loadData = (selectedOptions: any) => {
    if (selectedOptions.length > 1) {
      return;
    }

    const targetOption = selectedOptions[0];
    treeConfig[TreeNodeType.DATA_SOURCE].getChildren!({
      id: targetOption.value,
    }).then((res) => {
      let newOptions = res.map((t) => {
        return {
          label: t.name,
          value: t.key,
          type: TreeNodeType.DATABASE,
          databaseType: t.extraParams?.databaseType,
        };
      });
      targetOption.children = newOptions;
      setOptions([...(options || [])]);
    });

    // TODO:根据后端字段 如果有SCHEMAS再去查询SCHEMAS
    // if (targetOption.type === TreeNodeType.SCHEMAS) {
    //   treeConfig[TreeNodeType.DATA_SOURCE]?.getChildren({
    //     id: targetOption.value
    //   }).then(res => {
    //     let newOptions = res.map((t) => {
    //       return {
    //         label: t.name,
    //         value: t.name,
    //         type: TreeNodeType.SCHEMAS
    //       };
    //     });
    //     targetOption.children = newOptions;
    //     setOptions([...(options || [])]);
    //   })
    // } else {
    // }
  };

  const dropdownRender = (menus: React.ReactNode) => (
    <div>
      {menus}
      {/* <div style={{ height: 0, opacity: 0 }}>The footer is not very short.</div> */}
    </div>
  );

  function renderCurrentSelected() {
    const { databaseName, schemaName, databaseSourceName } = currentWorkspaceData;
    const currentSelectedArr = [databaseSourceName, databaseName, schemaName].filter((t) => t);
    return currentSelectedArr.join('/');
  }

  return (
    <div className={styles.selectDatabaseBox}>
      <Cascader
        popupClassName={styles.cascaderPopup}
        options={options}
        onChange={onChange}
        loadData={loadData}
        bordered={false}
        dropdownRender={dropdownRender}
      >
        <div className={styles.currentDatabase}>
          <div className={styles.name}>{renderCurrentSelected() || <span style={{ 'opacity': 0.8 }}>{i18n('workspace.cascader.placeholder')}</span>} </div>
          <Iconfont code="&#xe608;" />
        </div>
      </Cascader>
      <div className={styles.otherOperations}>
        <div className={styles.iconBox}>
          <Iconfont code="&#xec08;" />
        </div>
      </div>
    </div>
  );
}

function RenderTableBox() {
  const { state, dispatch } = useReducerContext();
  const { currentWorkspaceData } = state;
  const [initialData, setInitialData] = useState<ITreeNode[]>([]);

  useEffect(() => {
    if(currentWorkspaceData.databaseName){
      getInitialData();
    }
  }, [currentWorkspaceData]);

  function getInitialData() {
    treeConfig[TreeNodeType.TABLES].getChildren!({
      pageNo: 1,
      pageSize: 999,
      ...currentWorkspaceData,
      extraParams: currentWorkspaceData,
    }).then((res) => {
      setInitialData(res);
    });
  }

  return (
    <div className={styles.table_box}>
      <div className={styles.left_box_title}>Table</div>
      <LoadingContent data={initialData} handleEmpty>
        <Tree className={styles.tree} initialData={initialData}></Tree>
      </LoadingContent>
    </div>
  );
}

function RenderSaveBox() {
  const [savedList, setSaveList] = useState<IConsole[]>([]);
  const { state, dispatch } = useReducerContext();
  const { currentWorkspaceData } = state;

  useEffect(() => {
    getSaveList();
  }, [currentWorkspaceData])


  function getSaveList() {
    let p = {
      pageNo: 1,
      pageSize: 999,
      ...currentWorkspaceData
    }

    historyService.getSaveList(p).then(res => {
      setSaveList(res.data)
    })
  }

  return <div className={styles.save_box}>
    <div className={styles.left_box_title}>Saved</div>
    <div className={styles.save_box_list}>
      <LoadingContent data={savedList} handleEmpty>
        {
          savedList?.map(t => {
            return <div>
              {t.name}
            </div>
          })
        }
      </LoadingContent>
    </div>
  </div>
}
