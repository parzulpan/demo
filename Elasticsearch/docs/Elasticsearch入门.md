# Elasticsearch 入门

## 简介

全文搜索属于最常见的需求，开源的 Elasticsearch 是目前全文搜索引擎的首选。它可以快速地**存储**、**搜索**和**分析**海量数据。它的底层是开源库 Lucene，但是 Lucene 不能直接使用，必须自己写代码去调用它的接口。而 Elastic 是 Lucene 的封装，提供了 Rest API，可以开箱即用。

## 基本概念

### Index（索引）

动词意思，添加数据，相当于 MySQL 中的 insert；

名词意思，保存数据的地方，相当于 MySQL 中的 Database。

### Type（类型）

在 Index（索引）中，可以定义一个或多个 Type（类型）。相当于 MySQL 中的 Table，它将每一种类型的数据放在一起。

### Document（文档）

保存在某个 Index（索引）下，某种 Type（类型）的一个数据，就叫做 Document（文档），文档是 JSON 格式的，它相当于 MySQL 中的摸一个 Table 里面的内容。

<img src="https://images.cnblogs.com/cnblogs_com/parzulpan/1957875/o_210407051522Elasticsearch%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5.png" alt="Elasticsearch基本概念" style="zoom:67%;" />

### 倒排索引

为什么 ES 搜索快？这是因为使用了倒排索引。

通过**分词**，将整句拆分为单词。

假设保存的记录为：

* 红海行动
* 探索红海行动
* 红海特别行动
* 红海记录片
* 特工红海特别探索

那么会得到倒排索引表为：

| 词     | 记录      |
| ------ | --------- |
| 红海   | 1,2,3,4,5 |
| 行动   | 1,2,3     |
| 探索   | 2,5       |
| 特别   | 3,5       |
| 纪录片 | 4,        |
| 特工   | 5         |

例如检索：红海**特工**行动，查出后计算**相关性得分**，3 号记录命中了 2 次，且 3 号本身才有 3 个单词，2/3，所以 3 号最匹配。

例如检索：红海行动，1 号最匹配。

### 去掉 Type 概念

关系型数据库中两个数据表示是独立的，即使它们里面有相同名称的列也不影响使用，但 ES 中不是这样的。ES 是基于Lucene 开发的搜索引擎， ES 中不同 type 下名称相同的 filed 最终在 Lucene 中的处理方式是一样的。 

* 两个不同 type 下的两个 user_name，在 ES 同一个索引下其实被认为是同一个 filed，必须在两个不同的 type 中定义相同的 filed 映射。否则，不同 type 中的相同字段名称就会在处理中出现冲突的情况，导致 Lucene 处理效率下降。
* 去掉 type 就是为了提高 ES 处理数据的效率。
* Elasticsearch 7.x 中，URL 中的 type 参数为可选。比如，索引一个文档不再要求提供文档类型。
* Elasticsearch 8.x 中，不再支持 URL 中的 type 参数。 
* 解决方法：将索引从多类型迁移到单类型，每种类型文档一个独立索引。

## Docker 安装

* 下载安装 elasticsearch（存储和检索）和 kibana（可视化检索）

  ```shell
  docker pull elasticsearch:7.8.0
  docker pull kibana:7.8.0
  ```

* 配置

  ```shell
  # 将 docker 里的目录挂载到 linux 的 /docker 目录中
  # 修改 /docker 就可以改掉 docker 里的
  mkdir -p /docker/elasticsearch7.8.0/config
  mkdir -p /docker/elasticsearch7.8.0/data
  mkdir -p /docker/elasticsearch7.8.0/plugins
  
  # 让 es 可以被远程任何机器访问
  echo "http.host: 0.0.0.0" >> /docker/elasticsearch7.8.0/config/elasticsearch.yml
  
  # 修改文件权限
  chmod -R 777 /docker/elasticsearch7.8.0/
  ```

