import React, { memo, Fragment } from 'react';
import i18n from '@/i18n';
import styles from './index.less';
import classnames from 'classnames';
import { OSnow } from '@/utils'

interface IProps {
  className?: string;
}

const keyboardKey = (function () {
  if (OSnow().isMac) {
    return {
      command: 'Cmd'
    }
  }
  return {
    command: 'Ctrl'
  }
}())

const shortcutsList = [
  {
    title: i18n('common.text.textToSQL'),
    keys: [
      'Enter'
    ]
  },
  {
    title: i18n('common.text.optimizeSQL'),
    keys: [
      i18n('common.text.editorRightClick')
    ]
  },
  {
    title: i18n('common.text.executeSelectedSQL'),
    keys: [
      keyboardKey.command,
      'Enter'
    ]
  },
  {
    title: i18n('common.text.saveConsole'),
    keys: [
      keyboardKey.command,
      'S'
    ]
  },
  {
    title: i18n('common.text.refreshPage'),
    keys: [
      keyboardKey.command,
      'R'
    ]
  },
  // {
  //   title: i18n('common.button.createConsole'),
  //   keys: [
  //     keyboardKey.command,
  //     'T'
  //   ]
  // },
]

export default memo<IProps>(function ShortcutKey(props) {
  const { className } = props;
  return <div className={classnames(styles.box, className)}>
    <div className={styles.letterpress}>
      Chat2DB
    </div>
    <div className={styles.shortcuts}>
      {
        shortcutsList.map((t, i) => {
          return <div key={i} className={styles.shortcutsItem}>
            <div className={styles.title}>
              {t.title}
            </div>
            <div className={styles.plusSignBox}>
              {t.keys.map((item, i) => {
                return <Fragment key={i}>
                  <span>
                    {item}
                  </span>
                  {
                    i + 1 < t.keys.length &&
                    <span className={styles.plusSign}>
                      +
                    </span>
                  }
                </Fragment>
              })}
            </div>
          </div>
        })
      }
    </div>
  </div>
})
