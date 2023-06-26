export function formatParams(obj: { [key: string]: any }) {
  let params = '';
  for (let key in obj) {
    if (obj[key]) {
      params += `${key}=${obj[key]}&`;
    }
  }
  return params;
}