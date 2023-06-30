export function formatParams(obj: { [key: string]: any }) {
  const params = new URLSearchParams();
  Object.entries(obj).forEach(([key, value]) => {
    if (value === undefined || value === null) {
      return;
    }
    if (Array.isArray(value)) {
      value.forEach((item) => {
        params.append(key, item);
      });
    } else {
      params.append(key, value);
    }
  });
  return params.toString();
}
