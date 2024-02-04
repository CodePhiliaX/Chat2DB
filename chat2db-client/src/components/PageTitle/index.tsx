import { DivProps } from '@chat2db/ui/es/types';
import { createStyles } from 'antd-style';
import { memo } from 'react';

export interface PageTitleProps extends DivProps {
  title: string;
}

export const useStyle = createStyles(({ css, token }) => {
  return {
    title: css`
      font-weight: 800;
      font-size: ${token.fontSizeHeading3}px;
      padding: 16px;
    `,
  };
});
const PageTitle = memo<PageTitleProps>(({ className, style, title, ...rest }) => {
  const { styles, cx } = useStyle();
  return (
    <div className={cx(styles.title, className)} style={style} {...rest}>
      {title}
    </div>
  );
});

export default PageTitle;
