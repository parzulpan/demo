# Redis3.0.x 数据类型

## Redis3.0 版本说明

Redis3.0（里程碑）

Redis3.0 在 2015年4月1日 正式发布，相比于 Redis2.8 主要特性如下：

* Redis 最大的改动就是添加 Redis 的分布式官方实现 Redis Cluster。
* 全新的 embedded string 对象编码结果，优化小对象内存访问，在特定的工作负载下载速度大幅提升。
* LRU 算法大幅提升。
* migrate 连接缓存，大幅提升键迁移的速度。
* migrate 命令两个新的参数 copy 和 replace。
* 新的 client pause 命令，在指定时间内停止处理客户端请求。
* bitcount 命令性能提升。
* config set 设置 maxmemory 时候可以设置不同的单位（之前只能是字节）。
* Redis 日志稍做调整，日志中会反应当前实例的角色（master或者slave）。
* incr 命令性能提升。

## 五大数据类型

* String（字符串）
  * string 是 redis 最基本的类型。可以理解成与 Memcached 一模一样的类型，一个 key 对应一个 value。
  * string 类型是二进制安全的。意思是 redis 的 string 可以包含任何数据。比如 jpg 图片或者序列化的对象。
  * string 类型是 Redis 最基本的数据类型。一个 redis 中字符串 value 最多可以是 512M。
* Hash（哈希）
  * hash 是一个键值对集合。
  * hash 是一个 string 类型的 field 和 value 的映射表，hash 特别适合用于存储对象。
  * 类似 Java 里面的 `Map<String, Object>`。
* List（列表）
  * list 是简单的字符串列表。按照插入顺序排序。你可以添加一个元素导列表的头部（左边）或者尾部（右边）。
  * 它是通过 链表 实现的。
* Set（集合）
  * set 是 string 类型的无序集合。
  * 它是通过 HashTable 实现的。
* ZSet（有序集合）
  * zset 和 set 一样也是 string 类型元素的集合，且不允许重复的成员。
  * 不同的是每个元素都会关联一个 double 类型的分数。
  * redis 正是通过分数来为集合中的成员进行从小到大的排序。zset 的成员是唯一的，但分数（score）却可以重复。

## key

常用 key 操作：

* `del key` 用于在 key 存在时删除 key。
* `dump key` 序列化给定 key，并返回被序列化的值。
* `exists key` 检查给定 key 是否存在。
* `expire key seconds` 为给定 key 设置过期时间，以秒计。
* `expireat key timestamp` 作用和 EXPIRE 类似，都用于为 key 设置过期时间。不同在于 EXPIREAT 命令接受的时间参数是 **UNIX 时间戳**。
* `pexpire key milliseconds` 设置 key 的过期时间，以毫秒计。
* `pexpireat key milliseconds-timestamp` 设置 key 过期时间的**时间戳**，以毫秒计。
* `keys pattern` 查找所有符合给定模式（pattern）的 key 。
* `move key db` 将当前数据库的 key 移动到给定的数据库 db 当中。
* `persist key` 移除 key 的过期时间，key 将持久保持。
* `pttl key` 以毫秒为单位，返回 key 的剩余的过期时间。
* `ttl key` 以秒为单位，返回 key 的剩余的过期时间，-1 表示永不过期，-2 表示已过期。
* `randomkey` 从当前数据库中随机返回一个 key 。
* `rename key newkey` 修改 key 的名称。
* `renamenx key newkey` 仅当 newkey 不存在时，将 key 改名为 newkey。
* `scan cursor [MATCH pattern] [COUNT count]` 迭代数据库中的数据库键。
* `type key` 返回 key 所储存的值的类型。

## String

常用命令：

* `set key value` 设置指定 key 的值。
* `get key` 获取指定 key 的值。
* `getrange key start end` 返回 key 中字符串值的子字符
* `getset key value` 将给定 key 的值设为 value ，并返回 key 的旧值（old value）。
* `getbit key offset` 对 key 所储存的字符串值，获取指定偏移量上的位（bit）。
* `mget key1 [key2…]` 获取所有（一个或多个）给定 key 的值。
* `setbit key offset value` 对 key 所储存的字符串值，设置或清除指定偏移量上的位（bit）。
* `setex key seconds value` 将值 value 关联到 key ，并将 key 的过期时间设为 seconds，以秒为单位。
* `setnx key value` 只有在 key 不存在时设置 key 的值。
* `setrange key offset value` 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始。
* `strlen key` 返回 key 所储存的字符串值的长度。
* `mset key value [key value …]` 同时设置一个或多个 key-value 对。
* `msetnx key value [key value …]` 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
* `psetnx key milliseconds value` 将值 value 关联到 key ，并将 key 的过期时间设为 seconds，以毫秒为单位。
* `incr key` 将 key 中储存的数字值增一。
* `incrby key increment` 将 key 所储存的值加上给定的增量值（increment）。
* `incrbyfloat key increment` 将 key 所储存的值加上给定的浮点增量值（increment） 。
* `decr key` 将 key 中储存的数字值减一。
* `decrby key decrement` 将 key 所储存的值减去给定的减量值（decrement） 。
* `append key value` 如果 key 已经存在并且是一个字符串， 将指定的 value 追加到该 key 原来值（value）的末尾。

## List

常用命令：

