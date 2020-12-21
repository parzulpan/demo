use springT;

# ---
# 银行账户表结构

drop table if exists `bankAccount`;

create table `bankAccount`(
    `id` int(11) primary key auto_increment,
    `name` varchar(32) not null comment '账户名称',
    `money` float not null comment '账户余额'
) engine=InnoDB default charset=utf8;

insert into bankAccount(name, money) values
('aaa', 1000), ('bbb', 2000), ('ccc', 3000);

# ---

