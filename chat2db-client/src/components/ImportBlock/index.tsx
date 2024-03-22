import React from 'react';
import { Modal, Upload, Button } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import i18n from '@/i18n';

interface IImportBlockProps {
  children: React.ReactNode;
  title?: string;
  accept: string;
  maxCount?: number;
  onConfirm?: (fileList: Array<File> | File) => Promise<boolean>;
}

function ImportBlock(props: IImportBlockProps) {
  const { title, children, accept, maxCount = 1 } = props;

  const [open, setOpen] = React.useState(false);
  const [selectedFile, setSelectedFile] = React.useState<Array<File> | null>(null);

  const onClose = () => {
    setOpen(false);
  };

  const onOk = async () => {
    if (!selectedFile) return;

    if (props.onConfirm) {
      const success = await props.onConfirm(maxCount === 1 ? selectedFile?.[0] : selectedFile);
      if (success) {
        setOpen(false);
      }
    }
  };

  const handleBeforeUpload = (file: File, FileList: File[]) => {
    setSelectedFile(FileList);
    return false; // 停止自动上传
  };

  return (
    <div>
      <div onClick={() => setOpen(true)}>{children}</div>

      <Modal title={title} open={open} onCancel={onClose} onOk={onOk}>
        <Upload
          name="file"
          accept={accept}
          beforeUpload={handleBeforeUpload}
          maxCount={maxCount}
          fileList={selectedFile ? [...selectedFile] : null}
        >
          <Button icon={<UploadOutlined />}>{i18n('common.text.selectFile')}</Button>
        </Upload>
      </Modal>
    </div>
  );
}

export default ImportBlock;