* 启动 elasticsearch

  ```shell
  # 查看可用内存
  [root@10 /]# free -m
                total        used        free      shared  buff/cache   available
  Mem:            990         616          72           1         302         232
  Swap:          2047         393        1654
  
  # 9200 是用户交互端口，9300 是集群心跳端口
  # 第一个 -e，指定是单阶段运行
  # 第二个 -e，指定占用的内存大小，生产时可以设置 32G
  # 考虑到虚拟机情况，设置内存不超过 128m
  docker run --name elasticsearch7.8.0 -p 9200:9200 -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e ES_JAVA_OPTS="-Xms64m -Xmx128m" \
  -v /docker/elasticsearch7.8.0/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
  -v /docker/elasticsearch7.8.0/data:/usr/share/elasticsearch/data \
  -v /docker/elasticsearch7.8.0/plugins:/usr/share/elasticsearch/plugins \
  -d elasticsearch:7.8.0
  
  # 设置开机启动
  docker update elasticsearch7.8.0 --restart=always
  ```

* 测试 elasticsearch

  ```shell
  访问 http://192.168.56.56:9200/
  返回 elasticsearch 版本信息
  {
  "name": "0f6d6c60bc96",
  "cluster_name": "elasticsearch",
  "cluster_uuid": "sDTdW7KnQayVrFC5ioijiQ",
  "version": {
  "number": "7.8.0",
  "build_flavor": "default",
  "build_type": "docker",
  "build_hash": "757314695644ea9a1dc2fecd26d1a43856725e65",
  "build_date": "2020-06-14T19:35:50.234439Z",
  "build_snapshot": false,
  "lucene_version": "8.5.1",
  "minimum_wire_compatibility_version": "6.8.0",
  "minimum_index_compatibility_version": "6.0.0-beta1"
  },
  "tagline": "You Know, for Search"
  }
  
  访问 http://192.168.56.56:9200/_cat/nodes
  返回 elasticsearch 节点信息
  127.0.0.1 60 93 6 0.04 0.19 0.18 dilmrt * 0f6d6c60bc96
  ```

* 启动 kibana

  ```shell
  # kibana 指定了 ES 交互端口 9200
  # 5601 为 kibana 主页端口
  docker run --name kibana7.8.0 -e ELASTICSEARCH_HOSTS=http://192.168.56.56:9200 -p 5601:5601 -d kibana:7.8.0
  
  # 设置开机启动
  docker update kibana7.8.0 --restart=always
  ```

* 测试 kibana

  ```shell
  访问 http://192.168.56.56:5601
  返回可视化界面
  ```

## 初步检索

### 检索信息

* `GET /_cat/nodes` 查看所有节点

  ```shell
  # http://192.168.56.56:9200/_cat/nodes
  127.0.0.1 64 93 2 0.00 0.03 0.10 dilmrt * 0f6d6c60bc96
  
  # 0f6d6c60bc96 代表节点，* 代表主节点
  ```

* `GET /_cat/health` 查看 es 健康状况

  ```shell
  # http://192.168.56.56:9200/_cat/health
  1617779285 07:08:05 elasticsearch green 1 1 6 6 0 0 0 0 - 100.0%
  
  # green 表示健康值正常
  ```

* `GET/_cat/master` 查看主节点

  ```shell
  # http://192.168.56.56:9200/_cat/master
  -fBJbk3HQxq4oxHVP5o8XQ 127.0.0.1 127.0.0.1 0f6d6c60bc96
  
  # -fBJbk3HQxq4oxHVP5o8XQ 代表主节点唯一编号
  # 127.0.0.1 代表虚拟机地址
  ```

* `GET/_cat/indices` 查看所有索引，相当于 MySQL 中的 `show databases;`

  ```shell
  # http://192.168.56.56:9200/_cat/indices
  green open .kibana-event-log-7.8.0-000001 NSvWWbd7SaqNmoJ6QmjIRg 1 0  1 0  5.3kb  5.3kb
  green open .apm-custom-link               mn9tqI-0QnOkI5JAp1rCHw 1 0  0 0   208b   208b
  green open .kibana_task_manager_1         k5bSwn03TA-Hpisuzf677A 1 0  5 2 74.2kb 74.2kb
  green open .apm-agent-configuration       ZXRvqEdDSL2555OE8MyNSA 1 0  0 0   208b   208b
  green open .kibana_1                      _yCppL1mQ1a0-v88yOXNTQ 1 0 13 1 72.4kb 72.4kb
  ```

