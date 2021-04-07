# MySQL高级 锁机制

## 概述

### 锁的定义

* 锁是计算机协调多个进程或者线程并发访问某一资源的机制；
* 在数据库中，除传统的计算资源（如CPU、RAM、I/O等）的争用以外，数据也是一种供许多用户共享的资源。

**关于死锁**：

* 死锁：指两个或多个事务在同一个资源上相互占用，并请求锁定对方占用的资源，从而导致恶性循环的现象；
* 产生死锁的情况：当多个事务以不同的顺序锁定资源时；多个事务同时锁定一个资源时；
* 解决死锁的方式：死锁检测和死锁超时机制。一种好的方式是检测到死锁的循环依赖，并立即返回一个错误；
* 数据库处理死锁的方式：InnoDB 是将持有最少行级排它锁的事务进行回滚。

### 锁的分类

从锁的**粒度**角度：

* **表锁**：表锁分为读锁和写锁。**特点**是：锁定粒度最大，生锁冲突的概率最高，并发度最低；开销小，加锁快，不会出现死锁；
* **行锁**：行级锁分为共享锁和排他锁。**特点**是：锁定粒度最小，生锁冲突的概率最低，并发度最高；开销大，加锁慢，会出现死锁；**只在存储引擎层实现**；
* **页锁**：粒度和效果介于行锁和表锁之间。

从锁的**类别**角度：

* **读锁**：又称之为共享锁。它针对同一份数据，多个读操作可以同时进行而不会互相影响。它可以同时加上多个；
* **写锁**：又称之为排它锁。在当前写操作没有完成前，它会阻塞其他写锁和读锁。它只可以加上一个。

从**并发控制**角度：

* 悲观锁：假定会发生并发冲突，屏蔽一切可能违反数据完整性的操作。在查询完数据的时候就把事务锁起来，直到提交事务。**实现方式**一般会使用数据库中的锁机制。
* 乐观锁：假设不会发生并发冲突，只在提交操作时检查是否违反数据完整性。在修改数据的时候把事务锁起来，通过版本的方式来进行锁定。**乐观锁不能解决脏读的问题**。**实现方式**一般会使用 **版本号机制** 或 **CAS 算法** 实现。

## 表锁

表锁偏向 **MyISAM** 存储引擎。

### 表锁案例

