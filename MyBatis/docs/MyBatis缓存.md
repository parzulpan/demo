# MyBatis 缓存

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/MyBatisCRUD)

## 什么是缓存

像大多数的持久化框架一样，MyBatis 也提供了缓存策略，通过缓存策略来减少数据库的查询次数，从而提高性能。

Mybatis 中缓存分为一级缓存，二级缓存。

## 缓存的适用范围

适用范围：

* 经常查询并且不经常改变的
* 数据的正确与否对最终结果影响不大

## 一级缓存

它指的是 MyBatis 中 SqlSession 对象的缓存，当执行查询之后，查询的结果会同时存入到 SqlSession 会提供一块区域。该区域的结构是一个 Map，当再次查询同样的数据，MyBatis 会先去 SqlSession 中查询是否存在，如果有则直接拿出来使用。

当 SqlSession 对象消失时，MyBatis 的一级缓存也就消失了。

一级缓存的**使用**：

* 编写用户持久层 DAO 接口

    ```java
        /**
        * 根据 ID 查询操作，使用一级缓存
        * @param id
        * @return
        */
        User findByIdCache(Integer id);
    ```

* 编写用户持久层映射文件

    ```xml
        <select id="findByIdCache" resultType="USER" parameterType="java.lang.Integer">
            select * from user where id = #{id};
        </select>
    ```

* 测试

    ```java
        @Test
        public void findByIdCacheTest() {
            User user1 = userDAO.findByIdCache(41);
            System.out.println(user1.hashCode());  // 1439337960
            User user2 = userDAO.findByIdCache(41);
            System.out.println(user2.hashCode());  // 1439337960

            System.out.println(user1 == user2); // true
        }
    ```

一级缓存的**清空**：

```java
    @Test
    public void findByIdCacheClearTest() {
        User user1 = userDAO.findByIdCache(41);
        System.out.println(user1.hashCode());  // 1439337960

        // 使缓存消失方法一：关闭 SqlSession 对象
        // session.close();

        // 使缓存消失方法二
        session.clearCache();

        // session = factory.openSession();
        userDAO = session.getMapper(UserDAO.class);

        User user2 = userDAO.findByIdCache(41);
        System.out.println(user2.hashCode());  // 315860201

        System.out.println(user1 == user2); // false
    }
```

一级缓存的**分析**：

* 一级缓存是 SqlSession 范围的缓存，当调用 SqlSession 的修改，添加，删除，`commit()`，`close()` 等方法时，就会清空一级缓存。
* 第一次发起查询用户 id 为 41 的用户信息，先去找缓存中是否有 id 为 41 的用户信息，如果没有，从数据库查询用户信息。
* 得到用户信息，将用户信息存储到一级缓存中。
* 如果 SqlSession 去执行 commit 操作（执行插入、更新、删除），清空 SqlSession 中的一级缓存，这样做的目的为了让缓存中存储的是最新的信息，**避免脏读**。
* 第二次发起查询用户 id 为 41 的用户信息，先去找缓存中是否有 id 为 41 的用户信息，缓存中有，直接从缓存中获取用户信息。

## 二级缓存

它指的是 MyBatis 中 SqlSessionFactory 对象的缓存，由同一个 SqlSessionFactory 对象创建的 SqlSession 共享其缓存。

**二级缓存结构图**：

![二级缓存结构图](https://images.cnblogs.com/cnblogs_com/parzulpan/1900685/o_201219101310%E4%BA%8C%E7%BA%A7%E7%BC%93%E5%AD%98%E7%BB%93%E6%9E%84%E5%9B%BE.png)

二级缓存的**使用**：

* 在 SqlMapConfig.xml 文件开启二级缓存

    ```xml
    <settings>
            <!-- 开启二级缓存的支持 -->
            <setting name="cacheEnabled" value="true"/>
        </settings>
    ```

* 配置相关的 Mapper 映射文件

    ```xml
        <!-- 开启二级缓存的支持 -->
        <cache></cache>
    ```

* 配置 statement 上面的 useCache 属性

    ```xml
        <select id="findByIdHighCache" resultType="USER" parameterType="java.lang.Integer" useCache="true">
            select * from user where id = #{id};
        </select>
    ```

* 测试

    ```java
        @Test
        public void findByIdHighCacheTest() {
            SqlSession sqlSession1 = factory.openSession();
            UserDAO dao1 = sqlSession1.getMapper(UserDAO.class);
            User user1 = dao1.findByIdHighCache(41);
            System.out.println(user1.hashCode());   // 765284253
            sqlSession1.close();    // 一级缓存消失

            SqlSession sqlSession2 = factory.openSession();
            UserDAO dao2 = sqlSession2.getMapper(UserDAO.class);
            User user2 = dao2.findByIdHighCache(41);
            System.out.println(user2.hashCode());   // 1043351526
            sqlSession1.close();    // 一级缓存消失

            System.out.println(user1 == user2); // false

        }
    ```

测试中执行了两次查询，并且在执行第一次查询后，关闭了一级缓存，再去执行第二次查询时，可以发现并没有对数据库发出 SQL 语句，所以此时的数据就只能是来自于所说的二级缓存。

## 练习和总结
