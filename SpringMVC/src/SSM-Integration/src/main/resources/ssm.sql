drop database if exists ssm;

create database ssm;
use ssm;

# ---

drop table if exists `user`;

create table `user`(
                       id int(11) primary key auto_increment,
                       username varchar(30) not null comment '用户名',
                       password varchar(30) not null comment '密码'
) engine=InnoDB default charset=utf8;

insert into user(username, password) VALUES
('admin', 'admin11002244'), ('parzulpan', '12345678');

# ----