* 创建表

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  create table table_lock (
                          id int not null primary key auto_increment,
                          name varchar(20) default ''
  ) engine myisam;
  
  insert into table_lock(name) values('a');
  insert into table_lock(name) values('b');
  insert into table_lock(name) values('c');
  insert into table_lock(name) values('d');
  insert into table_lock(name) values('e');
  ```

* 查看表的锁情况，0 表示未上锁

  ```sql
  mysql> show open tables;
  +--------------------+------------------------------------------------------+--------+-------------+
  | Database           | Table                                                | In_use | Name_locked |
  +--------------------+------------------------------------------------------+--------+-------------+
  | MySQLTest          | tbl_emp                                              |      0 |           0
  | MySQLTest          | class                                                |      0 |           0 
  | MySQLTest          | dept                                                 |      0 |           0 
  | MySQLTest          | tbl_dept                                             |      0 |           0 
  | MySQLTest          | phone                                                |      0 |           0 
  | MySQLTest          | article                                              |      0 |           0 
  | MySQLTest          | tblA                                                 |      0 |           0 
  | MySQLTest          | table_lock                                           |      0 |           0 
  +--------------------+------------------------------------------------------+--------+-------------+
  9 rows in set (0.00 sec)
  ```

* 手动添加表锁

  ```sql
  lock table 表名1 read(write), 表名2 read(write), ...;
  ```

* 手动释放表锁

  ```sql
  unlock tables;
  ```

#### 读锁案例

* 在 会话 1 中，给 table_lock 表加上读锁

  ```sql
  mysql> lock table table_lock read;
  Query OK, 0 rows affected (0.00 sec)
  ```

* 在 会话 1 中，**可以**读取 table_lock 表；在 会话 2 中，**可以**读取 table_lock 表

  ```sql
  # 会话 1
  mysql> select * from table_lock;
  +----+------+
  | id | name |
  +----+------+
  |  1 | a    |
  |  2 | b    |
  |  3 | c    |
  |  4 | d    |
  |  5 | e    |
  +----+------+
  5 rows in set (0.00 sec)
  
  # 会话 2
  mysql> select * from table_lock;
  +----+------+
  | id | name |
  +----+------+
  |  1 | a    |
  |  2 | b    |
  |  3 | c    |
  |  4 | d    |
  |  5 | e    |
  +----+------+
  5 rows in set (0.00 sec)
  ```

* 在 会话 1 中，**不可以**读取其他表，例如 book 表；在 会话 2 中，**可以**读取其他表，例如 book 表

  ```sql
  # 会话 1
  mysql> select * from book;
  ERROR 1100 (HY000): Table 'book' was not locked with LOCK TABLES
  
  # 会话 2
  mysql> select * from book;
  +--------+------+
  | bookid | card |
  +--------+------+
  |      1 |    1 |
  |      2 |    1 |
  |      3 |   20 |
  |      4 |   15 |
  |      5 |   16 |
  |      6 |   15 |
  |      7 |    5 |
  |      8 |    1 |
  |      9 |    8 |
  |     10 |   17 |
  |     11 |   20 |
  |     12 |    8 |
  |     13 |   20 |
  |     14 |   15 |
  |     15 |   14 |
  |     16 |    4 |
  |     17 |   16 |
  |     18 |   10 |
  |     19 |   20 |
  |     20 |   11 |
  +--------+------+
  20 rows in set (0.00 sec)
  ```

* 在 会话 1 中，**不可以**修改 table_lock 表；在 会话 2 中，**阻塞**修改 table_lock 表，当表锁释放后，才会执行修改操作

  ```sql
  # 会话 1
  mysql> update table_lock set name = 'parzulpan' where id = 1;
  ERROR 1099 (HY000): Table 'table_lock' was locked with a READ lock and can't be updated
  
  # 会话 2
  mysql> update table_lock set name = 'parzulpan' where id = 1;
  # 一直阻塞着...
  ```

**总结**：

* 当前会话和其他会话均可以读取加了读锁的表；
* 当前会话不可以读取其他表，并且不可以修改加了读锁的表；
* 其他会话可以读取其他表，不过想要修改加了读锁的表，必须等待其读锁释放。

#### 写锁案例

* 在 会话 1 中，给 table_lock 表加上写锁

  ```sql
  mysql> lock table table_lock write;
  Query OK, 0 rows affected (0.00 sec)
  ```

* 在 会话 1 中，**可以**读取 table_lock 表；在 会话 2 中，**阻塞**读取 table_lock 表，当表锁释放后，才会执行读取操作

  ```sql
  # 会话 1
  mysql> select * from table_lock;
  +----+------+
  | id | name |
  +----+------+
  |  1 | a    |
  |  2 | b    |
  |  3 | c    |
  |  4 | d    |
  |  5 | e    |
  +----+------+
  5 rows in set (0.00 sec)
  
  # 会话 2
  mysql> select * from table_lock;
  # 一直阻塞着...
  ```
  
* 在 会话 1 中，**不可以**读取其他表，例如 book 表；在 会话 2 中，**可以**读取其他表，例如 book 表

  ```sql
  # 会话 1
  mysql> select * from book;
  ERROR 1100 (HY000): Table 'book' was not locked with LOCK TABLES
  
  # 会话 2
  mysql> select * from book;
  +--------+------+
  | bookid | card |
  +--------+------+
  |      1 |    1 |
  |      2 |    1 |
  |      3 |   20 |
  |      4 |   15 |
  |      5 |   16 |
  |      6 |   15 |
  |      7 |    5 |
  |      8 |    1 |
  |      9 |    8 |
  |     10 |   17 |
  |     11 |   20 |
  |     12 |    8 |
  |     13 |   20 |
  |     14 |   15 |
  |     15 |   14 |
  |     16 |    4 |
  |     17 |   16 |
  |     18 |   10 |
  |     19 |   20 |
  |     20 |   11 |
  +--------+------+
  20 rows in set (0.00 sec)
  ```

* 在 会话 1 中，**可以**修改 table_lock 表，这正是写锁的目的；在 会话 2 中，**阻塞**修改 table_lock 表，当表锁释放后，才会执行修改操作

  ```sql
  # 会话 1
  mysql> update table_lock set name = 'parzulpan' where id = 1;
  Query OK, 0 rows affected (0.00 sec)
  Rows matched: 1  Changed: 0  Warnings: 0
  
  # 会话 2
  mysql> update table_lock set name = 'parzulpan' where id = 1;
  # 一直阻塞着...
  ```

**总结**：

* 当前会话可以读取加了写锁的表，其他会话读取加了写锁的表必须等待写锁释放；
* 当前会话不可以读取其他表，可以修改加了写锁的表；
* 其他会话可以读取其他表，不过想要修改加了写锁的表，必须等待其写锁释放。

### 案例总结

* MyISAM 在执行查操作前，会自动给涉及的所有表加上**读锁**；在执行增删改操作前，会自动给涉及的所有表加上**写锁**；
* **加读锁，代表共享**。不会阻塞其他进程对同一个表的读请求，但是会阻塞对同一个表的写请求，只有当读锁释放后，才会执行其他进程的写请求；
* **加写锁，代表排它**。会阻塞其他进程对同一个表的读和写请求，只有当写锁释放后，才会执行其他进程的读和写请求。

### 表锁分析

* 通过 `show open tables;` 查看表的锁情况，0 表示未上锁，1 表示上锁

* 通过 `show status like 'table%';` 分析系统的表锁定情况

  ```sql
  mysql> show status like 'table%';
  +----------------------------+-------+
  | Variable_name              | Value |
  +----------------------------+-------+
  | Table_locks_immediate      | 179   |
  | Table_locks_waited         | 0     |
  | Table_open_cache_hits      | 6     |
  | Table_open_cache_misses    | 10    |
  | Table_open_cache_overflows | 0     |
  +----------------------------+-------+
  5 rows in set (0.00 sec)
  ```

  * **Table_locks_immediate** 产生表级锁定的次数，表示可以立即获取锁的查询次数，每立即获取锁值加 1；
  * **Table_locks_waited** 出现表级锁定争用而发生等待的次数（即不能立即获取锁的次数，每等待一次锁值加1），**此值高则说明存在着较严重的表级锁争用情况**；

* 值得注意的是，MyISAM 存储引擎的读写锁调度是 **写锁优先**，所以它不适合做以写为主的表的引擎，因为写锁后，其他线程不能做任何操作，**大量的更新会使查询很难得到锁**，从而造成阻塞。

## 行锁

表锁偏向 **InnoDB** 存储引擎。

MyISAM 和 InnoDB 的最大不同就是：InnoDB 支持事务，并且采用行锁。

### 行锁案例

* 创建表

  ```sql
  # create database MySQLTest;
  
  use MySQLTest;
  
  CREATE TABLE row_lock (
                            a INT(11),
      					b VARCHAR(16)
  )ENGINE=INNODB;
  
  INSERT INTO row_lock VALUES(1,'b2');
  INSERT INTO row_lock VALUES(3,'3');
  INSERT INTO row_lock VALUES(4, '4000');
  INSERT INTO row_lock VALUES(5,'5000');
  INSERT INTO row_lock VALUES(6, '6000');
  INSERT INTO row_lock VALUES(7,'7000');
  INSERT INTO row_lock VALUES(8, '8000');
  INSERT INTO row_lock VALUES(9,'9000');
  INSERT INTO row_lock VALUES(1,'b1');
  
  CREATE INDEX row_lock_a ON row_lock(a);
  CREATE INDEX row_lock_b ON row_lock(b);
  ```

* 手动添加行锁

  ```sql
  # 锁定某一行后，其它的操作会被阻塞，直到锁定行的会话提交
  select xxx ... for update
  ```

#### 操作同一行数据

* 会话 1 开启事务，可以修改 row_lock 表的数据；会话 2 开启事务，阻塞修改 row_lock 表同一行的数据，当会话 1 提交事务，才会执行修改操作

  ```sql
  # 会话 1
  mysql> set autocommit = 0;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> update row_lock set b = '4001' where a = 4;
  Query OK, 1 row affected (0.00 sec)
  Rows matched: 1  Changed: 1  Warnings: 0
  
  # 会话 2
  mysql> set autocommit = 0;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> update row_lock set b = '4002' where a = 4;
  # 一直阻塞着...
  # 并且阻塞时间太长，会报超时错误
  ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction
  ```

#### 操作不同行数据

* 会话 1 开启事务，可以修改 row_lock 表的数据；会话 2 开启事务，可以修改 row_lock 表不同行的数据

  ```sql
  # 会话 1
  mysql> set autocommit = 0;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> update row_lock set b = '4001' where a = 4;
  Query OK, 1 row affected (0.00 sec)
  Rows matched: 1  Changed: 1  Warnings: 0
  
  # 会话 2
  mysql> set autocommit = 0;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> update row_lock set b = '5001' where a = 5;
  Query OK, 1 row affected (0.00 sec)
  Rows matched: 1  Changed: 1  Warnings: 0
  ```

#### 无索引导致行锁升级为表锁

* 会话 1 开启事务，可以修改 row_lock 表的数据，并且在修改时使索引失效 ；会话 2 开启事务，阻塞修改 row_lock 表不同行的数据，当会话 1 提交事务，才会执行修改操作，**这是因为索引失效，导致行锁变为表锁**

  ```sql
  mysql> explain update row_lock set b = '4001' where a = 4;
  +----+-------------+----------+------------+-------+---------------+------------+---------+-------+------+----------+-------------+
  | id | select_type | table    | partitions | type  | possible_keys | key        | key_len | ref   | rows | filtered | Extra       |
  +----+-------------+----------+------------+-------+---------------+------------+---------+-------+------+----------+-------------+
  |  1 | UPDATE      | row_lock | NULL       | range | row_lock_a    | row_lock_a | 5       | const |    1 |   100.00 | Using where |
  +----+-------------+----------+------------+-------+---------------+------------+---------+-------+------+----------+-------------+
  1 row in set (0.00 sec)
  
  # 索引失效
  mysql> explain update row_lock set b = '4001' where a > 4;
  +----+-------------+----------+------------+------+---------------+------+---------+------+------+----------+-------------+
  | id | select_type | table    | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
  +----+-------------+----------+------------+------+---------------+------+---------+------+------+----------+-------------+
  |  1 | UPDATE      | row_lock | NULL       | ALL  | row_lock_a    | NULL | NULL    | NULL |    9 |   100.00 | Using where |
  +----+-------------+----------+------------+------+---------------+------+---------+------+------+----------+-------------+
  1 row in set (0.00 sec)
  
  # 会话 1
  mysql> set autocommit = 0;
  Query OK, 0 rows affected (0.00 sec)
  
  mysql> update row_lock set b = '4001' where a > 4;
  Query OK, 5 rows affected (0.00 sec)
  Rows matched: 5  Changed: 5  Warnings: 0
  
  # 会话 2
  mysql> update row_lock set b = '5001' where a = 5;
  # 一直阻塞着...
  # 并且阻塞时间太长，会报超时错误
  ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction
  ```

### 案例总结

* InnoDB 存储引擎由于实现了行级锁定，虽然在锁定机制的实现方面所带来的性能损耗可能比表级锁定会要更高一些，但是在整体并发处理能力方面要远远优于 MyISAM 的表级锁定的；
* **当系统并发量较高的时候，InnoDB 的整体性能和 MyISAM 相比就会有比较明显的优势了**；
* **但是**，InnoDB 的行级锁定同样也有其脆弱的一面，当我们使用不当的时候（**索引失效，导致行锁变表锁**），可能会让 InnoDB 的整体性能表现不仅不能比 MyISAM 高，甚至可能会更差。

### 行锁分析

* 通过 `show status like 'innodb_row_lock%';` 分析系统的行锁定情况

  ```sql
  mysql> show status like 'innodb_row_lock%';
  +-------------------------------+--------+
  | Variable_name                 | Value  |
  +-------------------------------+--------+
  | Innodb_row_lock_current_waits | 0      |
  | Innodb_row_lock_time          | 148585 |
  | Innodb_row_lock_time_avg      | 18573  |
  | Innodb_row_lock_time_max      | 51079  |
  | Innodb_row_lock_waits         | 8      |
  +-------------------------------+--------+
  5 rows in set (0.00 sec)
  ```

  * **Innodb_row_lock_current_waits** 当前正在等待锁定的数量；
  * **Innodb_row_lock_time** 从系统启动到现在锁定**总时间长度**；
  * **Innodb_row_lock_time_avg** 每次所花的**等待平均时间**；
  * **Innodb_row_lock_time_max** 从系统启动到现在等待最长的一次所花的时间；
  * **Innodb_row_lock_waits** 从系统启动后到现在**总等待的次数**；

尤其是当等待次数很高，而且每次等待时长也不小的时候，我们就需要分析系统中为什么会有如此多的等待，然后根据分析结果着手指定优化计划。

### 行锁优化

* 尽可能让所有数据检索都通过索引来完成，避免无索引行锁升级为表锁；
* 合理设计索引，尽量缩小锁的范围；
* 尽可能较少检索条件，避免**间隙锁**；
* 尽量控制事务大小，减少锁定资源量和时间长度；
* 尽可能使用低级别事务隔离；

## 页锁

* 页锁的粒度和效果介于行锁和表锁之间
* 会出现死锁，并发度一般

## 间隙锁

### 定义

* 当我们用**范围条件**而不是相等条件检索数据，**并请求共享或排他锁时**，InnoDB会给所有符合条件的已有数据记录的索引项加锁；**对于键值在条件范围内但并不存在的记录**，叫做“**间隙（GAP）**”；
* InnoDB 也会对这个“间隙”加锁，这种锁机制是所谓的**间隙锁**（Next-Key Lock）。

### 危害

* 查询操作执行过程中通过范围查找的话，它会锁定整个范围内所有的索引键值，即使这个键值并不存在。
* 当锁定一个范围键值之后，即使某些不存在的键值也会被无故的锁定，而造成在锁定的时候无法插入锁定键值范围内的任何数据。在某些场景下这可能会对性能造成很大的危害。

## 练习和总结

[更多 锁机制 优化技巧](https://backendnote.parzulpan.cn/#/notes/DB/MySQL/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E9%A2%98%EF%BC%9A%E9%94%81?id=%e9%ab%98%e9%a2%91%e9%9d%a2%e8%af%95%e9%a2%98%ef%bc%9a%e9%94%81)