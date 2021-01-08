# Redis3.0.x 发布订阅

## 基本命令

* `SUBSCRIBE channel [channel...]` 订阅给定的一个或多个频道
* `PSUBSCRIBE pattern [pattern...]` 订阅符合给定模式的一个或多个频道
* `UNSUBSCRIBE channel [channel...]` 退订给定的一个或多个频道
* `PUNSUBSCRIBE pattern [pattern...]` 退订符合给定模式的一个或多个频道
* `PUBLISH channel message` 将信息发送到指定的频道
* `PUBSUB subcommand [argument [argument …]]` 查看订阅与发布系统状态

## 发布订阅的使用

发布者：

```shell
127.0.0.1:6379> publish c1 "欢迎订阅"
(integer) 1
127.0.0.1:6379> publish c1 "welcome subscribe"
(integer) 1
```

---

订阅者：

```shell
127.0.0.1:6379> subscribe c1 c2 c3
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "c1"
3) (integer) 1
1) "subscribe"
2) "c2"
3) (integer) 2
1) "subscribe"
2) "c3"
3) (integer) 3

// 收到订阅消息
1) "message"
2) "c1"
3) "\xe6\xac\xa2\xe8\xbf\x8e\xe8\xae\xa2\xe9\x98\x85"
1) "message"
2) "c1"
3) "welcome subscribe"
```

---

查看订阅与发布系统状态：

```shell
// 列出当前的活跃频道
127.0.0.1:6379> pubsub channels
1) "c3"
2) "c1"
3) "c2"

// 返回给定频道的订阅者数量，订阅模式的客户端不计算在内。
127.0.0.1:6379> pubsub numsub c1
1) "c1"
2) (integer) 1

// 返回订阅模式的数量。返回的不是订阅模式的客户端的数量，而是客户端订阅的所有模式的数量总和。
127.0.0.1:6379> pubsub numpat
(integer) 0
```

## 练习和总结
