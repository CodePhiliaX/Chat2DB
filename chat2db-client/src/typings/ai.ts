export enum AiSqlSourceType {
  CHAT2DBAI = 'CHAT2DBAI',
  OPENAI = 'OPENAI',
  AZUREAI = 'AZUREAI',
  RESTAI = 'RESTAI',
}

export interface IRemainingUse {
  key: string;
  wechatMpUrl: string;
  expiry: number;
  remainingUses: number;
}

export interface ILoginAndQrCode {
  token: string;
  wechatQrCodeUrl: string;
  apiKey: string;
  tip: string;
}

export interface IInviteQrCode {
  wechatQrCodeUrl: string;
  tip: string;
}
