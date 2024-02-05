import { createStyles } from 'antd-style';

export const useStyle = createStyles(({ css, token }) => {
  return {
    container: css`
      display: flex;
      height: 100%;
      width: 100%;
    `,
    containerLeft: css`
      display: flex;
      flex-direction: column;
      overflow: hidden;
    `,
    listWrapper: css`
      padding: 0px 8px;
      display: flex;
      flex-direction: column;
      gap: 4px;
      flex: 1;
      overflow-y: auto;
    `,
    containerRight: css`
      height: 100%;
      background-color: ${token.colorBgBase};
    `,
  };
});
