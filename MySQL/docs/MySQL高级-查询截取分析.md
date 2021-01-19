# MySQL高级 查询截取分析

## 排序查询

### MySQL 排序原则

#### 调优原则

1. 开启并捕获慢查询；
2. 使用 explain 分析 SQL 性能；
3. 使用 show profile 查询 SQL 执行细节和生命周期情况；
4. 进行 SQL 数据库服务器参数调优。

#### in 和 exists

* **exists 语法**：`select ... from table where exists(subQuery)`，将查询的数据放到子查询中做条件验证，根据验证结果（ture 或 false）来决定主查询的数据结果是否得以保留。

* exists 子查询的实际执行过程可能经过了优化，它也可以用条件表达式、其他子查询或者 JOIN 来替代。

* exists 和 in 的**使用原则**：

  * 永远是小表驱动大表，即小的数据集驱动大的数据集
  * 当 A 表 数据集 小于 B 表数据集时，使用 exist：`select * from A where exists (select id from B where B.id = A.id)`
  * 当 A 表 数据集 大于 B 表数据集时，使用 in：`select * from A where id in (select id from B)`

* exists 和 in 的**使用案例**：

  ```sql
  mysql> select count(*) from tbl_emp;
  +----------+
  | count(*) |
  +----------+
  |        8 |
  +----------+
  1 row in set (0.00 sec)
  
  mysql> select count(*) from tbl_dept;
  +----------+
  | count(*) |
  +----------+
  |        5 |
  +----------+
  1 row in set (0.00 sec)
  
  # 所以 tbl_emp 数据集 大于 tbl_dept
  
  # exists 使用
  mysql> select * from tbl_emp e where exists (select id from tbl_dept d where e.deptId = d.id);
  +----+------+--------+
  | id | NAME | deptId |
  +----+------+--------+
  |  1 | z3   |      1 |
  |  2 | z4   |      1 |
  |  3 | z5   |      1 |
  |  4 | w5   |      2 |
  |  5 | w6   |      2 |
  |  6 | s7   |      3 |
  |  7 | s8   |      4 |
  +----+------+--------+
  7 rows in set (0.00 sec)
  
  # in 使用
  mysql> select * from tbl_emp e where e.deptId in (select id from tbl_dept);
  +----+------+--------+
  | id | NAME | deptId |
  +----+------+--------+
  |  1 | z3   |      1 |
  |  2 | z4   |      1 |
  |  3 | z5   |      1 |
  |  4 | w5   |      2 |
  |  5 | w6   |      2 |
  |  6 | s7   |      3 |
  |  7 | s8   |      4 |
  +----+------+--------+
  7 rows in set (0.00 sec)
  ```

### order by

#### 排序原则

* 尽量使用 index 方式排序，避免使用 filesort 方式排序

#### 案例分析

##### 建表测试

```sql
# create database MySQLTest;

use MySQLTest;

create table tblA(
#                      id int primary key not null auto_increment,
                     age int,
                     birth timestamp not null
);

insert into tblA(age, birth) values(22, now());
insert into tblA(age, birth) values(23, now());
insert into tblA(age, birth) values(24, now());

create index idx_tblA_ageBirth on tblA(age, birth);

mysql> show index from tblA;
+-------+------------+-------------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| Table | Non_unique | Key_name          | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
+-------+------------+-------------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| tblA  |          1 | idx_tblA_ageBirth |            1 | age         | A         |           3 |     NULL | NULL   | YES  | BTREE      |         |               |
| tblA  |          1 | idx_tblA_ageBirth |            2 | birth       | A         |           3 |     NULL | NULL   |      | BTREE      |         |               |
+-------+------------+-------------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
2 rows in set (0.00 sec)
```

##### 查询案例一

**能使用索引进行排序的情况**：

