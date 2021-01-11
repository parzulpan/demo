# MySQL高级 索引优化分析

## SQL 的效率问题

出现性能下降，SQL 执行慢，执行时间长，等待时间长等情况，可能的原因有：

* 查询语句写的不好
* 索引失效
  * 单值索引：在 user 表中给 name 属性建索引 `create index idx_user_name on user(name);`
  * 复合索引：在 user 表中给 name、email 属性索引 
* 由于设计缺陷或业务需求，导致关联查询太多表连接
* 配置文件参数设置以及调优影响

## 常见的连接查询

### SQL 执行顺序

书写的 SQL 语句顺序：

```sql
select distinct <select_list>
from <left_table> <join_type>
join <right_table>
on <join_condition>
where <where_condition>
group by <group_by_list>
having <having_condition>
order by <order_by_condition>
limit <limit_number>
```

实际的 SQL 执行顺序（常见的）：

```sql
from <left_table>
on <join_condition>
<join_type> join <right_table>
where <where_condition>
group by <group_by_list>
having <having_condition>
select distinct <select_list>
order by <order_by_condition>
limit <limit_number>
```

总结可知，实际的执行顺序是从 `form` 开始执行的。可以有下图形象表示：

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210111123351%E5%AE%9E%E9%99%85%E7%9A%84%20SQL%20%E6%89%A7%E8%A1%8C%E9%A1%BA%E5%BA%8F.png" alt="实际的 SQL 执行顺序" style="zoom:67%;" />

### 连接查询

对于多表连接的问题，**一张图**就够了。`left join` 意思是左边的全保留，如果左边的和右边的不符合 `on` 的条件，那么左边对应右边的列自动为 `null`。`right join` 同理。

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210111122323%E5%A4%9A%E8%A1%A8%E8%81%94%E7%BB%93%E9%97%AE%E9%A2%98.png" alt="多表联结问题" style="zoom: 67%;" />

#### 建表测试

```sql
create database MySQLTest;

use MySQLTest;

drop table if exists tbl_dept;
CREATE TABLE tbl_dept(
                         id INT(11) NOT NULL AUTO_INCREMENT,
                         deptName VARCHAR(30) DEFAULT NULL,
                         locAdd VARCHAR(40) DEFAULT NULL,
                         PRIMARY KEY(id)
)ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table if exists tbl_emp;
CREATE TABLE tbl_emp (
                         id INT(11) NOT NULL AUTO_INCREMENT,
                         NAME VARCHAR(20) DEFAULT NULL,
                         deptId INT(11) DEFAULT NULL,
                         PRIMARY KEY (id),
                         KEY fk_dept_Id (deptId)
    #CONSTRAINT 'fk_dept_Id' foreign key ('deptId') references 'tbl_dept'('Id')
)ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO tbl_dept(deptName,locAdd) VALUES('RD',11);
INSERT INTO tbl_dept(deptName,locAdd) VALUES('HR',12);
INSERT INTO tbl_dept(deptName,locAdd) VALUES('MK',13);
INSERT INTO tbl_dept(deptName,locAdd) VALUES('MIS',14);
INSERT INTO tbl_dept(deptName,locAdd) VALUES('FD',15);

INSERT INTO tbl_emp(NAME,deptId) VALUES('z3',1);
INSERT INTO tbl_emp(NAME,deptId) VALUES('z4',1);
INSERT INTO tbl_emp(NAME,deptId) VALUES('z5',1);
INSERT INTO tbl_emp(NAME,deptId) VALUES('w5',2);
INSERT INTO tbl_emp(NAME,deptId) VALUES('w6',2);
INSERT INTO tbl_emp(NAME,deptId) VALUES('s7',3);
INSERT INTO tbl_emp(NAME,deptId) VALUES('s8',4);
INSERT INTO tbl_emp(NAME,deptId) VALUES('s9',51);
```

#### 笛卡尔积

`tbl_emp` 表和 `tbl_dept` 表的笛卡尔乘积：

```sql
select * from tbl_emp, tbl_dept;
```

可以得知结果集个数为 `5 * 8 = 40` 

#### inner join

`tbl_emp` 表 和 `tbl_dept` 表的公共部分（交集）：

```sql
select * from tbl_emp e inner join tbl_dept d on e.deptId = d.id;
```

#### left join

`tbl_emp` 表和 `tbl_dept` 表的 公共部分 **加上** `tbl_emp` 表的独有部分：

```sql
select * from tbl_emp e left join tbl_dept d on e.deptId = d.id;
```

#### left join without common part

`tbl_emp` 表的独有部分：

```sql
select * from tbl_emp e left join tbl_dept d on d.deptId = d.id where d.id is null;
```

#### right join

`tbl_emp` 表和 `tbl_dept` 表的 公共部分 **加上** `tbl_dept` 表的独有部分：

```sql
select * from tbl_emp e right join tbl_dept d on e.deptId = d.id;
```

#### right join without common part

`tbl_dept` 表的独有部分：

```sql
select * from tbl_emp e right join tbl_dept d on d.deptId = d.id where e.id is null;
```

#### full join

遗憾的是，MySQL 不支持 `full join` ，但是可以通过 `left join` **union 联合** `right join` 实现。

