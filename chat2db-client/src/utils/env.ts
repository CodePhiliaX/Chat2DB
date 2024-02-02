export const isMac = !!window.electronApi?.getPlatform().isMac;

export const isDev = process.env.NODE_ENV === 'development';
