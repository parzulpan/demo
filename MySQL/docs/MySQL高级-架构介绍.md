# MySQL高级 架构介绍

## MySQL 简介

## MySQL 安装

[Docker 安装 参考链接](https://www.cnblogs.com/parzulpan/p/14260911.html#%E5%AE%89%E8%A3%85-docker)

[Linux 安装 参考链接](https://blog.csdn.net/oneby1314/article/details/107901006)

## MySQL 配置文件

* log-bin：二进制日志文件。用于主从复制。它记录了用户对数据库操作的 SQL 语句（除了数据查询语句）信息。可以使用 `mysqlbinlog` 命令查看二进制日志的内容。
* log-error：错误日志。默认是关闭的，记录严重的警告和错误信息、每次启动和关闭的详细信息等
* log：查询日志。默认是关闭的，记录查询的 sql 语句，如果开启会降低 mysql 的整体性能，因为记录日志也是需要消耗系统资源的
* 数据文件：
  * 数据库文件：默认路径为  /var/lib/mysql，所以才有 `/docker/mysql5.7.32/data:/var/lib/mysql` 目录挂载
  * frm 文件：存放表结构
  * myd 文件：存放表数据
  * myi 文件：存放表索引

## MySQL 逻辑架构

MySQL 采用一种分层的思想。

和其他数据库相比，MySQL 有点与众不同，它的架构可以在多种不同场景中应用并发挥良好作用。主要体现在存储引擎的架构上。

**插件式的存储引擎架构**将查询处理和其他的系统任务以及数据的存储提取**相分离**，这种架构额可以根据业务的需求和实际需要选择合适的存储引擎。

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210111092022MySQL%E9%80%BB%E8%BE%91%E6%9E%B6%E6%9E%84.png" alt="MySQL逻辑架构" style="zoom:67%;" />

* **连接层**：这一层主要是完成连接处理、授权认证、及相关的安全方案。在该层上引入线程池的概念，为通过认证安全接入的客户端提供线程。
* **服务层**：这一层主要完成大部分的核心服务功能，包括 SQL 接口、SQL 解析、SQL 优化、缓存查询以及内置函数。所有跨存储引擎的功能都在这一层实现，比如存储过程、触发器、视图等。
  * SQL Interface：SQL 接口。接受用户的 SQL 命令，并且返回用户需要查询的结果。
  * Parser：SQL 解析器。SQL 命令传递到解析器的时候会被解析器验证和解析。
  * Optimizer：SQL 查询优化器。SQL 语句在查询之前会使用查询优化器对查询进行优化，比如有 where 条件时，它会决定先投影还是先过滤。
  * Caches&Buffers：查询缓存。如果查询缓存有命中的查询结果，查询语句就可以直接去查询缓存中取得数据。这个缓存机制是由表缓存、记录缓存、键缓存、权限缓存等组成。
* **引擎层**：这一层主要完成数据的存储和提取，服务层通过 API 和存储引擎进行通信，不同的存储引擎适用于不用场合。值得注意的是，**存储引擎是基于表的**，而不是数据库。
* **存储层**：这一层主要是将数据存储在运行于裸设备的文件系统之上，并完成与存储引擎的交互。

以一个查询为例，大致流程为：

* MySQL 客户端通过协议与 MySQL 服务器建立链接，发送查询语句。先检查**查询缓存**，如果没命中，则进行语句解析等操作；如果命中，则直接返回结果，即不会再对查询进行解析、优化、以及执行等操作。
* 然后 MySQL 通过关键字将 SQL 语句进行解析，并生成一颗对应的**解析树**。**语法解析器**会使用 MySQL 语法规则验证和解析查询。**预处理器**则根据一些 MySQL 规则进一步检查解析数据是否合法。
* 然后由**语法解析器**将解析树转化为执行计划，一条查询语句可以有很多种执行方式，但是最后都会返回相同的结果。**优化器**会选择其中最好的执行计划。
* 最后，MySQL 默认使用 B 树索引，而且至多使用到表中的一个索引。

## MySQL 存储引擎

* 查看 MySQL 支持的存储引擎：

  ```sql
  show engines;
  ```

   查询结果为：

  | Engine              | Support | Comment                                                      | Transactions | XA   | Savepoints |
  | :------------------ | ------- | :----------------------------------------------------------- | :----------- | :--- | :--------- |
  | InnoDB              | DEFAULT | Supports transactions, row-level locking, and foreign keys   | YES          | YES  | YES        |
  | MRG\_MYISAM         | YES     | Collection of identical MyISAM tables                        | NO           | NO   | NO         |
  | MEMORY              | YES     | Hash based, stored in memory, useful for temporary tables    | NO           | NO   | NO         |
  | BLACKHOLE           | YES     | /dev/null storage engine \(anything you write to it disappears\) | NO           | NO   | NO         |
  | MyISAM              | YES     | MyISAM storage engine                                        | NO           | NO   | NO         |
  | CSV                 | YES     | CSV storage engine                                           | NO           | NO   | NO         |
  | ARCHIVE             | YES     | Archive storage engine                                       | NO           | NO   | NO         |
  | PERFORMANCE\_SCHEMA | YES     | Performance Schema                                           | NO           | NO   | NO         |
  | FEDERATED           | NO      | Federated MySQL storage engine                               | NULL         | NULL | NULL       |

* 查询 MySQL 默认使用的存储引擎：

  ```
  show variables like '%storage_engine%';
  ```

   查询结果为：

  | Variable\_name                       | Value  |
  | :----------------------------------- | :----- |
  | default\_storage\_engine             | InnoDB |
  | default\_tmp\_storage\_engine        | InnoDB |
  | disabled\_storage\_engines           |        |
  | internal\_tmp\_disk\_storage\_engine | InnoDB |

* MyISAM 和 InnoDB 对比：

  |          | MyISAM                                                       | InnoDB                                                       |
  | -------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
  | 主外键   | 不支持                                                       | 支持                                                         |
  | 事务     | 不支持                                                       | 支持                                                         |
  | 行表锁   | 支持表锁，不适用高并发场景。                                 | 支持行锁，并且采用 MVCC（[多版本并发控制](https://backendnote.parzulpan.cn/#/notes/DB/MySQL/高性能MySQL（一）?id=多版本并发控制)） 来支持高并发 |
  | 缓存     | 只缓存索引                                                   | 除了缓存索引还缓存真实数据                                   |
  | 表空间   | 小，以紧密格式存储                                           | 大，以数据格式存储                                           |
  | 索引     | 非聚簇索引。它的叶子节点存储的是行数据地址，需要再寻址一次才能得到数据。 | 聚簇索引。它的主键索引的叶子节点存储的是行数据，因此主键索引非常高效。它的非主键索引的叶子节点存储的是主键和其他带索引的列数据，因此查询时做到**覆盖索引**非常高效。 |
  | 应用场景 | 非事务表，大量的 select 操作                                 | 事务表，大量 insert 和 update 操作                           |
  | 关键特性 | 地理空间搜索                                                 | **插入缓冲**：对写索引做了优化，但是索引具体起作用是在读数据的时候。 **二次写**：保证数据页的可靠性。 **自适应哈希索引**：会监控对表上各索引页的查询，可以提升速度。 **异步IO**：提高磁盘操作性能。 **刷新邻接页**：当刷新一个脏页时, InnoDB 会检测该页所在的区, 并将该区下所有的脏页一起刷新。 |

* 存储引擎的选择：
  * 需要事务支持，推荐 InnoDB
  * 需要在线热备份，推荐 InnoDB
  * 需要容灾备份，推荐 InnoDB
  * 需要地理空间搜索，推荐 MyISAM

* 存储引擎的转换：
  * `alter table mytable engine = InnoDB;` 这种方法适用与任何存储引擎，但是需要执行很长时间。因为MySQL会执行将数据从原表复制到一张新的表中，在复制期间可能会消耗系统所有的I/O能力，同时原表会加上读锁。
  * 导出和导入。使用 mysqldump 工具将数据导出到文件。
  * 创建和查询。先创建一个新的存储引擎的表，然后利用insert... select`语法来导数据。如果数据量太大，可以考虑分批处理，针对每一段数据执行事务提交操作。

## 总结和练习

[参考链接](https://blog.csdn.net/oneby1314/article/details/107901006)