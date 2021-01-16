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

* 原理大致就是把无序的数据变成有序的查询。具体过程为，把创建索引的列的内容进行排序，对排序结果生成倒排表，在倒排表上拼接上数据地址链。在查询的时候，先拿到倒排表内容，再取出数据地址链，从而拿到具体数据。

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

#### B 树

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210112023607B%E6%A0%91%E7%B4%A2%E5%BC%95%E7%BB%93%E6%9E%84.png" alt="B树索引结构" style="zoom:67%;" />

**结构初始化**：

1. 如上图所示，浅蓝色的块称之为**磁盘块**，每个磁盘块包含几个**数据项**（深蓝色的块）和**指针**（黄色的块）。
2. 例如磁盘块 1 就包含着数据项 17 和 35，和包含指针 P1、P2、P3。其中 P1 表示小于 17 的磁盘块，P2 表示 17 和 35 之间的磁盘块，P3 表示大于 35 的磁盘块。
3. 真实的数据存在于**叶子节点**和**非叶子节点**中。

**查找过程**：

1. 以查找数据项 29 为例。
2. 那么首先会把磁盘块 1 由磁盘加载到内存，此时发生一次 **磁盘IO**，在内存中使用二分查找确定 29 在 17 和 35 之间，锁定磁盘块1 的 P2 指针。因为相比于磁盘 IO而言，内存时间非常短，可以忽略不记。
3. 然后通过磁盘块 1 的 P2 指针指向的磁盘地址将 磁盘块 3 由磁盘加载到内存，此时发生一次 **磁盘IO**，同理，锁定磁盘块3 的 P2 指针。
4. 最后通过磁盘块 3 的 P2 指针指向的磁盘地址将 磁盘块 8 由磁盘加载到内存，此时发生一次 **磁盘IO**，在内存中查找到 29，结束查询。
5. 可知，一共发生了三次磁盘 IO。



#### B+ 树

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210112023730BPlus%E6%A0%91%E7%B4%A2%E5%BC%95%E7%BB%93%E6%9E%84.png" alt="BPlus树索引结构" style="zoom:67%;" />

**结构初始化**：

1. 如上图所示，和 B树 不同的是，B树 的关键字和记录是放在一起的，而 B+树 的非叶子节点中只有关键字和指向下一个节点的索引，记录只放在叶子节点中。

**查找过程**：

1. 在 B树 中，越靠近根节点的记录的查找时间越短，因为只要找到关键字即可确定记录的存在；而 B+树 中，每个记录的查找时间基本是相同的，因为都需要从根节点走到叶子节点，而且在叶子节点中还要再次比较关键字。
2. 从上面这个角度来说，B树 的性能好像要高于 B+ 树，但是在实际应用中 B+ 树的性能要更好。
3. 原因一：因为 B+树 的非叶子节点不存放记录，这样的话每个节点可容纳的元素个数就比 B树 多，即树高比 B树 小，这样能减少 磁盘IO 次数。尽管 B+树 查找一次记录的比较次数多于 B树，但是一次 磁盘 IO 消耗的时间远大于成百上千次内存的比较。
4. 原因二：B+树 的叶子节点使用指针连接在一起，可以很方便进行顺序遍历。
5. 性能提升的真实情况：3 层的 B+树 可以表示上百万的数据， 如果上百万的数据查找只需要三次 磁盘IO， 性能提高将是巨大的，如果没有索引， 每个数据项都要发生一次 IO， 那么总共需要百万次的 IO， 显然成本非常非常高。

**为什么 B+树 比 B树 更适合实际应用中操作系统的文件索引和数据库索引？**

主要从两个方面考虑：

1. **B+树 的 磁盘IO 代价更低**：如上所述，如果磁盘块所能容纳的关键字数量越多，则一次性读入内存中的需要查找的关键字也就越多，即 磁盘IO 的次数也就越少。
2. B+树 的查询效率更加稳定：每个记录的查找时间基本是相同的，因为都需要从根节点走到叶子节点，即 每一个数据的查询效率大致相同。

### 索引使用时机

适合创建索引的字段：

