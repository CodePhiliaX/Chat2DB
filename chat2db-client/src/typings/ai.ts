export enum AiSqlSourceType {
  CHAT2DB='CHAT2DB',
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
