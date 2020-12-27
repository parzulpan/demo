# 创建产品表
drop table if exists `product`;
create table `product`(
    `id` int(12) primary key auto_increment comment '无意义 主键',
    `productNumber` varchar(50) not null comment '产品编号 唯一 不为空',
    `productName` varchar(50) comment '产品名称',
    `departureCity` varchar(50) comment '出发城市',
    `departureTime` datetime comment '出发时间',
    `productPrice` float comment '产品价格',
    `productDesc` varchar(500) comment '产品描述',
    `productStatus` int comment '产品状态 0代表关闭 1代表开启'
) engine=InnoDB default charset=utf8;

insert into product(productNumber, productName, departureCity, departureTime, productPrice, productDesc, productStatus)
values
('LY01-853424326526163464', '旅游-北京三日游', '北京', date('2019-10-24 10:24:00'), 666.8, '旅游路线：天安门-故宫-长城', 1),
('LY02-853424326526135125', '旅游-重庆五日游', '重庆', date('2020-01-01 08:24:00'), 965.8, '旅游路线：朝天门-解放碑-洪崖洞-长江', 1),
('LY03-853424326526135125', '旅游-大连二日游', '大连', date('2021-01-01 08:24:00'), 565.8, '旅游路线：红海滩-大连海', 0);