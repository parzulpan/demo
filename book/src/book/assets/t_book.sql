drop table t_book;

create table t_book(
    `id` int primary key auto_increment,
    `name` varchar(200),
    `author` varchar(100),
    `price` decimal(11, 2),
    `sales` int,
    `stock` int,
    `imgPath` varchar(200)
);

insert into t_book(`id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath`) value
    (null , 'Java 从入门到放弃' , '大哥' , 80 , 9999 , 9 , 'static/img/default.png'),
    (null , '数据结构与算法' , '严敏君' , 78.5 , 6 , 13 , 'static/img/default.png'),
    (null , '怎样拐跑别人的媳妇' , '龙伍' , 68, 99999 , 52 , 'static/img/default.png'),
    (null , 'C++编程思想' , '二哥' , 45.5 , 14 , 95 , 'static/img/default.png'),
    (null , '蛋炒饭' , '周星星' , 9.9, 12 , 53 , 'static/img/default.png'),
    (null , '赌神' , '龙伍' , 66.5, 125 , 535 , 'static/img/default.png'),
    (null , 'Java编程思想' , '阳哥' , 99.5 , 47 , 36 , 'static/img/default.png'),
    (null , 'JavaScript从入门到精通' , '婷姐' , 9.9 , 85 , 95 , 'static/img/default.png'),
    (null , 'Cocos2d-x游戏编程入门' , '大哥' , 49, 52 , 62 , 'static/img/default.png'),
    (null , 'C语言程序设计' , '谭浩强' , 28 , 52 , 74 , 'static/img/default.png'),
    (null , 'Lua语言程序设计' , '雷丰阳' , 51.5 , 48 , 82 , 'static/img/default.png'),
    (null , '西游记' , '罗贯中' , 12, 19 , 9999 , 'static/img/default.png'),
    (null , '水浒传' , '华仔' , 33.05 , 22 , 88 , 'static/img/default.png'),
    (null , '操作系统原理' , '刘优' , 133.05 , 122 , 188 , 'static/img/default.png'),
    (null , '数据结构 java版' , '封大神' , 173.15 , 21 , 81 , 'static/img/default.png'),
    (null , 'UNIX高级环境编程' , '乐天' , 99.15 , 210 , 810 , 'static/img/default.png'),
    (null , 'JavaScript高级编程' , '大哥' , 69.15 , 210 , 810 , 'static/img/default.png'),
    (null , '大话设计模式' , '大哥' , 89.15 , 20 , 10 , 'static/img/default.png'),
    (null , '人月神话' , '二哥' , 88.15 , 20 , 80 , 'static/img/default.png');