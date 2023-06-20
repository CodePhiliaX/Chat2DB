DROP TABLE if exists test_query;

CREATE TABLE  `test_query`
(
    `id`     bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键',
    `name`   VARCHAR(100) COMMENT '名字',
     `date`   datetime  COMMENT '时间',
     `number`   int  COMMENT '数字'
)  ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='测试表';


INSERT INTO `test_query` (name,date,number) VALUES ('姓名','2022-01-01',123);