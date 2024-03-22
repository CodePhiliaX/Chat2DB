/**
 * 文件下载 
 * @param url 
 * @param params 
 */
export function downloadFile(url: string, params: any) {
  // 创建POST请求
  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json', // 或者根据服务端的要求设置其他的内容类型
    },
    body: JSON.stringify(params), // 将参数转换为JSON字符串
  })
    .then((response) => {
      // 从content-disposition头中获取文件名
      const contentDisposition = response.headers.get('content-disposition');
      const filename = contentDisposition ? decodeURIComponent(contentDisposition.split("''")[1]) : 'file.txt';

      // 获取返回的Blob数据
      return response.blob().then((blob) => ({ blob, filename }));
    })
    .then(({ blob, filename }) => {
      // 创建一个代表Blob对象的URL
      const blobUrl = URL.createObjectURL(blob);

      // 创建一个隐藏的 <a> 标签，并设置其 href 属性
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = blobUrl;

      // 使用从响应头解析的文件名
      a.download = filename;

      // 将 <a> 标签附加到 DOM，并触发点击事件
      document.body.appendChild(a);
      a.click();

      // 清理：从 DOM 中移除 <a> 标签，并释放Blob URL
      document.body.removeChild(a);
      URL.revokeObjectURL(blobUrl);
    })
    .catch((error) => {
      console.error('下载文件失败:', error);
    });
}
