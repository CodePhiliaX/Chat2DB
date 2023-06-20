export function imghub(imgName: string) {
  const suffix = 'webp';
  const obj = new URL(`/public/logo.webp`);
  return obj.pathname;
}