* 只有带有大哥 age

  ```sql
  mysql> explain select * from tblA where age > 20 order by age;
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+--------------------------+
  | id | select_type | table | partitions | type  | possible_keys     | key               | key_len | ref  | rows | filtered | Extra                    |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+--------------------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | idx_tblA_ageBirth | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using where; Using index |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+--------------------------+
  1 row in set, 1 warning (0.02 sec)
  
  mysql> explain select * from tblA where birth > '2021-01-19 00:00:00' order by age;
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+--------------------------+
  | id | select_type | table | partitions | type  | possible_keys | key               | key_len | ref  | rows | filtered | Extra                    |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+--------------------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | NULL          | idx_tblA_ageBirth | 9       | NULL |    3 |    33.33 | Using where; Using index |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+--------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 有带头大哥 age 和 小弟 birth

  ```sql
  mysql> explain select * from tblA where age > 20 order by age, birth;
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+--------------------------+
  | id | select_type | table | partitions | type  | possible_keys     | key               | key_len | ref  | rows | filtered | Extra                    |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+--------------------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | idx_tblA_ageBirth | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using where; Using index |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+--------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 全升序或者降序的情况

  ```sql
  mysql> explain select * from tblA order by age asc, birth asc;
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-------------+
  | id | select_type | table | partitions | type  | possible_keys | key               | key_len | ref  | rows | filtered | Extra       |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | NULL          | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using index |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-------------+
  1 row in set, 1 warning (0.00 sec)
  
  mysql> explain select * from tblA order by age desc, birth desc;
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-------------+
  | id | select_type | table | partitions | type  | possible_keys | key               | key_len | ref  | rows | filtered | Extra       |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | NULL          | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using index |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-------------+
  1 row in set, 1 warning (0.00 sec)
  ```

##### 查询案例二

**不能使用索引进行排序的情况**：

* 没有带头大哥 age

  ```sql
  mysql> explain select * from tblA where age > 20 order by birth;
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+------------------------------------------+
  | id | select_type | table | partitions | type  | possible_keys     | key               | key_len | ref  | rows | filtered | Extra                                    |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+------------------------------------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | idx_tblA_ageBirth | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using where; Using index; Using filesort |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+------------------------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 小弟 birth 在带头大哥 age 前面

  ```sql
  mysql> explain select * from tblA where age > 20 order by birth, age;
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+------------------------------------------+
  | id | select_type | table | partitions | type  | possible_keys     | key               | key_len | ref  | rows | filtered | Extra                                    |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+------------------------------------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | idx_tblA_ageBirth | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using where; Using index; Using filesort |
  +----+-------------+-------+------------+-------+-------------------+-------------------+---------+------+------+----------+------------------------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

* 一升一降的情况

  ```sql
  mysql> explain select * from tblA order by age asc, birth desc;
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-----------------------------+
  | id | select_type | table | partitions | type  | possible_keys | key               | key_len | ref  | rows | filtered | Extra                       |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-----------------------------+
  |  1 | SIMPLE      | tblA  | NULL       | index | NULL          | idx_tblA_ageBirth | 9       | NULL |    3 |   100.00 | Using index; Using filesort |
  +----+-------------+-------+------------+-------+---------------+-------------------+---------+------+------+----------+-----------------------------+
  1 row in set, 1 warning (0.00 sec)
  ```

##### 优化结论

* MySQL 支持 index 和 filesort 两种排序方式。index 效率更高，它指 MySQL 扫描索引本身完成排序；
* order by 满足**最左前缀原则**的情况下，会使用 index 方式排序；
* 当无法使用 index 方式排序时，增大 **max_length_for_sort_data** 参数的设置 和 增大 **sort_buffer_size** 参数的设置。

### group by

#### 排序原则

* group by 实质是先排序后分组，所以同样遵循**最左前缀原则**；
* 当无法使用索引列时，增大 **max_length_for_sort_data** 参数的设置 和 增大 **sort_buffer_size** 参数的设置；
* where 执行优先级高于 having，能在 where 中限定的条件就不要在 having 中限定；
* 其余原则同 order by 一致。

## 慢查询日志

### 慢查询介绍

* MySQL 的慢查询日志时 MySQL 提供的一种日志记录，它用来记录在 MySQL 中响应时间超过阈值的 SQL 语句，具体指运行时间超过 **long_query_time** 值的 SQL 语句；
* long_query_time 的**默认值**为 10，意思是运行 10 秒以上（不含 10 秒）的 SQL 语句会被记录下来；

