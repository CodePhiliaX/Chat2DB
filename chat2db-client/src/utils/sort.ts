/**
 * 比较两个字符串数字的大小，包含大数情况
 * @param a
 * @param b
 * @returns
 */
export function compareStrings(a: string, b: string) {
  
  if (!a  ) {
    return -1;
  }

  if (!b) {
    return 1;
  }

  // 比较字符串长度
  if (a.length !== b.length) {
    return a.length - b.length;
  }

  // 逐位比较字符的ASCII码值
  for (let i = 0; i < a.length; i++) {
    if (a[i] !== b[i]) {
      return a.charCodeAt(i) - b.charCodeAt(i);
    }
  }

  // 如果两个字符串完全相等，则返回0
  return 0;
}
