import { createStyles } from 'antd-style';

export const useStyles = createStyles(({ css, token }) => {
  return {
    appBar: css`
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: ${token.colorBgLayout};
      border-bottom: 1px solid ${token.colorBorder};
      user-select: none;
      -webkit-app-region: drag;
    `,
    logoContainer: css`
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-left: 12px;
    `,
    logoRightSolt: css`
      display: flex;
      align-items: center;
    `,

    windowsActionBar: css`
      display: flex;
      -webkit-app-region: no-drag;
    `,
    windowsAction: css`
      width: 42px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      &:hover {
        background-color: ${token.controlItemBgHover};
      }
    `,
  };
});
