# Redis3.0.x NoSql 入门

## 概述

NoSQL（Not Only SQL ），即不仅仅是 SQL，泛指非关系型的数据库。NoSQL 数据库的产生就是为了解决大规模数据集合多重数据种类带来的挑战，尤其是大数据应用难题，包括超大规模数据的存储。

**NoSQL 和 RDBMS 的区别**：

* Redis 是 NoSQL 数据库，而 RDBMS 是 SQL 数据库。
* Redis 遵循键值结构，而 RDBMS 遵循表结构。
* Redis 非常快，而 RDBMS 相对较慢。
* Redis 将所有数据集存储在主存储器中，而 RDBMS 将其数据集存储在辅助存储器中。
* Redis 通常用于存储小型和常用文件，而 RDBMS 用于存储大文件。

## 3V3H

**大数据 3V**：

* 海量 Volume
* 多样 Variety
* 实时 Velocity

**互联网 3H**：

* 高并发
* 高可用
* 高性能

## NoSql 四大分类

**键值型数据库**：

* 应用举例：Redis
* 应用场景：内容缓存，主要用于处理大量数据的高访问负载，也用于日志系统等
* 数据模型：key-value，通常使用 HashTable 来实现
* 优点：查找速度快
* 缺点：数据无结构化，通常只被当作字符串或者二进制数据

**文档型数据库**：

* 应用举例：MongoDB
* 应用场景：Web 应用
* 数据模型：key-value，但 value 是结构化数据
* 优点：数据结构要求不严格，表结构可变
* 缺点：查询性能不高，而且缺乏统一的查询语法

**列存储数据库**：

* 应用举例：HBase
* 应用场景：分布式的文件系统，大数据应用
* 数据模型：以列簇式存储，将同一列数据存在一起
* 优点：查找速度快，易于扩展
* 缺点：功能相对局限

**图关系数据库**：

* 应用举例：Neo4j、HugeGraph
* 应用场景：社交网络，推荐系统，构建关系图谱
* 数据模型：图结构
* 优点：查找速度快
* 缺点：不易拓展

## CAP 原理

传统的关系型数据库遵循 **ACID** 原理，即：

* 事务：是逻辑上的一组操作，事务内的语句，要么全部执行成功，要么全部执行失败。
* **原子性**（Atomicity）：指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。
* **一致性**（Consistency）：指数据库总是从一个一致性状态切换到另一个一致性状态。
* **隔离性**（Isolation）：指并发执行的一个事务之间不能互相干扰；
* **持久性**（Durability）：指事务一旦提交，它对数据库的改变是永久性的。

而非关系型数据库遵循 **CAP** 原理，即：

* **强一致性**（Consistency）：
* **高可用性**（Availability）：
* **分区容错性**（Pratition tolerance）：

**CAP 原理的抉择**：

一个分布式系统不可能同时很好的满足 一致性、可用性和分区容错性这三个需求，最多只能同时较好的满足两个。而由于当前的网络硬件肯定会出现延迟丢包等问题，所以分区容忍性是我们必须需要实现的。所以我们只能在一致性和可用性之间进行权衡，没有 NoSQL 系统能同时保证这三点。

**经典的 CAP 搭配**：

* CA - 单点集群，满足 一致性，可用性 的系统，通常在可扩展性上不太强大。例如 Oracle 数据库。
* CP - 满足 一致性，分区容忍性 的系统，通常性能不是特别高。例如 Redis、Mongodb。
* AP - 满足 可用性，分区容忍性 的系统，通常可能对一致性要求低一些。例如 大多数网站架构。

![CAP Theorem](https://images.cnblogs.com/cnblogs_com/parzulpan/1909162/o_210107033541CAPTheorem.png)

**Base 简介**：

* **基本可用**（Basically available）
* **软状态**（Soft state）
* **最终一致**（Eventually consistent）
* BASE 就是为了解决关系数据库 强一致性 引起的问题，进而引起的可用性降低而提出的解决方案。
* 它的思想是通过让系统**放松**对某一时刻数据一致性的要求来换取系统整体伸缩性和性能上改观。

## 分布式和集群

**分布式**：不同的多台服务器上面部署**不同**的服务模块（工程），它们之间通过 **RPC/RMI** 之间通信和调用，对外提供服务和组内协作。

**集群**：不同的多台服务器上面部署**相同**的服务模块（工程），通过**分布式调度软件**进行统一的调度，对外提供服务和访问。

## 练习和总结