### 慢查询日志开启

默认情况下，MySQL 没有开启慢查询日志，需要手动设置这个参数。如果不是调优需要的话，不建议启动该参数，因为开启它会带来一些性能影响，它也支持将日志记录写入文件。

* 查看慢查询日志是否开启

  ```sql
  mysql> show variables like '%slow_query_log%';
  +---------------------+--------------------------------------+
  | Variable_name       | Value                                |
  +---------------------+--------------------------------------+
  | slow_query_log      | OFF                                  |
  | slow_query_log_file | /var/lib/mysql/15d99c8ae37f-slow.log |
  +---------------------+--------------------------------------+
  2 rows in set (0.01 sec)
  ```

* 开启慢查询日志

  ```sql
  # 对当前数据库生效，重启后失效
  mysql> set global slow_query_log = 1;
  Query OK, 0 rows affected (0.03 sec)
  
  mysql> show variables like '%slow_query_log%';
  +---------------------+--------------------------------------+
  | Variable_name       | Value                                |
  +---------------------+--------------------------------------+
  | slow_query_log      | ON                                   |
  | slow_query_log_file | /var/lib/mysql/15d99c8ae37f-slow.log |
  +---------------------+--------------------------------------+
  2 rows in set (0.00 sec)
  
  
  # 永久生效
  # 修改 my.cnf 文件，[mysqld] 下增加或修改参数，
  # 如果没有指定参数 slow_query_log_file 的话，系统默认会给一个缺省的文件 host_name-slow.log
  [mysqld]
  slow_query_log = 1
  slow_query_log_file = /var/lib/mysql/mysql5.7.32.3306-slow.log
  ```

* 查看什么样的 SQL 语句会记录到慢查询日志中

  ```sql
  # 这个是由参数 long_query_time 控制
  # 它可以使用命令修改，也可以在 my.cnf 参数里面修改
  mysql> show variables like '%long_query_time%';
  +-----------------+-----------+
  | Variable_name   | Value     |
  +-----------------+-----------+
  | long_query_time | 10.000000 |
  +-----------------+-----------+
  1 row in set (0.00 sec)
  ```

### 慢查询日志示例

**命令版的慢查询日志**：

* 设置参数

  ```sql
  mysql> show variables like '%long_query_time%';
  +-----------------+-----------+
  | Variable_name   | Value     |
  +-----------------+-----------+
  | long_query_time | 10.000000 |
  +-----------------+-----------+
  1 row in set (0.00 sec)
  
  mysql> set global long_query_time = 3;
  Query OK, 0 rows affected (0.00 sec)
  
  # 可以看到设置后阈值时间没变，但是全局的阈值已经改变。这是因为需要重新连接或者新开一个回话才能看到修改值。
  mysql> show variables like '%long_query_time%';
  +-----------------+-----------+
  | Variable_name   | Value     |
  +-----------------+-----------+
  | long_query_time | 10.000000 |
  +-----------------+-----------+
  1 row in set (0.00 sec)
  
  mysql> show global variables like '%long_query_time%';
  +-----------------+----------+
  | Variable_name   | Value    |
  +-----------------+----------+
  | long_query_time | 3.000000 |
  +-----------------+----------+
  1 row in set (0.00 sec)
  ```

* 慢查询测试

  ```sql
  mysql> select sleep(4);
  +----------+
  | sleep(4) |
  +----------+
  |        0 |
  +----------+
  1 row in set (4.01 sec)
  ```

* 查看慢查询日志

  ```shell
  root@15d99c8ae37f:/# cat /var/lib/mysql/15d99c8ae37f-slow.log
  mysqld, Version: 5.7.32 (MySQL Community Server (GPL)). started with:
  Tcp port: 3306  Unix socket: /var/run/mysqld/mysqld.sock
  Time                 Id Command    Argument
  # Time: 2021-01-19T07:03:49.246822Z
  # User@Host: root[root] @ localhost []  Id:     7
  # Query_time: 4.000464  Lock_time: 0.000000 Rows_sent: 1  Rows_examined: 0
  use MySQLTest;
  SET timestamp=1611039829;
  select sleep(4);
  ```