* `blpop key1 [key2 ] timeout` 移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
* `brpop key1 [key2 ] timeout` 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
* `brpoplpush source destination timeout` 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它；如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
* `lindex key index` 通过索引获取列表中的元素。
* `linsert key before/after pivot value` 在列表的元素前或者后插入元素。
* `llen key` 获取列表长度。
* `lpop key` 移出并获取列表的第一个元素。
* `lpush key value1 [value2]` 将一个或多个值插入到列表头部。
* `lpushx key value` 将一个值插入到已存在的列表头部。
* `lrange key start stop` 获取列表指定范围内的元素
* `lrem key count value` 移除列表元素
* `lset key index value` 通过索引设置列表元素的值
* `ltrim key start stop` 对一个列表进行修剪（trim），就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
* `rpop key` 移除列表的最后一个元素，返回值为移除的元素。
* `rpoplpush source destination` 移除列表的最后一个元素，并将该元素添加到另一个列表并返回。
* `rpush key value1 [value2]` 在列表中添加一个或多个值。
* `rpushx key value` 为已存在的列表添加值。

## Set

常用命令：

* `sadd key member1 [member2]` 向集合添加一个或多个成员。
* `scard key` 获取集合的成员数。
* `sdiff key1 [key2]` 返回给定所有集合的**差集**。
* `sdiffstore destination key1 [key2]` 返回给定所有集合的差集并存储在 destination 中。
* `sinter key1 [key2]` 返回给定所有集合的**交集**。
* `sinterstore destination key1 [key2]` 返回给定所有集合的交集并存储在 destination 中。
* `sismember key member` 判断 member 元素是否是集合 key 的成员。
* `smembers key` 返回集合中的所有成员。
* `smove source destination member` 将 member 元素从 source 集合移动到 destination 集合。
* `spop key` 移除并返回集合中的一个随机元素。
* `srandmember key [count]` 返回集合中一个或多个随机数。
* `srem key member1 [member2]` 移除集合中一个或多个成员。
* `sunion key1 [key2]` 返回所有给定集合的**并集**。
* `sunionstore destination key1 [key2]` 所有给定集合的并集存储在 destination 集合中。
* `sscan key cursor [MATCH pattern] [COUNT count]` 迭代集合中的元素。

## Hash

常用命令：

* `hdel key field1 [field2]` 删除一个或多个哈希表字段。
* `hexists key field` 查看哈希表 key 中，指定的字段是否存在。
* `hget key field` 获取存储在哈希表中指定字段的值。
* `hgetall key` 获取在哈希表中指定 key 的所有字段和值。
* `hincrby key field increment` 为哈希表 key 中的指定字段的整数值加上增量 increment。
* `hincrbyfloat key field increment` 为哈希表 key 中的指定字段的浮点数值加上增量 increment。
* `gkeys key` 获取所有哈希表中的字段。
* `hlen key` 获取哈希表中字段的数量。
* `hmget key field1 [field2]` 获取所有给定字段的值。
* `hmset key field1 value1 [field2 value2 ]` 同时将多个 field-value（域-值）对设置到哈希表 key 中。
* `hset key field value` 将哈希表 key 中的字段 field 的值设为 value。
* `hsetnx key field value` 只有在字段 field 不存在时，设置哈希表字段的值。
* `hvals key` 获取哈希表中所有值。
* `hscan key cursor [MATCH pattern] [COUNT count]` 迭代哈希表中的键值对。

## ZSet

常用命令：

* `zadd key score1 member1 [score2 member2]` 向有序集合添加一个或多个成员，或者更新已存在成员的分数。
* `zcad key` 获取有序集合的成员数。
* `zcount key min max` 计算在有序集合中指定区间分数的成员数。
* `zincrby key increment member` 有序集合中对指定成员的分数加上增量 increment。
* `zinterstore destination numkeys key [key …]` 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中。
* `zlexcount key min max` 在有序集合中计算指定字典区间内成员数量。
* `zrange key start stop [WITHSCORES]` 通过索引区间返回有序集合指定区间内的成员。
* `zrangebylex key min max [LIMIT offset count]` 通过字典区间返回有序集合的成员。
* `zrangebyscore key min max [WITHSCORES] [LIMIT]` 通过分数返回有序集合指定区间内的成员。
* `zrank key member` 返回有序集合中指定成员的索引。
* `zrem key member [member …]` 移除有序集合中的一个或多个成员。
* `zremrangebylex key min max` 移除有序集合中给定的字典区间的所有成员。
* `zremrangebyrank key start stop` 移除有序集合中给定的排名区间的所有成员。
* `zremrangebysocre key min max` 移除有序集合中给定的分数区间的所有成员。
* `zrevrance key start stop` [WITHSCORES] 返回有序集中指定区间内的成员，通过索引，分数从高到低。
* `zrevrancebyscore key max min [WITHSCORES]` 返回有序集中指定分数区间内的成员，分数从高到低排序。
* `zrevrank key member` 返回有序集合中指定成员的排名，有序集成员按分数值递减（从大到小）排序。
* `zscore key member` 返回有序集中，成员的分数值。
* `zunionstore destination numkeys key [key …]` 计算给定的一个或多个有序集的并集，并存储在新的 key 中。
* `zscan key cursor [MATCH pattern] [COUNT count]` 迭代有序集合中的元素（包括元素成员和元素分值）。

## 练习和总结
