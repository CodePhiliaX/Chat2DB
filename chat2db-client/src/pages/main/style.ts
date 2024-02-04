import { createStyles } from 'antd-style';

export const useStyle = createStyles(({ css, token }, { isMac }: { isMac: boolean }) => {
  return {
    container: css`
      display: flex;
      width: 100vw;
      height: 100vh;
    `,

    leftContainer: css`
      width: 64px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding-top: ${isMac ? '20px' : '0'};
      border-right: 1px solid ${token.colorBorder};
      background-color: ${token.colorBgBase};
    `,
    logo: css`
      margin-bottom: 20px;
    `,
    navContainer: css`
      margin-top: 20px;
      display: flex;
      align-items: center;
      flex-direction: column;
      gap: 4px;
    `,
    settingContainer: css`
      display: flex;
      align-items: center;
      flex-direction: column;
      margin-bottom: 20px;
    `,

    rightContianer: css`
      flex: 1;
    `,
    componentBox: css`
      width: 100%;
      height: 100%;
    `,
  };
});