* 非空字段，即指定列为 `NOT NULL`
* 被频繁用于查询的字段
* 被作为条件查询的字段
* 被频繁用于连接的字段
* 被经常排序的字段

不适合创建索引的字段：

* 被频繁更新的字段
* 不被频繁用于查询的字段

举例分析：

1. 假如一个表有 10 万行记录，有一个字段 A 只有 T 和 F 两种值，且每个值的分布概率大约为 50%，那么对这种表 A字段 建索引一般不会提高数据库的查询速度。
2. **索引的选择性**是指索引列中不同值的数目与表中记录数的比。如果一个表中有2000条记录，表索引列有1980个不同的值，那么这个索引的选择性就是1980/2000=0.99。一个索引的选择行越接近 1，这个索引的效率就越高。

## 性能分析

MySQL 中有抓门负责优化 查询语句 的**优化器**模块，它的主要功能是通过计算分析系统收集到的统计信息，为客户端请求的 Query 提供它认为最优的执行计划，但是注意可能不是实际生产中最好的。

当客户端向 MySQL 请求一条 Query，**命令解析器**模块完成请求分类，区分出 查询 并转发给 MySQL Query Optimizer。MySQL Query Optimizer 会对整条 Query 进行优化，处理掉一些常量表达式的预算，直接换算成常量值。并对 Query 中的查询条件进行简化和转化，例如去掉一些无用的条件、结构调用等。然后分析 Query 中的 **提示信息**，看能够根据提示信息完全确定执行计划，如果没有提示信息或者不能完全确定，则会读取所涉及对象的统计信息，根据 Query 进行相应的计算分析，最后得出执行计划。

可以使用 **EXPLAIN 关键字** 模拟优化器执行 SQL 语句，可以知道 MySQL 是如何处理你的 SQL 语句的，这样可以帮助分析查询语句或者表结构的性能瓶颈。它主要能得到以下字段：

* id：表的读取顺序
* select_type：数据读取操作的操作类型
* table：这是关于那一张表的数据
* possible_keys：可能使用的索引
* keys：实际使用的索引
* ref：表之间的引用关系
* rows：每张表被优化器查询的行数

举例：

```sql
mysql> explain select * from tbl_emp;
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------+
| id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------+
|  1 | SIMPLE      | tbl_emp | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    8 |   100.00 | NULL  |
+----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-------+
1 row in set, 1 warning (0.03 sec)
```

### id

**select 查询的序列号，包含一组数字，表示查询中执行 select 子句或操作表的顺序**。id 的取值有三种情况：

* id 相同。执行顺序从上往下。
* id 不同。如果是子查询，id 的序号会递增，**id 值越大，优先级越高，越先被执行**。
* id 相同和不同都存在。id 相同可以被认为是组，执行顺序从上往下。在所有组中，id 值越大，优先级越高，越先被执行。

### select_type

查询的类型，主要用于区别普通查询、联合查询、子查询等复杂查询。select_type 取值主要有：

* SIMPLE：简单的 select 查询，查询中不包含子查询或者 UNION。
* PRIMARY：查询中包含任何复杂的子部分，最外层查询则被标记为 PRIMARY。
* SUBQUERY：在 SELECT 或者 WHERE 列表中包含了 子查询。
* DERIVED：在 FROM 列表中包含的子查询被标记为 DERIVED 衍生，MySQL 会递归执行这些子查询，并把结果放在临时表中。
* UNION：若第二个 SELECT 出现在 UNION 之后，则被标记为 UNION；若 UNION 包含在 FROM 子句的查询中，外层的 SELECT 将被标记为 DERIVED。
* UNION RESULT：从 UNION 表获取结果的 SELECT。

例如 full join：

```sql
mysql> explain select * from tbl_emp e left join tbl_dept d on e.deptId = d.id union select * from tbl_emp e right join tbl_dept d on e.deptId = d.id;
+----+--------------+------------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
| id | select_type  | table      | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra                                              |
+----+--------------+------------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
|  1 | PRIMARY      | e          | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    8 |   100.00 | NULL                                               |
|  1 | PRIMARY      | d          | NULL       | ALL  | PRIMARY       | NULL | NULL    | NULL |    5 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
|  2 | UNION        | d          | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    5 |   100.00 | NULL                                               |
|  2 | UNION        | e          | NULL       | ALL  | fk_dept_Id    | NULL | NULL    | NULL |    8 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
| NULL | UNION RESULT | <union1,2> | NULL       | ALL  | NULL          | NULL | NULL    | NULL | NULL |     NULL | Using temporary                                    |
+----+--------------+------------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
5 rows in set, 1 warning (0.00 sec)
```

