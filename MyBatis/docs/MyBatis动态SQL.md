# MyBatis 动态SQL

## if

可以根据实体类的不同取值，使用不同的 SQL 语句来进行查询。

使用动态 SQL 最常见情景是根据条件包含 where 子句的一部分。

持久层 DAO 接口：

```java
public interface UserDAO {
    /**
     * 根据用户信息，查询用户列表
     * @param user
     * @return
     */
    List<User> findByUser(User user);
}
```

DAO 映射配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findByUser" resultType="User" parameterType="User">
        select * from user where 1 = 1
        <if test="username != null and username != ''">
            and username like #{username}
        </if>
        <if test="address != null">
            and address like #{address}
        </if>
    </select>

</mapper>
```

测试：

```java
@Test
public void findByUserTest() {
    User user = new User();
    user.setUsername("%Tim%");
//        user.setAddress("%北京%");
    List<User> users = userDAO.findByUser(user);
    for (User u : users) {
        System.out.println(u);
    }
}
```

## choose、when、otherwise

有时候，如果不想使用所有的条件，而只是想从多个条件中选择一个使用。针对这种情况，MyBatis 提供了 `choose` 元素，它有点像 Java 中的 `switch` 语句。

持久层 DAO 接口：

```java
public interface UserDAO {
    /***
     * 根据用户信息，查询用户列表，提供默认情况
     * @param user
     * @return
     */
    List<User> findByUserDefault(User user);
}
```

DAO 映射配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findByUserDefault" resultType="User" parameterType="User">
        select * from user where 1 = 1
        <choose>
            <when test="username !=null and username != ''">
                and username like #{username}
            </when>
            <when test="address != null">
                and address like #{address}
            </when>
            <otherwise>
                and id > 50
            </otherwise>
        </choose>
    </select>

</mapper>
```

测试：

```java
@Test
public void findByUserDefaultTest() {
    User user = new User();
//        user.setUsername("%Tim%");
    List<User> users = userDAO.findByUserDefault(user);
    for (User u : users) {
        System.out.println(u);
    }
}
```

## trim、where、set

为了去掉上面的 `where 1 = 1` 恒成立语句，可以使用 `where 元素`，where 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，where 元素也会将它们去除。

持久层 DAO 接口：

```java
public interface UserDAO {
    /**
     * 根据用户信息，查询用户列表，使用 Where
     * @param user
     * @return
     */
    List<User> findByUserWhere(User user);
}
```

DAO 映射配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    select * from user
    <where>
        <if test="username != null and username != ''">
            and username like #{username}
        </if>
        <if test="address != null">
            and address like #{address}
        </if>
    </where>
</mapper>
```

测试：

```java
@Test
    public void findByUserWhereTest() {
        User user = new User();
//        user.setUsername("%Tim%");
        List<User> users = userDAO.findByUserWhere(user);
        for (User u : users) {
            System.out.println(u);
        }
    }
```

也可以通过自定义 `trim` 元素来定制 where 元素的功能。比如，和 where 元素**等价**的自定义 trim 元素为：

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

`prefixOverrides` 属性会忽略通过管道符分隔的文本序列，上述例子会移除所有 prefixOverrides 属性中指定的内容，并且插入 `prefix` 属性中指定的内容。

`set` 元素可以用于动态包含需要更新的列，忽略其它不更新的列，通常用于动态更新语句。

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    update user
    <set>
        <if test="username != null and username != ''">
            username = #{username},
        </if>
        <if test="address != null">
            address = #{address},
        </if>
    </set>
    where id = #{id}
</mapper>
```

和 set 元素**等价**的自定义 trim 元素为：

```xml
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```

## foreach

动态 SQL 的另一个常见使用场景是对集合进行遍历,尤其是在构建 IN 条件语句的时候。

持久层 DAO 接口：

```java
public interface UserDAO {
     /**
     * 根据 id 集合查询用户
     * @param v
     * @return
     */
    List<User> findByIds(QueryV v);
}
```

DAO 映射配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findByIds" resultType="User" parameterType="QueryV">
        select * from user
        <where>
            <if test="ids != null and ids.size() > 0">
                <foreach collection="ids" open="id in (" close=")" item="uid" separator=",">
                    #{uid}
                </foreach>
            </if>
        </where>
    </select>
</mapper>
```

测试：

```java
@Test
public void findByIdsTest() {
    List<Integer> ids = new ArrayList<>();
    ids.add(41);
    ids.add(42);
    ids.add(43);
    ids.add(50);
    ids.add(51);
    ids.add(60);
    QueryV queryV = new QueryV();
    queryV.setIds(ids);
    List<User> users = userDAO.findByIds(queryV);
    for (User u : users) {
        System.out.println(u);
    }
}

```

## 简化编写的 SQL 片段

Sql 中可将重复的 sql 提取出来，使用时用 include 引用即可，最终达到 sql 重用的目的。

定义代码片段：

```xml
<!-- 抽取重复的语句代码片段 -->
<sql id="defaultSql">
    select * from user
</sql>
```

引用代码片段：

```xml
<!-- 配置查询所有操作 -->
<select id="findAll" resultType="user">
    <include refid="defaultSql"></include>
</select>

<!-- 根据 id 查询 -->
<select id="findById" resultType="User" parameterType="int">
    <include refid="defaultSql"></include>
    where id = #{uid}
</select>
```

## 练习和总结