### 新增文档

保存一个数据，保存在哪个索引的哪个类型下，相当于 MySQL 中的哪个数据库的哪张表下。指定用哪一个唯一标识。

`PUT customer/external/1` 在 customer 索引下的 external 类型下保存 1 号数据：

```shell
# postman 新增文档-PUT
# PUT http://192.168.56.56:9200/customer/external/1
# 创建数据成功后，显示 201 created 表示插入记录成功。
# 发送多次是更新操作
{
    "_index": "customer", # 表明该数据在哪个数据库下
    "_type": "external", # 表明该数据在哪个类型下
    "_id": "1", # 表明被保存数据的 id
    "_version": 1, # 被保存数据的版本
    "result": "created", # 创建了一条数据，如果重新 put 一条数据，则该状态会变为 updated，并且版本号也会发生变化
    "_shards": { # 分片
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 0, # 序列号
    "_primary_term": 1
}
```

`POST customer/external` 

```shell
# postman 新增文档-POST
# POST http://192.168.56.56:9200/customer/external
# 发送多次是更新操作
{
    "_index": "customer",
    "_type": "external",
    "_id": "dBNCq3gBsa8QUaibccNi", # 不指定 ID，会自动的生成 id，并且类型是新增的
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 2,
    "_primary_term": 1
}

```

`POST customer/external/3` 

```shell
# postman 新增文档-POST
# POST http://192.168.56.56:9200/customer/external/3
# 发送多次是更新操作
{
    "_index": "customer",
    "_type": "external",
    "_id": "3", # 指定 ID，会使用该 id，并且类型是新增的
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 5,
    "_primary_term": 1
}
```

**总结**：

* **POST** 新增。如果不指定 id，会自动生成 id。
  * 可以不指定 id，不指定 id 时永远为创建
  * 指定不存在的 id 时也为创建
  * 指定存在的 id 时为更新，并且 version 会根据内容变没变而指定版本号是否递增
* **PUT** 新增或修改。PUT 必须指定 id。
  * 一般用来做修改操作，不指定 id 会报错
  * version 总是递增
* `_version` 指版本号，起始值都为 1，每次对当前文档成功操作后都加 1
* `_seq_no` 指序列号，在第一次为索引插入数据时为 0，每对索引内数据操作成功一次加 1， 并且文档会记录是第几次操作使它成为现在的情况的

### 查询文档

`GET /customer/external/1`

```shell
# GET http://192.168.56.56:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 2,
    "_seq_no": 1, # 并发控制字段，每次更新都会 +1，用来做乐观锁
    "_primary_term": 1, # 同上，主分片重新分配，如重启，就会变化
    "found": true,
    "_source": {
        "name": "parzulpan"
    }
}
```

**乐观锁用法**：通过 `if_seq_no=1&if_primary_term=1` 参数，当序列号匹配的时候，才进行修改，否则不修改。

* 将 name 更新为 parzulpan1

  ```shell
  # PUT http://192.168.56.56:9200/customer/external/1?if_seq_no=1&if_primary_term=1
  {
      "_index": "customer",
      "_type": "external",
      "_id": "1",
      "_version": 3,
      "result": "updated",
      "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
      },
      "_seq_no": 8,
      "_primary_term": 1
  }
  
  # 再次查询
  # GET http://192.168.56.56:9200/customer/external/1
  {
      "_index": "customer",
      "_type": "external",
      "_id": "1",
      "_version": 4,
      "_seq_no": 9,
      "_primary_term": 1,
      "found": true,
      "_source": {
          "name": "parzulpan1"
      }
  }
  ```

### 更新文档

`方式一：POST customer/external/1/_update`

