create table goods
(
    id               int unsigned primary key auto_increment comment’商品id’,
    category_id      int unsigned not null default 0 comment’分类id’,
    spu_id           int unsigned not null default 0 comment’SPU id’,
    sn               varchar(20)  not null default ‘’ comment’编号’,
    name             varchar(120) not null default ‘‘comment’名称’,
    keyword          varchar(255) not null default ‘’ comment’关键字’,
    picture          varchar(255) not null default ’’ comment’图片’,
    tips             varchar(255) not null default ‘’ comment’提示’,
    description      varchar(255) not null default ‘’ comment’描述’,
    content          text         not null comment’详情’,
    price            decimal(10, 2) unsigned not null default 0 comment ‘价格’,
    stock            int unsigned not null default 0 comment ‘库存’,
    score            decimal(3, 2) unsigned not null default 0 comment’评分’,
    is_on_sale       tinyint unsigned not null default 0 comment’是否上架’,
    is_del           tinyint unsigned not null default 0 comment’是否删除’,
    is_free_shipping tinyint unsigned not null default 0 comment’是否包邮’,
    sell_count       int unsigned not null default 0 comment’销量计数’,
    comment          int unsigned not null default 0 comment’评论计数’,
    on_sale_time     datetime              default null comment’上架时间’,
    create_time      datetime     not null default current_timestamp comment’创建时间’,
    update_time      datetime              default null comment’更新时间’
);