* 查看当前系统中有多少条慢查询日志记录

  ```sql
  mysql> show global status like '%slow_queries%';
  +---------------+-------+
  | Variable_name | Value |
  +---------------+-------+
  | Slow_queries  | 1     |
  +---------------+-------+
  1 row in set (0.01 sec)
  ```

**配置版的慢查询日志**：

修改 my.cnf 文件，[mysqld] 下增加或修改参数：

```cnf
slow_query_log = 1
slow_query_log_file = /var/lib/mysql/mysql5.7.32.3306-slow.log
long_query_time = 3
log_output=FILE
```

### 日志分析

在生产环境中，如果要手工分析日志，查找、分析 SQL，显然是个体力活。为此，MySQL 提供了日志分析工具 mysqldumpslow。

```shell
root@15d99c8ae37f:/# mysqldumpslow --help
Usage: mysqldumpslow [ OPTS... ] [ LOGS... ]

Parse and summarize the MySQL slow query log. Options are

  --verbose    verbose
  --debug      debug
  --help       write this text to standard output

  -v           verbose
  -d           debug
  -s ORDER     what to sort by (al, at, ar, c, l, r, t), 'at' is default
                al: average lock time
                ar: average rows sent
                at: average query time
                 c: count
                 l: lock time
                 r: rows sent
                 t: query time
  -r           reverse the sort order (largest last instead of first)
  -t NUM       just show the top n queries
  -a           don't abstract all numbers to N and strings to 'S'
  -n NUM       abstract numbers with at least n digits within names
  -g PATTERN   grep: only consider stmts that include this string
  -h HOSTNAME  hostname of db server for *-slow.log filename (can be wildcard),
               default is '*', i.e. match all
  -i NAME      name of server instance (if using mysql.server startup script)
  -l           don't subtract lock time from total time
```

**mysqldumpslow 参数解释**：

* `s` 按何种方式排序
* `c` 访问次数
* `l` 锁定时间
* `r` 返回记录数
* `t` 查询时间
* `al` 平均锁定时间
* `ar` 平均返回记录数
* `at` 平均查询时间
* `g` 匹配正则表达式，大小写不敏感

**常用操作**：

* 得到返回记录集最多的 10 个 SQL

  ```sh
  mysqldumpslow -s r -t 10 /var/lib/mysql/15d99c8ae37f-slow.log
  ```

* 得到访问次数最多的 10 个 SQL

  ```sh
  mysqldumpslow -s c -t 10 /var/lib/mysql/15d99c8ae37f-slow.log
  ```

* 得到按照时间排序的前 10 条里面含有左连接的查询语句

  ```shell
  mysqldumpslow -s t -t 10 -g "left join" /var/lib/mysql/15d99c8ae37f-slow.log
  ```

* 这些命令也可以配合 more 使用

  ```shell
  mysqldumpslow -s t -t 10 -g "left join" /var/lib/mysql/15d99c8ae37f-slow.log | more
  ```

## 批量数据脚本

### 建表测试

```sql
# create database MySQLTest;

use MySQLTest;

CREATE TABLE dept(
                     deptno int unsigned primary key auto_increment,
                     dname varchar(20) not null default '',
                     loc varchar(8) not null default ''
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE emp(
                    id int unsigned primary key auto_increment,
                    empno mediumint unsigned not null default 0,
                    ename varchar(20) not null default '',
                    job varchar(9) not null default '',
                    mgr mediumint unsigned not null default 0,
                    hiredate date not null,
                    sal decimal(7,2) not null,
                    comm decimal(7,2) not null,
                    deptno mediumint unsigned not null default 0
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```

### 设置参数

如果创建函数，如果报错。这是因为开启了慢查询日志，所以必须为 函数 指定一个参数，可以通过修改 log_bin_trust_function_creators 参数来设置不为函数传递参数：