union 用于连接结果集，并且自动去重。

`tbl_emp` 表和 `tbl_dept` 表的 公共部分 **加上** `tbl_emp` 表的独有部分 **加上** `tbl_dept` 表的独有部分：

```sql
select * from tbl_emp e left join tbl_dept d on e.deptId = d.id
union
select * from tbl_emp e right join tbl_dept d on e.deptId = d.id;
```

#### full join without common part

`tbl_emp` 表的独有部分  **加上** `tbl_dept` 表的独有部分：

```sql
select * from tbl_emp e left join tbl_dept d on e.deptId = d.id where d.id is null
union
select * from tbl_emp e right join tbl_dept d on e.deptId = d.id; where e.od id null;
```

## 索引简介

### 索引概念

* 索引是一种用于快速查询和检索数据的**数据结构**，需要占据**物理空间**，它们包含着对数据表所有记录的引用指针。常见的索引结构有: **B树**、**B+树** 和 **Hash**。
* 平时所说的索引，如果没有特别指明，都是指 B树 结构的索引。
* 聚集索引、次要索引、覆盖索引、复合索引、前缀索引、唯一索引默认都是使用 B+树 结构的索引，统称索引。
* 通俗的讲，索引就相当于书的目录，为了方便查找书中的内容，可以通过对内容建立索引形成目录。

### 索引原理

* 除了数据之外，数据库还维护着满足特定查找算法的数据结构，**这些数据结构以某种方式引用或者指向数据**，这样就可以在这些数据结构上实现高级查找算法，加快执行速度。这里的数据结构就是我们常说的索引。

* 一种可能的索引形式，如下图：

  * 左边是数据表，表的最左边的十六进制数字是数据记录的物理位置。

  * 为了加快 Col2 的查找，可以维护一个右边所示的二叉查找树，**每个节点分别包含着索引键值和一个指向对应数据记录物理地址的指针**，这样就可以运用 **二叉查找** 在一定的复杂度内获取到相应数据，从而快速的检索出符合条件的记录。

    <img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210111132240%E5%8F%AF%E8%83%BD%E7%9A%84%E7%B4%A2%E5%BC%95%E5%BD%A2%E5%BC%8F.png" alt="可能的索引形式" style="zoom:67%;" />

### 索引优缺点

* **优点**：
  * 可以提高数据检索效率，降低数据库的 IO 成本。
  * 可以降低数据排序成本，降低 CPU 的消耗。
* **缺点**：
  * 索引实际上是一张表，它保存了主键和索引字段，并指向实体表。它也是需要占据物理空间的。
  * 会降低更新表的速度，对表进行增删改操作，MySQL 不仅要保存数据，而且还有更新索引。
  * 对于大数据量的表，建立良好的索引是比较困难的。

### 索引分类

* **普通索引**：最基本的索引类型，没有什么特别的限制。
  * `ALTER TABLE table_name ADD INDEX index_name (column(length));` 创建普通索引
  * `ALTER TABLE table_name ADD INDEX index_name (column1(length), colimn2(length));` 创建普通组合索引
* **唯一索引**：数据列不能重复，能为 NULL，一个表可以有多个唯一索引。
  * `ALTER TABLE table_name ADD UNIQUE  (column(length));` 创建唯一索引
  * `ALTER TABLE table_name ADD UNIQUE (column1(length), column2(length));` 创建唯一组合索引
* **主键索引**：数据列不能重复，不能为 NULL，一个表只能有一个主键索引。
  * `ALTER TABLE table_name ADD PRIMARY KEY (column(length));` 创建主键索引
* **组合索引**：
  * `ALTER TABLE table_name ADD INDEX index_title_time (title(length), time(length));` 创建组合索引
  * 这个组合索引，相当于  `title, time` 和 `title` 两种索引。
  * 为什么没有 `time` 这个索引呢？这是因为 MySQL 组合索引 **最左前缀的规则** ，即 **只从最左面的开始组合，并不是只要包含这两列的查询都会用到该组合索引**。
* **全文索引**：目前搜索引擎关键的技术，MyISAM 支持，InnoDB 不支持。
  * `ALTER TABLE table_name ADD FULLTEXT (column(length));` 创建全文索引

### 索引使用

* **创建索引**：
  * 如果是 CHAR 和 VARCHAR 类型，length 可以小于字段实际长度。
  * 如果是 BLOB 和 TEXT 类型，必须指定 length。
  * **直接创建** `create index_type [index_name] on table(column_name(length))`
  * **修改表结构创建** `alter table table_name add index_type [index_name] (column_name)`
  * **创建表时同时创建** ` create table xx (index_type [index_name] (column_name(length)))`
* 删除索引：
  * **直接删除**  drop index 索引名 on 表名
  * **改表结构删除** alter table 表名 drop index 索引名
* 查看索引：
  * `EXPLAIN select * from `index_demo` d where d.e_name = 'Jane';`  查看 SQL 语句对索引的使用情况
  * `show index from 表名;` 查看已创建的索引

### 索引结构



### 索引使用时机



## 性能分析

## 索引优化

## 总结和练习