### table

表示这是关于那一张表的数据。

### partitions



### type

访问类型排列，显示查询使用了何种类型。

type 显示的是访问类型，是**很重要的一个指标，**结果值从最好到最坏依次是：

* `system > const > eq_ref > ref > fultext > ref_or_null > index_merge > unique_subquery > index_subquery > range > index > all`
* 比较重要的是：`system > const > eq_ref > ref > range > index > all`，一般来说，要保证查询至少是 range，最好是 ref。
* system：表示表只有一行记录，即系统表，是 const 类型的特例。
* const：表示通过索引一次就找到了，const 用于比较 primary key 或者 unique 索引。如果将主键置于 where 列表中，MySQL 就能将该查询转换为一个常量。
* eq_ref：唯一性索引，对于每个索引键，表中只有一条记录与之匹配，常见于主键或唯一索引扫描。
* ref：非唯一性索引，返回匹配某个单独值的所有行。本质上也是一种索引访问，它返回所有匹配某一个单独值的行。
* range：只检索给定范围的行，使用一个索引来选择行。
* index：全索引扫描，与 all 的区别为它扫描索引树，而 all 扫描全表。
* all：全表扫描。

### possible_keys

可能使用的索引，一个或多个，但不一定实际使用。

### key

实际使用的索引，如果为 null，则表示没有使用索引。

### key_len

实际使用的索引的字节数，可通过它计算出使用索引的长度，在不损失精度的情况下，长度越短越好。

值得注意的是，它表示的是**最大可能长度**，而并实际使用长度，即它是根据表定义计算而得，不是通过表内检索而得。

### ref

表之间的引用关系，那些列或者常量被用于查找索引列上的值。最好的情况是一个常数。

### rows

根据表统计信息以及索引选用情况，大致算出每张表被优化器查询的行数。

### filtered



### Extra

包含不适合在其他列中显示但十分重要的额外信息。主要有：

1. Using filesort：
   1. MySQL 中无法利用索引完成的排序称为“**文件排序**”；
   2. 说明 MySQL 会对数据使用一个外部的索引排序，而不是按照表内的索引顺序，这是**比较坏的情况**，**需要尽快优化 SQL**。
2. Using temporary：
   1. MySQL 在对查询结果排序时使用了“**临时表**”；
   2. 常见于排序 order by 和 分组查询 group by，这是**超级坏的情况**，**需要立即优化 SQL**。
3. Using index：
   1. MySQL 进行 select 操作中使用“**覆盖索引（Conveing index）**”；
   2. 避免了访问表的数据行，这是**比较好的情况**。如果同时出现 Using where 的情况，则索引被用来执行索引键值的查找。如果没有出现 Using where 的情况，则索引被用来读取数据而非执行查找动作。
   3. **怎么理解覆盖索引？**
      1. 可以理解为 **select 的数据列只用从索引中就能获得，而不必读取数据行**，即查询列要被所建的索引覆盖。
      2. 也可以理解为 **一个索引包含/覆盖了满足查询结果的数据**就叫做覆盖索引。
      3. 值得注意的是，**如果要使用覆盖索引，一定要注意select列表中只取出需要的列，不可`select *`** 。
4. Using where：
   1. MySQL 使用了 where 条件过滤。
5. Using join buffer：
   1. MySQL 使用了 连接缓存。
6. impossible where：
   1. 说明 where 子句的值总是 false，不能用来获取到任何数据。
7. select tables optimized away：
   1. 在没有 group by 子句的情况下，基于索引优化 MIN/MAX 操作或者对于 MyISAM 存储引擎优化`count(*)`操作，不必等到执行阶段再进行计算，查询执行计划生成的阶段即完成优化。