```shell
# POST http://192.168.56.56:9200/customer/external/1/_update
{
    "doc": { # 注意要带上 doc
        "name": "parzulpanUpdate"
    }
}

# 返回
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 5,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 10,
    "_primary_term": 1
}

# 如果再次执行更新，则不执行任何操作，版本号和序列号也不发生变化
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 5,
    "result": "noop", # 无操作
    "_shards": {
        "total": 0,
        "successful": 0,
        "failed": 0
    },
    "_seq_no": 10,
    "_primary_term": 1
}
```

`方式二：POST customer/external/1`

```shell
# POST http://192.168.56.56:9200/customer/external/1
{
    "name": "parzulpanUpdate"
}

# 返回
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 6,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 11,
    "_primary_term": 1
}

# 如果再次执行更新，数据会更新成功，并且版本号和序列号会发生变化
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 7,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 12,
    "_primary_term": 1
}
```

`方式三：PUT customer/external/1`

```shell
# PUT http://192.168.56.56:9200/customer/external/1/
{
    "name": "parzulpanUpdate"
}

# 返回
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 8,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 13,
    "_primary_term": 1
}

# 如果再次执行更新，数据会更新成功，并且版本号和序列号会发生变化
```

**总结**：

* POST ，带 _update 时，如果数据相同，不会重新保存并且版本号和序列号不会发生变化
* POST ，不带 _update 时，总是会重新保存并且版本号和序列号会发生变化
* PUT，总是会重新保存并且版本号和序列号会发生变化
* 使用场景：对于大并发更新，推荐不带 _update，而对于大并发查询且偶尔更新，推荐带 _update

### 删除文档或索引

注意，ES 并没有提供删除类型的操作，只提供了删除文档或者索引的操作。

#### 删除文档

```shell
# 删除 id=1 的数据，删除后继续查询
# DELETE http://192.168.56.56:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 10,
    "result": "deleted", # 已被删除
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 15,
    "_primary_term": 1
}

# 再次执行 DELETE http://192.168.56.56:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 11,
    "result": "not_found", # 找不到
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 16,
    "_primary_term": 1
}

# GET http://192.168.56.56:9200/customer/external/1
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "found": false # 找不到
}
```

#### 删除索引

```shell
# 删除整个 customer 索引
# 删除前，GET http://192.168.56.56:9200/_cat/indices
green  open .kibana-event-log-7.8.0-000001 NSvWWbd7SaqNmoJ6QmjIRg 1 0  1 0  5.3kb  5.3kb
green  open .apm-custom-link               mn9tqI-0QnOkI5JAp1rCHw 1 0  0 0   208b   208b
green  open .kibana_task_manager_1         k5bSwn03TA-Hpisuzf677A 1 0  5 2 74.2kb 74.2kb
green  open .apm-agent-configuration       ZXRvqEdDSL2555OE8MyNSA 1 0  0 0   208b   208b
green  open .kibana_1                      _yCppL1mQ1a0-v88yOXNTQ 1 0 15 0 34.9kb 34.9kb
yellow open customer                       t6RCi8QZQoiEx-wxJQvlmw 1 1  5 0  4.6kb  4.6kb

# DELETE http://192.168.56.56:9200/customer
{
    "acknowledged": true
}

# 删除后，GET http://192.168.56.56:9200/_cat/indices
green open .kibana-event-log-7.8.0-000001 NSvWWbd7SaqNmoJ6QmjIRg 1 0  1 0  5.3kb  5.3kb
green open .apm-custom-link               mn9tqI-0QnOkI5JAp1rCHw 1 0  0 0   208b   208b
green open .kibana_task_manager_1         k5bSwn03TA-Hpisuzf677A 1 0  5 2 74.2kb 74.2kb
green open .apm-agent-configuration       ZXRvqEdDSL2555OE8MyNSA 1 0  0 0   208b   208b
green open .kibana_1                      _yCppL1mQ1a0-v88yOXNTQ 1 0 15 0 34.9kb 34.9kb
```

### 批量操作-bulk

这里的批量操作，指的是当发生某一条执行发生失败时，其他的数据仍然能够接着执行，也就是说彼此之间是独立的。

