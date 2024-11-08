delete from DATA_SOURCE where ALIAS ='DEMO@db.sqlgpt.cn';

delete from DASHBOARD where id =ID;

delete from CHART where id<=3;

delete  from DASHBOARD_CHART_RELATION where CHART_ID<=3;
