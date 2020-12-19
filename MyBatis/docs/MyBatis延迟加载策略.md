# MyBatis 延迟加载策略

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/MyBatisCRUD)

## 什么是延迟加载

延迟加载，就是在需要用到数据时才进行加载，不需要用到数据时就不加载数据，也被成为懒加载。

**好处**：先从单表查询，需要时再从关联表去关联查询，大大提高了数据库性能。

**坏处**：因为当需要用到数据时才进行数据库查询，这样在进行大批量数据查询时，以可能造成用户等待时间变长，造成用户体验下降。

与延迟加载相对的，就是立即加载，它指的是不管用不用，只要一调用方法，马上发起查询。

在对应的四种表关系中，一对多和多对多通常情况下采用延迟加载，而多对一和一对一通常情况下采用立即加载。

## 延迟加载的使用

开启延迟加载的两个关键设置：

* `lazyLoadingEnabled` 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 `fetchType` 属性来覆盖该项的开关状态 **有效值**为 true | false
* `aggressiveLazyLoading` 开启时，任一方法的调用都会加载该对象的所有延迟加载属性。 否则，每个延迟加载属性会按需加载（参考lazyLoadTriggerMethods） **有效值**为 true | false
* `lazyLoadTriggerMethods` 指定对象的哪些方法触发一次延迟加载**有效值**为用逗号分隔的方法列表

### 使用 assocation 实现延迟加载

* 账户的持久层 DAO 接口

    ```java
    /**
    * 查询所有账户，懒加载
    * @return
    */
    List<Account> findAllLazy();
    ```

* 账户的持久层映射文件

    ```xml
        <resultMap id="accountLazyMap" type="account">
            <id column="id" property="id"/>
            <result column="uid" property="uid"/>
            <result column="money" property="money"/>
            <association property="user" javaType="user" select="cn.parzulpan.dao.UserDAO.findByIdLazy" column="uid">
            </association>
        </resultMap>
        <!--  select： 填写要调用的 select 映射的 id-->
        <!--  column ： 填写要传递给 select 映射的参数-->
        <select id="findAllLazy" resultMap="accountLazyMap">
            select * from account;
        </select>
    ```

* 用户的持久层 DAO 接口

    ```java
    /**
    * 查询所有信息，使用懒加载
    * @return
    */
    List<User> findByIdLazy();
    ```

* 用户的持久层映射文件

    ```xml
        <select id="findByIdLazy" resultType="user" parameterType="int">
            select * from user where id = #{uid};
        </select>
    ```

* 开启 Mybatis 的延迟加载策略

    ```xml
        <settings>
            <setting name="lazyLoadingEnabled" value="true"/>
            <setting name="aggressiveLazyLoading" value="false"/>
        </settings>
    ```

* 测试

    ```java
        @Test
        public void findAllLazyTest() {
            List<Account> accounts = accountDAO.findAllLazy();

        }
    ```

### 使用 collection 实现延迟加载

...

## 练习和总结
