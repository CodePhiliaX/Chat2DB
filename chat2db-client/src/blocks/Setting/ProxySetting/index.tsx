import React, { useState } from 'react';
import i18n from '@/i18n';
import { Button, Input, message } from 'antd';
import classnames from 'classnames';
import styles from './index.less';
import outSideService from '@/service/outside';

// 代理设置
export default function ProxyBody() {
  const [apiPrefix, setApiPrefix] = useState(window._BaseURL);

  function updateApi(e: any) {
    setApiPrefix(e.target.value);
  }

  function affirmUpdateApi() {
    if (!apiPrefix) {
      return;
    }
    outSideService.dynamicUrl(`${apiPrefix}/api/system/get-version-a`).then((res: any) => {
      localStorage.setItem('_BaseURL', apiPrefix);
      location.reload();
    }).catch((err: any) => {
      message.error(i18n('setting.message.urlTestError'))
    });
    // try {
    //   const xhr = new XMLHttpRequest();
    //   xhr.withCredentials = true;
    //   xhr.open('GET', `${apiPrefix}/api/system/get-version-a`);
    //   xhr.onload = function () {
    //     if (xhr.status === 200) {
    //       localStorage.setItem('_BaseURL', apiPrefix);
    //       location.reload();
    //     } else {
    //       message.error(i18n('setting.message.urlTestError'));
    //     }
    //   };
    //   xhr.send();
    // } catch {
    //   message.error(i18n('setting.message.urlTestError'));
    // }
  }

  return (
    <>
      <div className={styles.title}>{i18n('setting.label.serviceAddress')}</div>
      <div className={classnames(styles.content, styles.chatGPTKey)}>
        <Input value={apiPrefix} onChange={updateApi} />
      </div>
      <div className={styles.bottomButton}>
        <Button type="primary" onClick={affirmUpdateApi}>
          {i18n('setting.button.apply')}
        </Button>
      </div>
    </>
  );
}