bulk api 以此按顺序执行所有的 action（动作）。如果一个单个的动作因任何原因失败，它将继续处理它后面剩余的动作。当 bulk api 返回时，它将提供每个动作的状态（与发送的顺序相同），所以可以检查一个指定的动作是否失败了。

注意，由于 bulk 不支持 json 或者 text 格式，所以不能在 postman 中测试，可以使用 kibana 的 DevTools。

实例 1：执行多条数据

```shell
# 在 kibana 的 DevTools 的控制台执行以下命令
POST /customer/external/_bulk
{"index":{"_id":"1"}}
{"name":"John Doe"}
{"index":{"_id":"2"}}
{"name":"John Doe"}

# 返回
#! Deprecation: [types removal] Specifying types in bulk requests is deprecated.
{
  "took" : 368, # 命令花费时间
  "errors" : false, # 没有发送任何错误
  "items" : [ # 每个数据的结果
    {
      "index" : { # 第一条数据
        "_index" : "customer",
        "_type" : "external",
        "_id" : "1",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 201 # 新建完成
      }
    },
    {
      "index" : { # 第二条数据
        "_index" : "customer",
        "_type" : "external",
        "_id" : "2",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    }
  ]
}
```

实例 2：对于整个索引执行批量操作

```shell
POST /_bulk
{"delete":{"_index":"website","_type":"blog","_id":"123"}}
{"create":{"_index":"website","_type":"blog","_id":"123"}}
{"title":"my first blog post"}
{"index":{"_index":"website","_type":"blog"}}
{"title":"my second blog post"}
{"update":{"_index":"website","_type":"blog","_id":"123"}}
{"doc":{"title":"my updated blog post"}}

# 返回
#! Deprecation: [types removal] Specifying types in bulk requests is deprecated.
{
  "took" : 450,
  "errors" : false,
  "items" : [
    {
      "delete" : { # 删除
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 1,
        "result" : "not_found",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 404
      }
    },
    {
      "create" : { # 创建
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 2,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : { # 保存
        "_index" : "website",
        "_type" : "blog",
        "_id" : "nxPjrHgBsa8QUaibx-rD",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 2,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "update" : { # 更新
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 3,
        "result" : "updated",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 3,
        "_primary_term" : 1,
        "status" : 200
      }
    }
  ]
}

```

### 样本测试数据