```sql
mysql> show variables like 'log_bin_trust_function_creators';
+---------------------------------+-------+
| Variable_name                   | Value |
+---------------------------------+-------+
| log_bin_trust_function_creators | OFF   |
+---------------------------------+-------+
1 row in set (0.00 sec)

mysql> set global log_bin_trust_function_creators=1;
Query OK, 0 rows affected (0.00 sec)

# 永久生效
# 修改 my.cnf 文件，[mysqld] 下增加或修改参数，
[mysqld]
log_bin_trust_function_creators = 1
```

### 创建函数

创建函数，保证每条数据都不相同。

* 随机产生字符串的函数

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  delimiter $$
  create function rand_string(n int) returns varchar(255)
  begin
      declare chars_str varchar(100) default 'abcdefghijklmnopqrstuvwxyz';
      declare return_str varchar(255) default '';
      declare i int default 0;
      while i < n do
              set return_str = concat(return_str,substring(chars_str,floor(1+rand()*52),1));
              set i=i+1;
          end while;
      return return_str;
  end $$
  ```

* 随机产生部门编号的函数

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  delimiter $$
  create function rand_num() returns int(5)
  begin
      declare i int default 0;
      set i = floor(100+rand()*10);
      return i;
  end $$
  ```

### 创建存储过程

* 创建往 emp 表中插入数据的存储过程

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  delimiter $$
  create procedure insert_emp(in start int(10), in max_num int(10))
  begin
      declare i int default 0;
      set autocommit = 0;
      repeat
          set i = i + 1;
          insert into emp(empno, ename, job, mgr, hiredate, sal, comm, deptno)
          values ((start+i),rand_string(6),'salesman',0001,curdate(),2000,400,rand_num());
      until i = max_num
          end repeat;
      commit;
  end $$
  ```

* 创建往 dept 表中插入数据的存储过程

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  delimiter $$
  create procedure insert_dept(in start int(10), in max_num int(10))
  begin
      declare i int default 0;
      set autocommit = 0;
      repeat
          set i = i + 1;
          insert into dept(deptno, dname, loc)
          values ((start+i),rand_string(10),rand_string(8));
      until i = max_num
          end repeat;
      commit;
  end $$
  ```

### 调用存储过程

* 往 dept 表插入 10 条记录

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  delimiter ;
  call insert_dept(100, 10);
  ```

* 往 emp 表插入 50W 条记录

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  delimiter ;
  call insert_emp(100001, 500000);
  ```

## Show Profile

### Show Profile 介绍

* 它可以分析当前会话中语句执行的资源消耗情况，常用于测量 SQL 的调优情况；
* 默认情况下，参数处于关闭状态，并保存最近 15 次的运行结果。

### Show Profile 开启

```sql
mysql> show variables like '%profiling%';
+------------------------+-------+
| Variable_name          | Value |
+------------------------+-------+
| have_profiling         | YES   |
| profiling              | OFF   |
| profiling_history_size | 15    |
+------------------------+-------+
3 rows in set (0.00 sec)

# 说明是支持 Show Profile 的，使用 `set profiling=on` 开启 Show Profile
mysql> set profiling = on;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> show variables like '%profiling%';
+------------------------+-------+
| Variable_name          | Value |
+------------------------+-------+
| have_profiling         | YES   |
| profiling              | ON    |
| profiling_history_size | 15    |
+------------------------+-------+
3 rows in set (0.04 sec)
```

### Show Profile 示例

* 正常的 SQL

  ```sql
  select * from tbl_emp;
  select * from tbl_emp e inner join tbl_dept d on e.deptId = d.id;
  select * from tbl_emp e left join tbl_dept d on e.deptId = d.id;
  ```

* 慢的 SQL

  ```sql
  select * from emp group by id%10 limit 150000;
  select * from emp group by id%10 limit 150000;
  select * from emp group by id%10 order by 5;
  ```

