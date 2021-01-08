# Redis3.0.x 事务

## 基本概念

multi，exec，discard，watch 是 Redis 事务的基础，它们允许一步执行一组命令，有两个重要保证：

* 事务中的所有命令都被序列化并顺序执行。在 Redis 事务的执行过程中，永远不会执行另一个客户端发出的请求。
* 所有命令要么都被执行，要么都不被执行。

Redis 以 乐观锁的形式对这两个保证提供支持，其方式和 **CAS**（Check And Set，检查后设置）操作非常相似。

**悲观锁**：顾名思义，就是每次去拿数据的时候都认为会被修改，所以每次都会加上锁，这样再去拿数据的时候就会被阻塞，直到拿到锁。比如行锁、表锁、读写锁等，都是在操作之前先加上锁。

**乐观锁**：顾名思义，就是每次去拿数据的时候都认为不会被修改，所以都不会上锁，但是在更新的时候会判断在此期间有没有更新这个数据，可以使用版本控制等策略。一般使用于多读少写的应用。

**版本控制策略**：执行更新的前提是提交的版本必须大于当前记录的版本。

## 基本命令

* `MULTI`：标记一个事务块的开始
* `EXEC`：执行事务块内所有的命令
* `DISCARD`：放弃执行事务块内所有的命令
* `WATCH key [key...]`：监视一个或多个 key，如果在事务执行之前 这些 key 被其他命令改动，那么事务将被打断
* `UNWATCHL`：取消 WATCH 对 所有 key 的监视

## 事务使用

### 正常执行

```shell
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set k1 v1
QUEUED
127.0.0.1:6379> get k1
QUEUED
127.0.0.1:6379> set k2 v2
QUEUED
127.0.0.1:6379> exec
1) OK
2) "v1"
3) OK
```

### 放弃事务

```shell
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set k3 v3
QUEUED
127.0.0.1:6379> get k3
QUEUED
127.0.0.1:6379> set k4 v4
QUEUED
127.0.0.1:6379> discard
OK
127.0.0.1:6379> get k3
(nil)
127.0.0.1:6379> get k4
(nil)
```

### “编译”异常

```shell
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name parzulpan
QUEUED
127.0.0.1:6379> get name
QUEUED
127.0.0.1:6379> set eamil
(error) ERR wrong number of arguments for 'set' command
127.0.0.1:6379> exec
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get name
(nil)
```

类似 Java 中的编译时异常，比如语法错误、内存不足等。这样情况下，命令无法排队，Redis 将拒绝执行事务，并且在 EXEC 期间还会返回错误并自动丢弃该事务。

### “运行”异常

```shell
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set k3 v3
QUEUED
127.0.0.1:6379> get k3
QUEUED
127.0.0.1:6379> set a 1
QUEUED
127.0.0.1:6379> get a
QUEUED
127.0.0.1:6379> incr k3
QUEUED
127.0.0.1:6379> exec
1) OK
2) "v3"
3) OK
4) "1"
5) (error) ERR value is not an integer or out of range
```

类似 Java 中的运行时异常，比如类型错误等。这种情况下，命令可以排序，Redis 将执行事务，只不过会对应的返回错误信息。

### 监控使用

初始化信用卡可用余额和欠额：

```shell
127.0.0.1:6379> set balance 1000
OK
127.0.0.1:6379> set debt 0
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby balance 100
QUEUED
127.0.0.1:6379> incrby debt 100
QUEUED
127.0.0.1:6379> exec
1) (integer) 900
2) (integer) 100
127.0.0.1:6379> get balance
"900"
127.0.0.1:6379> get debt
"100"
```

---

无监控键更改。先监控键在开启事务，保证交易金额变动在同一个事务里：

```shell
127.0.0.1:6379> watch balance
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby balance 100
QUEUED
127.0.0.1:6379> incrby debt 100
QUEUED
127.0.0.1:6379> exec
1) (integer) 800
2) (integer) 200
```

所有 监控键 都会具有从调用开始一直到调用EXEC为止 监视更改的效果。即执行完 EXEC，监控消失。

---

有监控键更改。开启多个终端，模拟更改监控键。

```shell
127.0.0.1:6379> get balance
"800"
127.0.0.1:6379> watch balance
OK
127.0.0.1:6379> set balance 900
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby balance 100
QUEUED
127.0.0.1:6379> incrby debt 100
QUEUED
127.0.0.1:6379> exec
(nil)
127.0.0.1:6379> get balance
"900"
127.0.0.1:6379> get debt
"200"
```

监控键受到监控，以检测其更改。如果在 EXEC 命令之前至少更改了一个监视键，则整个事务将中止，并且 EXEC 返回 Null 答复以通知该事务失败。

---

有监控键更改，但更改后解除监控。开启多个终端，模拟更改监控键。

```shell
127.0.0.1:6379> get balance
"800"
127.0.0.1:6379> get debt
"200"
127.0.0.1:6379> watch balance
OK
127.0.0.1:6379> set balance 900
OK
127.0.0.1:6379> unwatch
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set balance 700
QUEUED
127.0.0.1:6379> set debt 300
QUEUED
127.0.0.1:6379> exec
1) OK
2) OK
127.0.0.1:6379> get balance
"700"
127.0.0.1:6379> get debt
"300"
```

如果监控键受到监控，在更改之后，使用 UNAWTCH 解除监控，那么事务将正常执行。

## 为什么 Redis 不支持回滚

Redis 中不保证原子性，不支持回滚，即部分支持事务。主要有以下两个原因：

* 仅当使用错误的语法（并且在命令排队期间无法检测到该问题）或针对包含错误数据类型的键调用 Redis 命令时，该命令才能失败。这实际上意味着失败的命令是编程错误的结果，还有一种很可能在开发过程中而不是生产过程中发现的错误。
* Redis 在内部得到了简化和优化，所以它不需要回滚的能力。

## Redis 脚本和事务

一个 Redis 的脚本是定义事务性的，所以可以用 Redis 的事务做的，也可以用一个脚本做，并且使用脚本会更简单，更快速。

## 练习和总结