一份顾客银行账户信息的虚构的 JSON 文档样本，[文件地址](https://github.com/elastic/elasticsearch/blob/master/docs/src/test/resources/accounts.json)

格式为：

```shell
{
	"account_number": 1,
	"balance": 39225,
	"firstname": "Amber",
	"lastname": "Duke",
	"age": 32,
	"gender": "M",
	"address": "880 Holmes Lane",
	"employer": "Pyrami",
	"email": "amberduke@pyrami.com",
	"city": "Brogan",
	"state": "IL"
}
```

```shell
POST bank/account/_bulk
{"index":{"_id":"1"}}
{"account_number":1,"balance":39225,"firstname":"Amber","lastname":"Duke","age":32,"gender":"M","address":"880 Holmes Lane","employer":"Pyrami","email":"amberduke@pyrami.com","city":"Brogan","state":"IL"}
...

GET http://192.168.56.56:9200/_cat/indices
green  open .kibana-event-log-7.8.0-000001 NSvWWbd7SaqNmoJ6QmjIRg 1 0    1 0  5.3kb  5.3kb
yellow open website                        3rGabFSISrq8ZwdXxP331g 1 1    2 2  8.8kb  8.8kb
yellow open bank                           ZpN0_upESxqV84IVAgyvJw 1 1 1000 0  397kb  397kb
green  open .apm-custom-link               mn9tqI-0QnOkI5JAp1rCHw 1 0    0 0   208b   208b
green  open .kibana_task_manager_1         k5bSwn03TA-Hpisuzf677A 1 0    5 2 74.2kb 74.2kb
green  open .apm-agent-configuration       ZXRvqEdDSL2555OE8MyNSA 1 0    0 0   208b   208b
green  open .kibana_1                      _yCppL1mQ1a0-v88yOXNTQ 1 0   28 2 63.9kb 63.9kb
yellow open customer                       kYEsiy1iQWa2S_7JSsG9kQ 1 1    2 0  3.6kb  3.6kb

# 可以看到 bank 索引导入了 1000 条数据
```

## 进阶检索

### SearchAPI

ES 支持两种基本方式检索：

* 通过 REST request uri 发送检索参数，即 **uri + 检索参数**

  ```shell
  GET http://192.168.56.56:9200/bank/_search?q=*&sort=account_number:asc
  # q=* 表示查询所有
  # sort 表示排序字段
  # asc 表示升序
  
  # 返回
  {
      "took": 2, # 花费多少 ms 检索
      "timed_out": false, # 是否超时
      "_shards": { # 多少分片被搜索了，以及多少成功/失败的搜索分片
          "total": 1,
          "successful": 1,
          "skipped": 0,
          "failed": 0
      },
      "hits": {
          "total": {
              "value": 1000, # 多少匹配文档被找到
              "relation": "eq"
          },
          "max_score": null, # 文档相关性最高得分
          "hits": [
              {
                  "_index": "bank",
                  "_type": "account",
                  "_id": "0",
                  "_score": null, # 相关得分
                  "_source": {
                      "account_number": 0,
                      "balance": 16623,
                      "firstname": "Bradshaw",
                      "lastname": "Mckenzie",
                      "age": 29,
                      "gender": "F",
                      "address": "244 Columbus Place",
                      "employer": "Euron",
                      "email": "bradshawmckenzie@euron.com",
                      "city": "Hobucken",
                      "state": "CO"
                  },
                  "sort": [ # 结果的排序 key（列），没有的话按照 score 排序
                      0
                  ]
              },
              // ...
              {
                  "_index": "bank",
                  "_type": "account",
                  "_id": "9",
                  "_score": null,
                  "_source": {
                      "account_number": 9,
                      "balance": 24776,
                      "firstname": "Opal",
                      "lastname": "Meadows",
                      "age": 39,
                      "gender": "M",
                      "address": "963 Neptune Avenue",
                      "employer": "Cedward",
                      "email": "opalmeadows@cedward.com",
                      "city": "Olney",
                      "state": "OH"
                  },
                  "sort": [
                      9
                  ]
              }
          ]
      }
  }
  ```

* 通过 REST request body，即 **uri + 请求体**

  ```shell
  GET bank/_search
  {
    "query": { "match_all": {} },
    "sort": [
      { "account_number": "asc" },
      { "balance":"desc"}
    ]
  }
  
  # 返回
  {
      "took": 3,
      "timed_out": false,
      "_shards": {
          "total": 1,
          "successful": 1,
          "skipped": 0,
          "failed": 0
      },
      "hits": {
          "total": {
              "value": 1000,
              "relation": "eq"
          },
          "max_score": null,
          "hits": [
              {
                  "_index": "bank",
                  "_type": "account",
                  "_id": "0",
                  "_score": null,
                  "_source": {
                      "account_number": 0,
                      "balance": 16623,
                      "firstname": "Bradshaw",
                      "lastname": "Mckenzie",
                      "age": 29,
                      "gender": "F",
                      "address": "244 Columbus Place",
                      "employer": "Euron",
                      "email": "bradshawmckenzie@euron.com",
                      "city": "Hobucken",
                      "state": "CO"
                  },
                  "sort": [
                      0,
                      16623
                  ]
              },
              // ...
              {
                  "_index": "bank",
                  "_type": "account",
                  "_id": "9",
                  "_score": null,
                  "_source": {
                      "account_number": 9,
                      "balance": 24776,
                      "firstname": "Opal",
                      "lastname": "Meadows",
                      "age": 39,
                      "gender": "M",
                      "address": "963 Neptune Avenue",
                      "employer": "Cedward",
                      "email": "opalmeadows@cedward.com",
                      "city": "Olney",
                      "state": "OH"
                  },
                  "sort": [
                      9,
                      24776
                  ]
              }
          ]
      }
  }
  ```

### Query DSL 

DSL（Domain specific language，领域特定语言 ）



## 总结和练习

