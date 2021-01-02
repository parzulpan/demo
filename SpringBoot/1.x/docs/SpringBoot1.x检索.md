# SpringBoot1.x 检索

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-search)

## 概念

Elasticsearch 是一个分布式的开源搜索和分析引擎，适用于所有类型的数据，包括文本、数字、地理空间、结构化和非结构化数据。Elasticsearch 在 Apache Lucene 的基础上开发而成，由 Elasticsearch N.V.（即现在的 Elastic）于 2010 年首次发布。Elasticsearch 以其简单的 REST 风格 API、分布式特性、速度和可扩展性而闻名，是 Elastic Stack 的核心组件；Elastic Stack 是适用于数据采集、充实、存储、分析和可视化的一组开源工具。人们通常将 Elastic Stack 称为 ELK Stack（代指 Elasticsearch、Logstash 和 Kibana），目前 Elastic Stack 包括一系列丰富的轻量型数据采集代理，这些代理统称为 Beats，可用来向 Elasticsearch 发送数据。

以 **员工文档** 的形式存储为例：一个 **文档** 代表一个员工数据。存储数据到 ElasticSearch 的行为叫做 **索引** ，但在索引一个文档之前，需要确定将文档存储在哪里。

一个 ElasticSearch 集群可以 包含多个 **索引** ，相应的每个索引可以包含多个 **类型** 。 这些不同的类型存储着多个 **文档** ，每个文档又有多个 **属性**。

**Elasticsearch 的用途**：

* 应用程序搜索
* 网站搜索
* 企业搜索
* 日志处理和分析
* 基础设施指标和容器监测
* 应用程序性能监测
* 地理空间数据分析和可视化
* 安全分析
* 业务分析

**Elasticsearch 的工作原理**：原始数据会从多个来源（包括日志、系统指标和网络应用程序）输入到 Elasticsearch 中。数据采集指在 Elasticsearch 中进行索引之前解析、标准化并充实这些原始数据的过程。这些数据在 Elasticsearch 中索引完成之后，用户便可针对他们的数据运行复杂的查询，并使用聚合来检索自身数据的复杂汇总。在 Kibana 中，用户可以基于自己的数据创建强大的可视化，分享仪表板，并对 Elastic Stack 进行管理。

## 整合 Elasticsearch

SpringBoot 默认支持两种技术来和 ES 交互：

* Jest，它默认不生效
  * 使用它需要导入 jest 工具包，`io.searchbox.client.JestClient;`
* SpringData Elasticsearch，默认生效，**推荐使用它**
  * 使用 Client 节点，配置属性 clusterNodes、clusterName
  * 用 ElasticsearchTemplate 操作 ES，或者
  * 编写 ElasticsearchRepository 的子接口 来 操作 ES

### 安装测试

* Docker 安装 Elasticsearch：`docker pull elasticsearch:6.8.13`
* 启动 Elasticsearch：`docker run --name elasticsearch -p 9200:9200 -p 9300:9300 -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -d elasticsearch:6.8.13`，注意分配内存，默认为 2GB
* 输入 `http://localhost:9200/`，若显示内容，则代表安装启动成功

### Jest

* 导入依赖

    ```xml
            <!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
            <dependency>
                <groupId>io.searchbox</groupId>
                <artifactId>jest</artifactId>
                <version>6.3.1</version>
            </dependency>
    ```

* 编写配置文件

    ```properties
    # 这也是默认值
    spring.elasticsearch.jest.uris=http://localhost:9200
    ```

* 编写 Article 实体类：

    ```java
    public class Article {

        @JestId
        private Integer id;
        private String author;
        private String title;
        private String content;

        // constructor setter getter toString
    }
    ```

* 编写测试文件：

    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class IntegrationSearchApplicationTests {

        @Autowired
        JestClient jestClient;  // Jest 操作 ES

        // 创建一个索引 http://localhost:9200/parzulpan/tips/1001
        @Test
        public void testJestCreate() {
            // 给 ES 中 索引（保存）一个文档
            Article article = new Article(1001, "消息通知", "zs", "Hello World");

            // 构建一个索引
            Index index = new Index.Builder(article).index("parzulpan").type("tips").build();

            try {
                // 执行
                DocumentResult result = jestClient.execute(index);
                System.out.println(result.getJsonString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 全文搜索
        @Test
        public void testJestSearch() {
            // 全文搜索 查询表达式
            String json = "{\n" +
                    "    \"query\" : {\n" +
                    "        \"match\" : {\n" +
                    "            \"content\" : \"Hello\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            // 构建一个搜索
            Search search = new Search.Builder(json).addIndex("parzulpan").addType("tips").build();

            try {
                // 执行
                SearchResult result = jestClient.execute(search);
                System.out.println(result.getJsonString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    ```

### SpringData Elasticsearch

* 导入依赖

    ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
            </dependency>
    ```

* **注意**，**Spring1.5.22.RELEASE** 默认使用的 elasticsearch 版本为 **2.4.6**。有可能和安装的 ES 版本不合适，可以参考[官方说明](https://github.com/spring-projects/spring-data-elasticsearch)，解决这个问题有两种方法：
  * 升级 SpringBoot
  * 安装对应的 Elasticsearch，安装：`docker pull elasticsearch:2.4.6`，运行：`docker run --name elasticsearch2 -p 9201:9200 -p 9301:9300 -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d elasticsearch:2.4.6`

    ![SpringDataElasticsearch版本说明](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102101632SpringDataElasticsearch%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E.png)

* 编写配置文件

    ```properties
    # 这也是默认值
    spring.elasticsearch.jest.uris=http://localhost:9200

    ## elasticsearch:6.8.13
    #spring.data.elasticsearch.cluster-name=docker-cluster
    #spring.data.elasticsearch.cluster-nodes=localhost:9300

    # elasticsearch:2.4.6
    spring.data.elasticsearch.cluster-name=elasticsearch
    spring.data.elasticsearch.cluster-nodes=localhost:9301
    ```

* 编写 Book 实体类
* 编写 ElasticsearchRepository 的子接口

    ```java
    /**
     * @Author : parzulpan
     * @Time : 2021-01
     * @Desc : 操作 ES
     */

    public interface BookRepository extends ElasticsearchRepository<Book, Integer> {

        // 更多可参考：https://docs.spring.io/spring-data/elasticsearch/docs/2.1.23.RELEASE/reference/html/#reference
        public List<Book> findByBookNameLike(String bookName);  // 自定义方法，按书名模糊查询
    }
    ```

* 编写测试文件：

    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class IntegrationSearchApplicationTests {

        @Autowired
        BookRepository bookRepository;  // 编写 ElasticsearchRepository 的子接口 来 操作 ES

        // http://localhost:9201/parzulpan/book/1
        @Test
        public void testElasticsearchRepositoryCreate() {
            Book book = new Book(1, "Elasticsearch 实战", "parzulpan");
            Book index = bookRepository.index(book);
            System.out.println(index);
        }

        @Test
        public void testElasticsearchRepositorySearch() {
            List<Book> books = bookRepository.findByBookNameLike("实战");
            System.out.println(books);
        }
    }
    ```

* [更多可参考](https://docs.spring.io/spring-data/elasticsearch/docs/2.1.23.RELEASE/reference/html/#reference)

## 练习和总结
