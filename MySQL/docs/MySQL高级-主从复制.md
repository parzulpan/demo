# MySQL高级 主从复制

## 复制的基本原理

### binlog

`binlog` 是 MySQL 数据库的二进制日志，用于记录用户对数据库操作的 SQL 语句（除了数据查询语句）信息。可以使用 `mysqlbinlog` 命令查看二进制日志的内容。

`binlog` 的格式有三种：

* **STATMENT 模式**：基于SQL语句的复制(`statement-based replication, SBR`)，每一条会修改数据的SQL语句会记录到 binlog 中。
  * **优点**：不需要记录每一条SQL语句与每行的数据变化，这样 `binlog` 的日志也会比较少，减少了磁盘`IO`，提高性能。
  * **缺点**：在某些情况下会导致主从数据不一致。
* **ROW 模式**：基于行的复制(`row-based replication, RBR`)，不记录每一条SQL语句的上下文信息，仅需记录哪条数据被修改，修改成什么样子。
  * **优点**：不会出现主从数据不一致的情况。
  * **缺点**：会产生大量的日志，尤其是`ALTER TABLE`的时候会让日志暴涨。
* **MIXED模式**：混合模式复制(`mixed-based replication, MBR`)，以上两种模式的混合使用，一般的复制使用STATEMENT 模式 保存 `binlog`，对于 STATEMENT 模式 无法复制的操作使用 ROW 模式 保存 `binlog`，MySQL 会根据执行的 SQL 语句选择日志保存方式。

无论是增量备份还是主从复制，都是需要开启 `binlog` 日志，最好跟数据目录设置到不同的磁盘分区，可以降低 `IO` 等待，提升性能；并且在磁盘故障的时候可以利用 `binlog` 恢复数据。

### 基本原理

slave 会从 master 读取 binlog 来进行数据同步，这个过程时异步并且串行化的，主要过程为：

* master 将 每一条会修改数据的SQL语句会记录到 **binlog（二进制日志）** 中，这个记录过程称之为 **binary log events（二进制日志事件）**；
* slave 将 master 的 binary log events 拷贝到 它的 **relay log（中继日志）**；
* slave 重做中继日志中的事件，将变更同步到自己的数据库中。

大致的图说明：

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1914630/o_210120093519%E4%B8%BB%E4%BB%8E%E5%A4%8D%E5%88%B6%E5%9F%BA%E6%9C%AC%E5%8E%9F%E7%90%86.png" alt="主从复制的基本原理" style="zoom:67%;" />

## 复制的基本原则

* 每个 master 可以有多个 salve；
* 每个 slave 只能有一个 master；
* 每个 slave 只能有一个唯一的服务器 ID。

## 复制的最大问题

会发生多次 磁盘 IO，存在延迟情况。

## 一主一从常见配置

使用的前提条件：MySQL 版本一致，并且主从机都在同一网段下。

**这里使用 docker 模拟操作。**

### 主服务器

#### 修改配置文件

`vim /etc/mysql/my.cnf`

```cnf
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# master server config
pid-file=/var/run/mysqld/mysqld.pid
socket=/var/run/mysqld/mysqld.sock
datadir=/var/lib/mysql
symbolic-links=0
server-id=1
log-bin=binlog
log-bin-index=binlog.index
binlog-ignore-db=mysql
binlog-do-db=MySQLTest
```

配置说明：

* **server-id** 服务器 ID，必选
* **log-bin** 启动二进制日志记录，必选
* **log-error** 启动错误日志记录
* **basedir** 根目录
* **tmpdir** 临时目录
* **datadir** 数据目录
* **read-only** 是否只读
* **binlog-ignore-db** 设置不需要复制的数据库
* **binlog-do-db** 设置需要复制的数据库

#### 启动主服务器

```sql
docker run -p 3306:3306 --name mysql5.7.32.3306 \
-v /docker/mysql5.7.32/log:/var/log/mysql \
-v /docker/mysql5.7.32/data:/var/lib/mysql \
-v /docker/mysql5.7.32/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7.32
```

#### 查看是否启动 bin-log

```sql
mysql> show variables like '%log_bin%';
+---------------------------------+-----------------------------+
| Variable_name                   | Value                       |
+---------------------------------+-----------------------------+
| log_bin                         | ON                          |
| log_bin_basename                | /var/lib/mysql/binlog       |
| log_bin_index                   | /var/lib/mysql/binlog.index |
| log_bin_trust_function_creators | OFF                         |
| log_bin_use_v1_row_events       | OFF                         |
| sql_log_bin                     | ON                          |
+---------------------------------+-----------------------------+
6 rows in set (0.01 sec)
```

#### 查看主服务器状态

```sql
mysql> show master status;
+---------------+----------+--------------+------------------+-------------------+
| File          | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+---------------+----------+--------------+------------------+-------------------+
| binlog.000001 |      154 |              |                  |                   |
+---------------+----------+--------------+------------------+-------------------+
1 row in set (0.01 sec)
```

注意，这里的 File 和 Position 值在后面的从服务器需要用到。

### 从服务器

#### 修改配置文件

`vim /etc/mysql/my.cnf`

```cnf
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# 主服务器配置
port=3307
server-id=2
```

#### 启动从服务器

```sql
docker run -p 3307:3307 --name mysql5.7.32.3307 \
-v /docker/mysql5.7.32.3307/log:/var/log/mysql \
-v /docker/mysql5.7.32.3307/data:/var/lib/mysql \
-v /docker/mysql5.7.32.3307/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7.32
```

#### 配置需要复制的主服务器

```sql
# 格式为
CHANGE MASTER TO 
MASTER_HOST='主机 IP',
MASTER_USER='创建用户名',
MASTER_PASSWORD='创建的密码',
MASTER_LOG_FILE='File 名字',
MASTER_LOG_POS=Position数字;

mysql> change master to
    -> master_host='192.168.56.56',
    -> master_user='root',
    -> master_password='root',
    -> master_log_file='binlog.000001',
    -> master_log_pos=154;
Query OK, 0 rows affected, 2 warnings (0.04 sec)

```

#### 启动从服务器复制功能

```sql
mysql> start slave;
Query OK, 0 rows affected (0.01 sec)

# 查看从服务器复制功能是否启动成功，Slave_SQL_Running 为 Yes 和 Slave_IO_Running 为 Yes 说明成功
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.56.56
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: binlog.000001
          Read_Master_Log_Pos: 154
               Relay_Log_File: e76f724e3878-relay-bin.000002
                Relay_Log_Pos: 317
        Relay_Master_Log_File: binlog.000001
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 154
              Relay_Log_Space: 531
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 1
                  Master_UUID: 8bfa63d2-57c9-11eb-8997-0242ac110002
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
         Replicate_Rewrite_DB:
                 Channel_Name:
           Master_TLS_Version:
1 row in set (0.00 sec)
```

#### 停止从服务器复制功能

```sql
mysql> stop slave;
```

## 总结和练习



