import { createStyles } from 'antd-style';

export const useStyle = createStyles(({ css, token }) => {
  return {
    connectionListWrapper: css`
      flex: 1;
      overflow-y: auto;
      padding: 0px 8px;
    `,
    connectionItem: css``,
    addConnection: css`
      margin: 0 20px 10px;
    `,
  };
});
