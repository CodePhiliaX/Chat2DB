export enum AiSqlSourceType {
  CHAT2DB = 'CHAT2DB',
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
