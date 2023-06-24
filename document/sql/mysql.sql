CREATE TABLE `product` (
                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                           `name` varchar(255) NOT NULL COMMENT '商品名称',
                           `description` varchar(1000) NOT NULL COMMENT '商品描述',
                           `price` decimal(10,2) NOT NULL COMMENT '商品单价',
                           `category_id` int(11) NOT NULL COMMENT '所属类别ID',
                           `brand_id` int(11) DEFAULT NULL COMMENT '品牌ID',
                           `origin` varchar(255) DEFAULT NULL COMMENT '商品产地',
                           `weight` decimal(10,2) DEFAULT NULL COMMENT '商品重量（kg）',
                           `length` decimal(10,2) DEFAULT NULL COMMENT '商品长度（cm）',
                           `width` decimal(10,2) DEFAULT NULL COMMENT '商品宽度（cm）',
                           `height` decimal(10,2) DEFAULT NULL COMMENT '商品高度（cm）',
                           `thumbnail` varchar(255) DEFAULT NULL COMMENT '商品缩略图URL',
                           `image` varchar(1000) DEFAULT NULL COMMENT '商品图片URL',
                           `is_sale` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否上架，0：下架，1：上架',
                           `stock` int(11) NOT NULL COMMENT '商品库存',
                           `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `category_id` (`category_id`),
                           KEY `brand_id` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

INSERT INTO product (name, description, price, category_id, brand_id, origin, weight, length, width, height, thumbnail, image, is_sale, stock, created_at, updated_at)
SELECT
    CONCAT('商品', t1.n),
    CONCAT('这是商品', t1.n, '的描述'),
    ROUND(RAND() * 1000, 2),
    FLOOR(RAND() * 10) + 1,
    IF(RAND() < 0.5, NULL, FLOOR(RAND() * 10) + 1),
    CONCAT('产地', t1.n),
    ROUND(RAND() * 10, 2),
    ROUND(RAND() * 100, 2),
    ROUND(RAND() * 100, 2),
    ROUND(RAND() * 100, 2),
    CONCAT('http://example.com/thumbnail_', t1.n, '.jpg'),
    CONCAT('http://example.com/image_', t1.n, '.jpg'),
    IF(RAND() < 0.5, 0, 1),
    FLOOR(RAND() * 1000),
    NOW() - INTERVAL FLOOR(RAND() * 365) DAY,
    NOW() - INTERVAL FLOOR(RAND() * 365) DAY
FROM
    (SELECT @rownum:=0) t0,
    (SELECT @rownum:=@rownum+1 AS n FROM information_schema.COLUMNS LIMIT 10000) t1 ;


CREATE TABLE `order` (
                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                         `user_id` int(11) NOT NULL COMMENT '用户ID',
                         `total_price` decimal(10,2) NOT NULL COMMENT '订单总价',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

INSERT INTO `order` (user_id, total_price, created_at, updated_at)
SELECT
        FLOOR(RAND() * 1000) + 1,
        ROUND(RAND() * 10000, 2),
        NOW() - INTERVAL FLOOR(RAND() * 365) DAY,
        NOW() - INTERVAL FLOOR(RAND() * 365) DAY
FROM
    (SELECT @rownum:=0) t0,
    (SELECT @rownum:=@rownum+1 AS n FROM information_schema.COLUMNS LIMIT 10000) t1 ;


CREATE TABLE `order_item` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
                              `order_id` int(11) NOT NULL COMMENT '订单ID',
                              `product_id` int(11) NOT NULL COMMENT '商品ID',
                              `quantity` int(11) NOT NULL COMMENT '购买数量',
                              `price` decimal(10,2) NOT NULL COMMENT '商品单价',
                              `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `order_id` (`order_id`),
                              KEY `product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

INSERT INTO order_item (order_id, product_id, quantity, price, created_at, updated_at)
SELECT
        FLOOR(RAND() * 10000) + 1,
        FLOOR(RAND() * 1000) + 1,
        FLOOR(RAND() * 10) + 1,
        ROUND(RAND() * 1000, 2),
        NOW() - INTERVAL FLOOR(RAND() * 365) DAY,
        NOW() - INTERVAL FLOOR(RAND() * 365) DAY
FROM
    (SELECT @rownum:=0) t0,
    (SELECT @rownum:=@rownum+1 AS n FROM information_schema.COLUMNS LIMIT 10000) t1 ;