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
      background-color: ${token.colorBgBase};
      height: 100%;
    `,

    connectionListWrapper: css`
      flex: 1;
      overflow-y: auto;
      padding: 0px 8px;
    `,
    connectionItem: css`
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 8px;
      margin-bottom: 4px;
      border-radius: 8px;
      user-select: none;
      background-color: 'transparent';
      cursor: pointer;
      transition: background-color 400ms ${token.motionEaseOut};
      color: ${token.colorTextLabel};
      &:hover {
        background-color: ${token.colorFill};
        color: ${token.colorTextBase};
      }
    `,
    activeConnectionItem: css`
      background-color: ${token.colorFillSecondary};
      color: ${token.colorTextBase};
      font-weight: 500;
    `,
    connectionItemIcon: css`
      margin-right: 6px;
    `,

    connectionItemLabel: css`
      margin-right: 6px;
    `,

    addConnection: css`
      margin: 0 20px 10px;
    `,

    containerRight: css``,
    connectionDetail: css`
      background-color: ${token.colorBgBase};
    `,
  };
});
