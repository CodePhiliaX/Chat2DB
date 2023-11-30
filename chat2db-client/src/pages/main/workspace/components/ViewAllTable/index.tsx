import React, { memo, useMemo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { BaseTable, ArtColumn, useTablePipeline, features, SortItem } from 'ali-react-table';
import styled from 'styled-components';
import i18n from '@/i18n';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [tableData, setTableData] = React.useState<any[]>([]);
  const SupportBaseTable: any = styled(BaseTable)`
    &.supportBaseTable {
      --bgcolor: var(--color-bg-base);
      --header-bgcolor: var(--color-bg-subtle);
      --hover-bgcolor: transparent;
      --header-hover-bgcolor: var(--color-bg-subtle);
      --highlight-bgcolor: transparent;
      --header-highlight-bgcolor: var(--color-bg-subtle);
      --color: var(--color-text);
      --header-color: var(--color-text);
      --lock-shadow: rgb(37 37 37 / 0.5) 0 0 6px 2px;
      --border-color: var(--color-border-secondary);
      --cell-padding: 0px;
      --row-height: 32px;
      --lock-shadow: 0px 1px 2px 0px var(--color-border);
    }
  `;

  // 表格 列配置
  const columns: ArtColumn[] = useMemo(() => {
    return [].map((item, colIndex) => {
      return {
        code: 'colNoCode',
        name: 'No.',
        key: 'name',
        lock: true,
        render: (value: any, rowData, rowIndex) => {
          return <div>1</div>;
        },
      };
    });
  }, []);

  // 表格渲染的配置
  const pipeline = useTablePipeline()
    .input({ dataSource: tableData, columns })
    .use(
      features.columnResize({
        fallbackSize: 150,
        // handleBackground: '#ddd',
        handleHoverBackground: `var(--color-primary-bg-hover)`,
        handleActiveBackground: `var(--color-primary-bg-hover)`,
        minSize: 60,
        maxSize: 1080,
        sizes: [0],
      }),
    );

  return (
    <div className={classnames(styles.box, className)}>
      <SupportBaseTable
        className={classnames('supportBaseTable', props.className, styles.table)}
        components={{ EmptyContent: () => <h2>{i18n('common.text.noData')}</h2> }}
        isStickyHead
        stickyTop={31}
        {...pipeline.getProps()}
      />
    </div>
  );
});
