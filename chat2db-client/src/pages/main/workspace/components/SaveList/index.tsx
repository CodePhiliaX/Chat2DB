import React, { memo, useState, useEffect, useRef } from 'react';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Input, Dropdown } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IWorkspaceModelType } from '@/models/workspace';
import historyServer from '@/service/history';
import { ConsoleStatus, ConsoleOpenedStatus, WorkspaceTabType, workspaceTabConfig } from '@/constants';
import { IConsole, ITreeNode } from '@/typings';
import styles from './index.less';
import { approximateList } from '@/utils';

const dvaModel = connect(({ workspace }: { workspace: IWorkspaceModelType }) => ({
  workspaceModel: workspace,
}));

const SaveList = dvaModel((props: any) => {
  const { workspaceModel, dispatch } = props;
  const { curWorkspaceParams, consoleList } = workspaceModel;
  const [searching, setSearching] = useState<boolean>(false);
  const inputRef = useRef<any>();
  const [searchedList, setSearchedList] = useState<ITreeNode[] | undefined>();
  const leftModuleTitleRef = useRef<any>(null);
  const saveBoxListRef = useRef<any>(null);

  // 监听saveBoxListRef滚动时，给leftModuleTitle添加下阴影
  useEffect(() => {
    const saveBoxList = saveBoxListRef.current;
    const leftModuleTitle = leftModuleTitleRef.current;
    if (saveBoxList && leftModuleTitle) {
      saveBoxList.addEventListener('scroll', () => {
        if (saveBoxList.scrollTop > 0) {
          leftModuleTitle.classList.add(styles.leftModuleTitleShadow);
        } else {
          leftModuleTitle.classList.remove(styles.leftModuleTitleShadow);
        }
      });
    }
  }, []);

  useEffect(() => {
    if (!curWorkspaceParams.dataSourceId || !(curWorkspaceParams?.databaseName || curWorkspaceParams?.schemaName)) {
      return;
    }
    dispatch({
      type: 'workspace/fetchGetSavedConsole',
      payload: {
        pageNo: 1,
        pageSize: 999,
        orderByDesc: true,
        status: ConsoleStatus.RELEASE,
        ...curWorkspaceParams,
      },
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setConsoleList',
          payload: res.data,
        });
      },
    });
  }, [curWorkspaceParams]);

  useEffect(() => {
    if (searching) {
      inputRef.current!.focus({
        cursor: 'start',
      });
    }
  }, [searching]);

  function openSearch() {
    setSearching(true);
  }

  function onBlur() {
    if (!inputRef.current.input.value) {
      setSearching(false);
      setSearchedList(undefined);
    }
  }

  function onChange(value: string) {
    setSearchedList(approximateList(consoleList, value));
  }

  function openConsole(data: IConsole) {
    const params: any = {
      id: data.id,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    };
    historyServer.updateSavedConsole(params).then((res) => {
      dispatch({
        type: 'workspace/setCreateConsoleIntro',
        payload: {
          id: data.id,
          type: WorkspaceTabType.CONSOLE,
          title: data.name,
          uniqueData: data,
        },
      });
    });
  }

  function deleteSaved(data: IConsole) {
    const params: any = {
      id: data.id,
      status: ConsoleStatus.DRAFT,
    };
    historyServer.updateSavedConsole(params).then(() => {
      dispatch({
        type: 'workspace/fetchGetSavedConsole',
        payload: {
          orderByDesc: true,
          status: ConsoleStatus.RELEASE,
          ...curWorkspaceParams,
        },
        callback: (_res: any) => {
          dispatch({
            type: 'workspace/setConsoleList',
            payload: _res.data,
          });
        },
      });
    });
  }

  return (
    <div className={styles.saveModule}>
      <div ref={leftModuleTitleRef} className={styles.leftModuleTitle}>
        {searching ? (
          <div className={styles.leftModuleTitleSearch}>
            <Input
              ref={inputRef}
              size="small"
              placeholder={i18n('common.text.search')}
              prefix={<Iconfont code="&#xe600;" />}
              onBlur={onBlur}
              onChange={(e) => onChange(e.target.value)}
              allowClear
            />
          </div>
        ) : (
          <div className={styles.leftModuleTitleText}>
            <div className={styles.modelName}>{i18n('workspace.title.saved')}</div>
            <div className={styles.iconBox}>
              {/* <div className={styles.refreshIcon} onClick={() => refreshTableList()}>
                  <Iconfont code="&#xec08;" />
                </div> */}
              <div className={styles.searchIcon} onClick={() => openSearch()}>
                <Iconfont code="&#xe600;" />
              </div>
            </div>
          </div>
        )}
      </div>
      <div ref={saveBoxListRef} className={styles.saveBoxList}>
        <LoadingContent className={styles.loadingContent} data={consoleList} handleEmpty>
          {(searchedList || consoleList)?.map((t: IConsole) => {
            return (
              <div
                onDoubleClick={() => {
                  openConsole(t);
                }}
                key={t.id}
                className={styles.saveItem}
              >
                <div className={styles.saveItemText}>
                  <div className={styles.iconBox}>
                    <Iconfont code={workspaceTabConfig[t.operationType]?.icon} />
                  </div>
                  <div className={styles.itemName} dangerouslySetInnerHTML={{ __html: t.name }} />
                </div>
                <Dropdown
                  menu={{
                    items: [
                      {
                        key: 'open',
                        label: i18n('common.button.open'),
                        onClick: () => {
                          openConsole(t);
                        },
                      },
                      {
                        key: 'delete',
                        label: i18n('common.button.delete'),
                        onClick: () => {
                          deleteSaved(t);
                        },
                      },
                    ],
                  }}
                >
                  <div className={styles.moreButton}>
                    <Iconfont code="&#xe601;" />
                  </div>
                </Dropdown>
              </div>
            );
          })}
        </LoadingContent>
      </div>
    </div>
  );
});

export default dvaModel(SaveList);