8. distinct：
   1. 对于 distinct，MySQL 在找到第一个匹配的原则后立即停止找同样值的工作。

## 索引优化

### 索引原则

**最左前缀原则**：指的是最左优先，在创建列的索引时，要根据业务需求，将 WHERE 子句中使用最频繁的一列放在最左边。

**最左匹配原则**：指的是 MySQL 会一直向右匹配直到遇到范围查询（`> < between like` 等）就停止匹配，即索引失效。

### 索引失效

以下情况索引将失效：

* WHERE 子句中的查询条件使用了不等于号，例如 `where age != 100`
* WHERE 子句中的查询条件使用了函数，例如 `where day(age) = 5`
* WHERE 子句中的查询条件使用了 NULL 判断，例如 `where age not is null`
* 在连接操作中，主键和外键的数据类型不相同

### 单表索引优化

#### 建表测试

```sql
# create database MySQLTest;

use MySQLTest;

drop table if exists article;
CREATE TABLE IF NOT EXISTS article(
                                      id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                      author_id INT(10) UNSIGNED NOT NULL,
                                      category_id INT(10) UNSIGNED NOT NULL,
                                      views INT(10) UNSIGNED NOT NULL,
                                      comments INT(10) UNSIGNED NOT NULL,
                                      title VARCHAR(255) NOT NULL,
                                      content TEXT NOT NULL
);

INSERT INTO article(author_id,category_id,views,comments,title,content)
VALUES
(1,1,1,1,'1','1'),
(2,2,2,2,'2','2'),
(1,1,3,3,'3','3');
```

#### 查询案例

* 查询 category_id 为 1 且 comments 大于 1 的情况下，views 最多的 article_id：

  ```sql
  mysql> select id, author_id from article where category_id = 1 and comments > 1 order by views desc limit 1;
  +----+-----------+
  | id | author_id |
  +----+-----------+
  |  3 |         1 |
  +----+-----------+
  1 row in set (0.00 sec)
  ```

* 查看表索引情况：

  ```sql
  mysql> show index from article;
  +---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table   | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | article |          0 | PRIMARY  |            1 | id          | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  +---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  1 row in set (0.00 sec)
  ```

* 使用 explain 分析 SQL 语句的执行效率

  ```sql
  mysql> explain select id, author_id from article where category_id = 1 and comments > 1 order by views desc limit 1;
  +----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-----------------------------+
  | id | select_type | table   | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra                       |
  +----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-----------------------------+
  |  1 | SIMPLE      | article | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    3 |    33.33 | Using where; Using filesort |
  +----+-------------+---------+------------+------+---------------+------+---------+------+------+----------+-----------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 可以看到 type 是 All，这是**最坏的情况**；

* 而且 extra 出现了 Using filesort，这也是**比较坏的情况**。

#### 优化流程

##### 新建索引

* 在 category_id 、comments、views 列上建立联合索引：

  ```sql
  mysql> create index idx_article_ccv on article(category_id, comments, views);
  Query OK, 0 rows affected (0.02 sec)
  Records: 0  Duplicates: 0  Warnings: 0
  
  mysql> show index from article;
  +---------+------------+-----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table   | Non_unique | Key_name        | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +---------+------------+-----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | article |          0 | PRIMARY         |            1 | id          | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  | article |          1 | idx_article_ccv |            1 | category_id | A         |           2 |     NULL | NULL   |      | BTREE      |         |               |
  | article |          1 | idx_article_ccv |            2 | comments    | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  | article |          1 | idx_article_ccv |            3 | views       | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  +---------+------------+-----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  4 rows in set (0.00 sec)
  ```

* 再次使用 explain 分析 SQL 语句的执行效率：

  ```sql
  mysql> explain select id, author_id from article where category_id = 1 and comments > 1 order by views desc limit 1;
  +----+-------------+---------+------------+-------+-----------------+-----------------+---------+------+------+----------+---------------------------------------+
  | id | select_type | table   | partitions | type  | possible_keys   | key             | key_len | ref  | rows | filtered | Extra                                 |
  +----+-------------+---------+------------+-------+-----------------+-----------------+---------+------+------+----------+---------------------------------------+
  |  1 | SIMPLE      | article | NULL       | range | idx_article_ccv | idx_article_ccv | 8       | NULL |    1 |   100.00 | Using index condition; Using filesort |
  +----+-------------+---------+------------+-------+-----------------+-----------------+---------+------+------+----------+---------------------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 可以看到，type 现在是 range，这是可以接受的。但是为什么 extra 还是 Using filesort 呢？

