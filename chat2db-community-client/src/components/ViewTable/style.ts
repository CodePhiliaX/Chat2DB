import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      position: relative;
      height: 100%;
    `,
    tableLoading: css`
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      overflow: hidden;
    `,
    stopExecuteSql: css`
      cursor: pointer;
      margin-top: 30px;
      &:hover {
        color: ${token.colorPrimary};
      }
    `,
    retryBox: css`
      display: flex;
      height: 100%;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      gap: 12px;
    `,
    retryText: css`
      color: ${token.colorTextSecondary};
    `,
  };
});
