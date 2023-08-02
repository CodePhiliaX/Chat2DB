import 'umi/typings';

declare namespace NodeJS {
  interface ProcessEnv {
    readonly NODE_ENV: 'development' | 'production'
    readonly UMI_ENV: string
    readonly __ENV: string;
  }
}