* 查看结果

  ```sql
  mysql> show profiles;
  +----------+------------+------------------------------------------------------------------+
  | Query_ID | Duration   | Query                                                            |
  +----------+------------+------------------------------------------------------------------+
  |        1 | 0.04100475 | show variables like '%profiling%'                                |
  |        2 | 0.01290600 | select * from emp group by id%10 limit 150000                    |
  |        3 | 0.00014375 | select * from emp group by id%10 limit 150000                    |
  |        4 | 0.00026100 | select * from tbl_emp                                            |
  |        5 | 0.00037550 | select * from tbl_emp e inner join tbl_dept d on e.deptId = d.id |
  |        6 | 0.00126000 | select * from tbl_emp e left join tbl_dept d on e.deptId = d.id  |
  |        7 | 0.00013400 | select * from emp group by id%10 limit 150000                    |
  |        8 | 0.00017125 | select * from emp group by id%10 limit 150000                    |
  |        9 | 0.00013850 | select * from emp group by id%10 order by 5                      |
  +----------+------------+------------------------------------------------------------------+
  9 rows in set, 1 warning (0.00 sec)
  ```

* 诊断 SQL

  ```sql
  mysql> show profile cpu, block io for query 4; # 4 是 Query_ID
  +----------------------+----------+----------+------------+--------------+---------------+
  | Status               | Duration | CPU_user | CPU_system | Block_ops_in | Block_ops_out |
  +----------------------+----------+----------+------------+--------------+---------------+
  | starting             | 0.000092 | 0.000090 |   0.000000 |            0 |             0 |
  | checking permissions | 0.000008 | 0.000007 |   0.000000 |            0 |             0 |
  | Opening tables       | 0.000016 | 0.000016 |   0.000000 |            0 |             0 |
  | init                 | 0.000014 | 0.000013 |   0.000000 |            0 |             0 |
  | System lock          | 0.000006 | 0.000006 |   0.000000 |            0 |             0 |
  | optimizing           | 0.000003 | 0.000004 |   0.000000 |            0 |             0 |
  | statistics           | 0.000010 | 0.000010 |   0.000000 |            0 |             0 |
  | preparing            | 0.000009 | 0.000009 |   0.000000 |            0 |             0 |
  | executing            | 0.000003 | 0.000002 |   0.000000 |            0 |             0 |
  | Sending data         | 0.000067 | 0.000067 |   0.000000 |            0 |             0 |
  | end                  | 0.000004 | 0.000004 |   0.000000 |            0 |             0 |
  | query end            | 0.000005 | 0.000004 |   0.000000 |            0 |             0 |
  | closing tables       | 0.000006 | 0.000006 |   0.000000 |            0 |             0 |
  | freeing items        | 0.000010 | 0.000009 |   0.000000 |            0 |             0 |
  | cleaning up          | 0.000011 | 0.000011 |   0.000000 |            0 |             0 |
  +----------------------+----------+----------+------------+--------------+---------------+
  15 rows in set, 1 warning (0.00 sec)
  ```

  参数说明：

  * `all` 显示所有的开销信息
  * `block io` 显示块 io 相关开销
  * `context switches` 上下文切换相关开销
  * `cpu` 显示 cpu 相关开销
  * `ipc` 显示发送和接收相关开销
  * `memory` 显示内存相关开销
  * `page faults` 显示页面错误相关开销
  * `source` 显示和 source_function、source_file、source_line 相关开销
  * `swaps` 显示交换次数相关开销

* 重要结论

  * **converting HEAP to MyISAM**：查询结果太大，内存都不够用了往磁盘上搬了。
  * **Creating tmp table**：创建临时表，mysql 先将拷贝数据到临时表，然后用完再将临时表删除。
  * **Copying to tmp table on disk**：把内存中临时表复制到磁盘，**危险**。
  * **locked**：锁表。

## 全局查询日志

**注意**：这个功能永远不要在生产环境中开启。

* 命令开启全局查询日志

  ```sql
  set global general_log=1;
  set global log_output='TABLE';
  ```

* 配置开启全局查询日志

  ```cnf
  [mysqld]
  # 开启
  general_log=1
  
  # 记录日志文件的路径
  general_log_file=/path/logfile
  
  # 输出格式
  log_output=FILE
  ```

* 开启后，所执行的 sql 语句，将会记录到 mysql 库里的 general_log 表

  ```sql
  mysql> select * from mysql.general_log;
  Empty set (0.01 sec)
  ```

## 总结和练习

