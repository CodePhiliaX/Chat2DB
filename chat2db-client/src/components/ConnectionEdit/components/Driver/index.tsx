import React, { memo, useState, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { i18n } from '@/i18n';
import { Form, Modal, Input, Select } from 'antd';
import connectionService, { IDriverResponse } from '@/service/connection';
import UploadDriver from '@/components/UploadDriver';
import LoadingGracile from '@/components/Loading/LoadingGracile';
const { Option } = Select;

interface IProps {
  className?: string;
  onChange: (data: any) => void;
  backfillData: any;
}

enum DownloadStatus {
  Default,
  Loading,
  Error,
  Success,
}

export default memo<IProps>((props) => {
  const { className, backfillData, onChange } = props;
  const [downloadStatus, setDownloadStatus] = useState<DownloadStatus>(DownloadStatus.Default);
  const [driverForm] = Form.useForm();
  const [driverObj, setDriverObj] = useState<IDriverResponse>();
  const [uploadDriverModal, setUploadDriverModal] = useState(false);
  const [driverSaved, setDriverSaved] = useState<any>({});

  useEffect(() => {
    if (backfillData) {
      getDriverList();
    }
  }, [backfillData?.type]);

  useEffect(() => {
    if (backfillData) {
      const data = {
        jdbcDriverClass: backfillData?.driverConfig?.jdbcDriverClass,
        jdbcDriver: backfillData?.driverConfig?.jdbcDriver,
      };
      driverForm.setFieldsValue(data);
      onChange(data);
    }
  }, [backfillData?.driverConfig, backfillData?.id]);

  function getDriverList() {
    connectionService.getDriverList({ dbType: backfillData.type }).then((res) => {
      if (!res) {
        return;
      }
      setDriverObj({
        ...res,
        driverConfigList: res.driverConfigList || [],
      });
      if (res.driverConfigList?.length && !backfillData?.driverConfig?.jdbcDriver) {
        const data = {
          jdbcDriverClass: res.driverConfigList[0]?.jdbcDriverClass,
          jdbcDriver: res.driverConfigList[0]?.jdbcDriver,
        };
        driverForm.setFieldsValue(data);
        onChange(data);
      }
    });
  }

  function formChange(data: any) {
    setDriverSaved(data);
  }

  function saveDriver() {
    connectionService.saveDriver(driverSaved).then(() => {
      setUploadDriverModal(false);
      getDriverList();
    });
  }

  function downloadDrive() {
    setDownloadStatus(DownloadStatus.Loading);
    connectionService
      .downloadDriver({ dbType: backfillData.type })
      .then(() => {
        setDownloadStatus(DownloadStatus.Success);
        getDriverList();
      })
      .catch(() => {
        setDownloadStatus(DownloadStatus.Error);
      });
  }

  function onValuesChange(data: any) {
    const selected = driverObj?.driverConfigList.find((t) => t.jdbcDriver === data.jdbcDriver);
    driverForm.setFieldsValue({
      jdbcDriverClass: selected?.jdbcDriverClass,
    });
    onChange({
      jdbcDriverClass: selected?.jdbcDriverClass,
      jdbcDriver: data.jdbcDriver,
    });
  }

  return (
    <div className={classnames(styles.box, className)}>
      <Form form={driverForm} onValuesChange={onValuesChange} colon={false}>
        <Form.Item labelAlign="left" name="jdbcDriver" label={i18n('connection.title.driver')}>
          <Select>
            {driverObj?.driverConfigList?.map((t) => (
              <Option key={t.jdbcDriver} value={t.jdbcDriver}>
                {t.jdbcDriver}
              </Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item labelAlign="left" name="jdbcDriverClass" label="Class">
          <Input disabled />
        </Form.Item>
      </Form>
      <div className={styles.downloadDriveFooter}>
        {(driverObj?.driverConfigList && !driverObj?.driverConfigList?.length) ||
        downloadStatus === DownloadStatus.Success ? (
          <div onClick={downloadDrive} className={styles.downloadDrive}>
            {downloadStatus === DownloadStatus.Default && (
              <div className={classnames(styles.downloadText, styles.downloadTextDownload)}>
                {i18n('connection.text.downloadDriver')}
              </div>
            )}
            {downloadStatus === DownloadStatus.Loading && (
              <div className={classnames(styles.downloadText, styles.downloadTextLoading)}>
                <LoadingGracile />
                <div className={styles.text}>{i18n('connection.text.downloading')}</div>
              </div>
            )}
            {downloadStatus === DownloadStatus.Error && (
              <div className={classnames(styles.downloadText, styles.downloadTextError)}>
                {i18n('connection.text.tryAgainDownload')}
              </div>
            )}
            {downloadStatus === DownloadStatus.Success && (
              <div className={classnames(styles.downloadText, styles.downloadTextSuccess)}>
                {i18n('connection.text.downloadSuccess')}
              </div>
            )}
          </div>
        ) : (
          <div />
        )}

        <div
          className={styles.uploadCustomDrive}
          onClick={() => {
            setUploadDriverModal(true);
          }}
        >
          {i18n('connection.tips.customUpload')}
        </div>
      </div>
      <Modal
        destroyOnClose={true}
        title={i18n('connection.title.uploadDriver')}
        open={uploadDriverModal}
        onOk={() => {
          saveDriver();
        }}
        onCancel={() => {
          setUploadDriverModal(false);
        }}
      >
        <UploadDriver
          jdbcDriverClass={driverObj?.defaultDriverConfig?.jdbcDriverClass}
          formChange={formChange}
          databaseType={backfillData.type}
        />
      </Modal>
    </div>
  );
});
