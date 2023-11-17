import React, { useEffect, useState } from 'react';
import { Modal, Upload, Button, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import i18n from '@/i18n';

const uploadFileType = {
  ncx: {
    accept: '.ncx',
    uploadUrl: window._BaseURL + '/api/converter/ncx/upload',
  },
  dbp: {
    accept: '.dbp',
    uploadUrl: window._BaseURL + '/api/converter/dbp/upload',
  },
};

interface IImportConnectionProps {
  open: boolean;
  onClose: () => void;
  onConfirm?: () => void;
}

const ImportConnection: React.FC<IImportConnectionProps> = ({ open, onClose, onConfirm }) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (open) {
      setSelectedFile(null);
    }
  }, [open]);

  const handleBeforeUpload = (file: File) => {
    setSelectedFile(file);
    return false; // 停止自动上传
  };

  const handleConfirmUpload = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append('file', selectedFile);

    const fileExtension = selectedFile.name.split('.').pop() || '';

    const { uploadUrl } = uploadFileType[fileExtension] || {};

    try {
      setUploading(true);

      const response = await fetch(uploadUrl, {
        method: 'POST',
        body: formData,
      });
      if (response.ok) {
        message.success(`${selectedFile.name} 导入数据源成功`);
        onConfirm && onConfirm();
      } else {
        message.error(`${selectedFile.name} 导入数据源失败`);
      }
    } catch (error) {
      message.error(`Error: ${error}`);
    } finally {
      setUploading(false);
    }
  };

  return (
    <Modal
      title="导入文件, .ncx(navicat) 或 .dbp(dbever)"
      open={open}
      onCancel={onClose}
      onOk={handleConfirmUpload}
      confirmLoading={uploading}
    >
      <Upload
        name="file"
        accept=".ncx,.dbp"
        beforeUpload={handleBeforeUpload}
        maxCount={1}
        fileList={selectedFile ? [selectedFile] : []}
      >
        <Button icon={<UploadOutlined />}>{i18n('common.text.selectFile')}</Button>
      </Upload>
    </Modal>
  );
};

export default ImportConnection;