* 这是因为，根据 B树索引的工作原理，会先排序 category_id，如果遇到相同的 category_id 再排序 comments，如果遇到相同的 comments 再排序 views。而 comments > 1 处于联合索引的中间位置，根据 最左匹配原则，此时索引会失效，即 views 部分是无法使用索引的。

##### 删除索引

* 知道了问题的所在，可以先删除索引：

  ```sql
  mysql> drop index idx_article_ccv on article;
  Query OK, 0 rows affected (0.00 sec)
  Records: 0  Duplicates: 0  Warnings: 0
  
  mysql> show index from article;
  +---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table   | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | article |          0 | PRIMARY  |            1 | id          | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  +---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  1 row in set (0.00 sec)
  ```

##### 再建索引

* 不为 comments 列建立索引：

  ```sql
  mysql> create index idx_article_cv on article(category_id, views);
  Query OK, 0 rows affected (0.01 sec)
  Records: 0  Duplicates: 0  Warnings: 0
  mysql> show index from article;
  +---------+------------+----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table   | Non_unique | Key_name       | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +---------+------------+----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | article |          0 | PRIMARY        |            1 | id          | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  | article |          1 | idx_article_cv |            1 | category_id | A         |           2 |     NULL | NULL   |      | BTREE      |         |               |
  | article |          1 | idx_article_cv |            2 | views       | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
  +---------+------------+----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  3 rows in set (0.00 sec)
  ```

* 再次使用 explain 分析 SQL 语句的执行效率：

  ```sql
  mysql> explain select id, author_id from article where category_id = 1 and comments > 1 order by views desc limit 1;
  +----+-------------+---------+------------+------+----------------+----------------+---------+-------+------+----------+-------------+
  | id | select_type | table   | partitions | type | possible_keys  | key            | key_len | ref   | rows | filtered | Extra       |
  +----+-------------+---------+------------+------+----------------+----------------+---------+-------+------+----------+-------------+
  |  1 | SIMPLE      | article | NULL       | ref  | idx_article_cv | idx_article_cv | 4       | const |    2 |    33.33 | Using where |
  +----+-------------+---------+------------+------+----------------+----------------+---------+-------+------+----------+-------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 可以看到，效果非常的理想。为了不影响后面的使用，这是还是删除该表的 `idx_aarticle_cv` 索引。

### 双表索引优化

#### 建表测试

```sql
# create database MySQLTest;

use MySQLTest;

drop table if exists class;
CREATE TABLE IF NOT EXISTS class(
                                    id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                                    card INT(10) UNSIGNED NOT NULL,
                                    PRIMARY KEY(id)
);

drop table if exists book;
CREATE TABLE IF NOT EXISTS book(
                                   bookid INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                                   card INT(10) UNSIGNED NOT NULL,
                                   PRIMARY KEY(bookid)
);

INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO class(card) VALUES(FLOOR(1+(RAND()*20)));

INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO book(card) VALUES(FLOOR(1+(RAND()*20)));
```

#### 查询案例

* 两表左连接查询：

  ```sql
  mysql> select * from class left join book on class.card = book.card;
  +----+------+--------+------+
  | id | card | bookid | card |
  +----+------+--------+------+
  |  1 |    1 |      1 |    1 |
  |  1 |    1 |      2 |    1 |
  |  7 |   20 |      3 |   20 |
  | 16 |   20 |      3 |   20 |
  | 20 |   20 |      3 |   20 |
  |  1 |    1 |      8 |    1 |
  |  4 |    8 |      9 |    8 |
  | 21 |    8 |      9 |    8 |
  |  7 |   20 |     11 |   20 |
  | 16 |   20 |     11 |   20 |
  | 20 |   20 |     11 |   20 |
  |  4 |    8 |     12 |    8 |
  | 21 |    8 |     12 |    8 |
  |  7 |   20 |     13 |   20 |
  | 16 |   20 |     13 |   20 |
  | 20 |   20 |     13 |   20 |
  |  6 |    4 |     16 |    4 |
  | 19 |    4 |     16 |    4 |
  |  8 |   10 |     18 |   10 |
  |  7 |   20 |     19 |   20 |
  | 16 |   20 |     19 |   20 |
  | 20 |   20 |     19 |   20 |
  |  2 |    3 |   NULL | NULL |
  |  3 |   12 |   NULL | NULL |
  |  5 |    6 |   NULL | NULL |
  |  9 |    7 |   NULL | NULL |
  | 10 |    6 |   NULL | NULL |
  | 11 |    7 |   NULL | NULL |
  | 12 |   19 |   NULL | NULL |
  | 13 |   12 |   NULL | NULL |
  | 14 |    3 |   NULL | NULL |
  | 15 |   18 |   NULL | NULL |
  | 17 |    7 |   NULL | NULL |
  | 18 |   13 |   NULL | NULL |
  +----+------+--------+------+
  34 rows in set (0.00 sec)
  ```

* 使用 explain 分析 SQL 语句的执行效率：

  ```sql
  mysql> explain select * from class left join book on class.card = book.card;
  +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
  | id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra                                              |
  +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
  |  1 | SIMPLE      | class | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   21 |   100.00 | NULL                                               |
  |  1 | SIMPLE      | book  | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   20 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
  +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
  2 rows in set, 1 warning (0.00 sec)
  ```

* 可以看到 type 是 All，这是**最坏的情况**；

* 而且 rows 为表中数据的总行数，说明 class 和 book 进行了全表检索。

#### 优化流程

##### 添加索引

* 在 book 的 card 字段上添加索引：

  ````sql
  mysql> alter table book add index idx_book(card);
  Query OK, 0 rows affected (0.01 sec)
  Records: 0  Duplicates: 0  Warnings: 0
  
  mysql> show index from book;
  +-------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +-------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | book  |          0 | PRIMARY  |            1 | bookid      | A         |          20 |     NULL | NULL   |      | BTREE      |         |               |
  | book  |          1 | idx_book |            1 | card        | A         |          11 |     NULL | NULL   |      | BTREE      |         |               |
  +-------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  2 rows in set (0.00 sec)
  ````

* 再次使用 explain 分析 SQL 语句的执行效率：

  ```sql
  mysql> explain select * from class left join book on class.card = book.card;
  +----+-------------+-------+------------+------+---------------+----------+---------+----------------------+------+----------+-------------+
  | id | select_type | table | partitions | type | possible_keys | key      | key_len | ref                  | rows | filtered | Extra       |
  +----+-------------+-------+------------+------+---------------+----------+---------+----------------------+------+----------+-------------+
  |  1 | SIMPLE      | class | NULL       | ALL  | NULL          | NULL     | NULL    | NULL                 |   21 |   100.00 | NULL        |
  |  1 | SIMPLE      | book  | NULL       | ref  | idx_book      | idx_book | 4       | MySQLTest.class.card |    1 |   100.00 | Using index |
  +----+-------------+-------+------------+------+---------------+----------+---------+----------------------+------+----------+-------------+
  2 rows in set, 1 warning (0.00 sec)
  ```

* 可以看到 book 表的 type 是 ref，这是可以接受的；

* 而且 book 表的  rows 为 1，说明没有进行全表检索；

* **总结**：左连接，是拿着左表的数据去右表里面查，所以索引需要在右表中建立。右连接同理。

* 效果非常的理想。为了不影响后面的使用，这是还是删除该表的 `idx_book` 索引。

### 三表索引优化

#### 建表测试

```sql
# create database MySQLTest;

use MySQLTest;

drop table if exists phone;
CREATE TABLE IF NOT EXISTS phone(
                                    phoneid INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                                    card INT(10) UNSIGNED NOT NULL,
                                    PRIMARY KEY(phoneid)
)ENGINE=INNODB;

INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
INSERT INTO phone(card) VALUES(FLOOR(1+(RAND()*20)));
```

#### 查询案例

* 三表左连接查询：

  ```sql
  mysql> select * from class left join book on class.card = book.card left join phone on book.card = phone.card;
  +----+------+--------+------+---------+------+
  | id | card | bookid | card | phoneid | card |
  +----+------+--------+------+---------+------+
  |  8 |   10 |     18 |   10 |       2 |   10 |
  |  6 |    4 |     16 |    4 |       8 |    4 |
  | 19 |    4 |     16 |    4 |       8 |    4 |
  |  7 |   20 |      3 |   20 |       9 |   20 |
  | 16 |   20 |      3 |   20 |       9 |   20 |
  | 20 |   20 |      3 |   20 |       9 |   20 |
  |  7 |   20 |     11 |   20 |       9 |   20 |
  | 16 |   20 |     11 |   20 |       9 |   20 |
  | 20 |   20 |     11 |   20 |       9 |   20 |
  |  7 |   20 |     13 |   20 |       9 |   20 |
  | 16 |   20 |     13 |   20 |       9 |   20 |
  | 20 |   20 |     13 |   20 |       9 |   20 |
  |  7 |   20 |     19 |   20 |       9 |   20 |
  | 16 |   20 |     19 |   20 |       9 |   20 |
  | 20 |   20 |     19 |   20 |       9 |   20 |
  |  4 |    8 |      9 |    8 |      10 |    8 |
  | 21 |    8 |      9 |    8 |      10 |    8 |
  |  4 |    8 |     12 |    8 |      10 |    8 |
  | 21 |    8 |     12 |    8 |      10 |    8 |
  |  7 |   20 |      3 |   20 |      11 |   20 |
  | 16 |   20 |      3 |   20 |      11 |   20 |
  | 20 |   20 |      3 |   20 |      11 |   20 |
  |  7 |   20 |     11 |   20 |      11 |   20 |
  | 16 |   20 |     11 |   20 |      11 |   20 |
  | 20 |   20 |     11 |   20 |      11 |   20 |
  |  7 |   20 |     13 |   20 |      11 |   20 |
  | 16 |   20 |     13 |   20 |      11 |   20 |
  | 20 |   20 |     13 |   20 |      11 |   20 |
  |  7 |   20 |     19 |   20 |      11 |   20 |
  | 16 |   20 |     19 |   20 |      11 |   20 |
  | 20 |   20 |     19 |   20 |      11 |   20 |
  |  6 |    4 |     16 |    4 |      14 |    4 |
  | 19 |    4 |     16 |    4 |      14 |    4 |
  |  6 |    4 |     16 |    4 |      17 |    4 |
  | 19 |    4 |     16 |    4 |      17 |    4 |
  |  1 |    1 |      1 |    1 |    NULL | NULL |
  |  1 |    1 |      2 |    1 |    NULL | NULL |
  |  1 |    1 |      8 |    1 |    NULL | NULL |
  |  2 |    3 |   NULL | NULL |    NULL | NULL |
  |  3 |   12 |   NULL | NULL |    NULL | NULL |
  |  5 |    6 |   NULL | NULL |    NULL | NULL |
  |  9 |    7 |   NULL | NULL |    NULL | NULL |
  | 10 |    6 |   NULL | NULL |    NULL | NULL |
  | 11 |    7 |   NULL | NULL |    NULL | NULL |
  | 12 |   19 |   NULL | NULL |    NULL | NULL |
  | 13 |   12 |   NULL | NULL |    NULL | NULL |
  | 14 |    3 |   NULL | NULL |    NULL | NULL |
  | 15 |   18 |   NULL | NULL |    NULL | NULL |
  | 17 |    7 |   NULL | NULL |    NULL | NULL |
  | 18 |   13 |   NULL | NULL |    NULL | NULL |
  +----+------+--------+------+---------+------+
  50 rows in set (0.00 sec)
  ```

* 使用 explain 分析 SQL 语句的执行效率：

  ```sql
  mysql> explain select * from class left join book on class.card = book.card left join phone on book.card = phone.card;
  +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
  | id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra                                              |
  +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
  |  1 | SIMPLE      | class | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   21 |   100.00 | NULL                                               |
  |  1 | SIMPLE      | book  | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   20 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
  |  1 | SIMPLE      | phone | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   20 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
  +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
  3 rows in set, 1 warning (0.00 sec)
  ```

* 可以看到 type 是 All，这是**最坏的情况**；

* 而且 rows 为表中数据的总行数，说明 class 和 book 和 phone 进行了全表检索；

* Extra 中 是 Using join buffer (Block Nested Loop)，说明在连接过程中使用了 join 缓冲区。

#### 优化流程

##### 添加索引

* 在 book 和 phone 的 card 字段上添加索引：

  ```sql
  mysql> alter table book add index idx_book_card(card);
  Query OK, 0 rows affected (0.00 sec)
  Records: 0  Duplicates: 0  Warnings: 0
  
  mysql> show index from book;
  +-------+------------+---------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table | Non_unique | Key_name      | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +-------+------------+---------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | book  |          0 | PRIMARY       |            1 | bookid      | A         |          20 |     NULL | NULL   |      | BTREE      |         |               |
  | book  |          1 | idx_book_card |            1 | card        | A         |          11 |     NULL | NULL   |      | BTREE      |         |               |
  +-------+------------+---------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  2 rows in set (0.00 sec)
  
  mysql> alter table phone add index idx_phone_card(card);
  Query OK, 0 rows affected (0.01 sec)
  Records: 0  Duplicates: 0  Warnings: 0
  
  mysql> show index from phone;
  +-------+------------+----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | Table | Non_unique | Key_name       | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
  +-------+------------+----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  | phone |          0 | PRIMARY        |            1 | phoneid     | A         |          20 |     NULL | NULL   |      | BTREE      |         |               |
  | phone |          1 | idx_phone_card |            1 | card        | A         |          14 |     NULL | NULL   |      | BTREE      |         |               |
  +-------+------------+----------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
  2 rows in set (0.00 sec)
  ```

* 使用 explain 分析 SQL 语句的执行效率：

  ```sql
  mysql> explain select * from class left join book on class.card = book.card left join phone on book.card = phone.card;
  +----+-------------+-------+------------+------+----------------+----------------+---------+----------------------+------+----------+-------------+
  | id | select_type | table | partitions | type | possible_keys  | key            | key_len | ref                  | rows | filtered | Extra       |
  +----+-------------+-------+------------+------+----------------+----------------+---------+----------------------+------+----------+-------------+
  |  1 | SIMPLE      | class | NULL       | ALL  | NULL           | NULL           | NULL    | NULL                 |   21 |   100.00 | NULL        |
  |  1 | SIMPLE      | book  | NULL       | ref  | idx_book_card  | idx_book_card  | 4       | MySQLTest.class.card |    1 |   100.00 | Using index |
  |  1 | SIMPLE      | phone | NULL       | ref  | idx_phone_card | idx_phone_card | 4       | MySQLTest.book.card  |    1 |   100.00 | Using index |
  +----+-------------+-------+------------+------+----------------+----------------+---------+----------------------+------+----------+-------------+
  3 rows in set, 1 warning (0.00 sec)
  ```

* 可以看到 book、phone 表的 type 是 ref，这是可以接受的；

* 而且 book、phone 表的  rows 为 1，说明没有进行全表检索；

* **总结**：**永远用小结果集驱动大的结果集（在大结果集中建立索引，在小结果集中遍历全表）**；

* 效果非常的理想。为了不影响后面的使用，这是还是相关索引。

## 总结和练习

**索引优化口诀**：

* 全值匹配我最爱，最左前缀要遵守；
* 带头大哥不能死，中间兄弟不能断；
* 索引列上少计算，范围之后全失效；
* `LIKE 百分` 写最右，覆盖索引不写 `*`；
* 不等空值还有 OR，索引影响要注意；
* VAR 引号不可丢，SQL 优化有诀窍。

[更多 SQL优化技巧](https://backendnote.parzulpan.cn/#/notes/DB/MySQL/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E9%A2%98%EF%BC%9ASQL%E4%BC%98%E5%8C%96)

