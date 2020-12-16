# MyBtis CRUD

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/MyBatisCRUD)

## 基于代理 DAO 的 CRUD

### 根据 ID 查询操作

在持久层接口中添加 findById 方法：

```java
public interface UserDAO {

    /**
     * 根据 ID 查询操作
     * @param userId
     * @return
     */
    User findById(Integer userId);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findById" resultType="cn.parzulpan.domain.User" parameterType="java.lang.Integer">
        select * from user where id = #{id};
    </select>
</mapper>
```

**注意**：

* `resultType 属性` 指定结果集的类型
* `parameterType 属性` 指定传入参数的类型
* `#{} 字符` 它代表占位符，相当于原来 JDBC 的 `?`，都是用于执行语句时替换实际的数据。具体的数据是由 `#{}` 里面的内容决定的。

在测试类添加测试：

```java
package cn.parzulpan.test;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class MyBatisCRUDTest {
    private InputStream is;
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory factory;
    private SqlSession session;
    private UserDAO userDAO;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = builder.build(is);
        session = factory.openSession();
        userDAO = session.getMapper(UserDAO.class);
    }

    @After
    public void destroy() throws IOException {
        session.commit();   // 事务提交
        session.close();
        is.close();
    }

    @Test
    public void findByIdTest() {
        User user = userDAO.findById(41);
        System.out.println(user);
    }
}
```

### 用户保存操作

在持久层接口中添加 saveUser 方法：

```java
public interface UserDAO {

    /**
     * 用户保存操作
     * @param user
     * @return 影响数据库记录的条数
     */
    int saveUser(User user);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <insert id="saveUser" parameterType="cn.parzulpan.domain.User">
        # 扩展：新增用户 id 的返回值，在 insert 之后执行
        <selectKey keyColumn="id" keyProperty="id" resultType="int" order="AFTER">
            select last_insert_id();
        </selectKey>
        insert into user(username, birthday, sex, address) values (#{username}, #{birthday}, #{sex}, #{address});
    </insert>
</mapper>
```

**注意**：

* `ognl 表达式` 它是 apache 提供的一种表达式语言，全称是 `Object Graphic Navigation Language`（对象图导航语言）：
  * 它是按照一定的语法格式来获取数据的，语法格式就是使用 `#{对象.对象}` 的方式
  * `#{user.username}` 它会先去找 `user` 对象，然后在 `user` 对象中找到 `username` 属性，并调用 `getUsername()` 把值取出来。但是在 `parameterType` 属性上指定了实体类名称，所以可以省略 `user`。
而直接写 username。

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void saveUserTest() {
        User user = new User(null, "modify username", new Date(), "男", "Beijing");
        System.out.println("save before: " + user); // User{id=null, ...}
        int i = userDAO.saveUser(user);
        System.out.println(i);
        System.out.println("save after: " + user); // User{id=52, ...}
    }
}
```

### 用户更新操作

在持久层接口中添加 updateUser 方法：

```java
public interface UserDAO {

    /**
     * 用户更新操作
     * @param user
     * @return 影响数据库记录的条数
     */
    int updateUser(User user);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <update id="updateUser" parameterType="cn.parzulpan.domain.User">
        update user set username = #{username}, birthday = #{birthday}, sex = #{sex}, address = #{address} where id = #{id};
    </update>
</mapper>
```

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void updateUserTest() {
       User user = userDAO.findById(42);
       user.setUsername("Tom Tim");
       user.setAddress("瑞典");
       int i = userDAO.updateUser(user);
       System.out.println(i);
    }
}
```

### 用户删除操作

在持久层接口中添加 deleteUser 方法：

```java
public interface UserDAO {

    /**
     * 用户删除操作
     * @param userId
     * @return 影响数据库记录的条数
     */
    int deleteUser(Integer userId);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <delete id="deleteUser" parameterType="java.lang.Integer">
        delete from user where id = #{id};
    </delete>
</mapper>
```

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void deleteUserTest() {
        int i = userDAO.deleteUser(49);
        System.out.println(i);
    }
}
```

### 用户模糊查询操作

在持久层接口中添加 findByName 方法：

```java
public interface UserDAO {

    /**
     * 用户模糊查询操作
     * @param username
     * @return
     */
    List<User> findByName(String username);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <!-- 模糊查询操作的第一种配置方式 -->
    <!-- 实际执行语句 Preparing: select * from user where username like ?; -->
    <!-- 这种方式，在传入字符串实参时，就需要给定模糊查询的标识% -->
    <select id="findByName" resultType="cn.parzulpan.domain.User" parameterType="String">
        select * from user where username like #{username};
    </select>
</mapper>
```

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void findByNameTest() {
        List<User> userList = userDAO.findByName("%Tim%");
        userList.forEach(System.out::println);
    }
}
```

模糊查询的另一种配置方式，**推荐这种方式**，在持久层接口中添加 findByNameV2 方法：

```java
public interface UserDAO {

    /**
     * 用户模糊查询操作
     * @param username
     * @return
     */
    List<User> findByNameV2(String username);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <!--模糊查询操作的第二种配置方式-->
    <!-- 实际执行语句 Preparing: select * from user where username like '%Tim%'; -->
    <!-- 这种方式，在传入字符串实参时，就不需要给定模糊查询的标识% -->
    <select id="findByNameV2" resultType="cn.parzulpan.domain.User" parameterType="String">
        select * from user where username like '%${value}%';
    </select>
</mapper>
```

**注意**：

* `#{}` 表示一个占位符号，实现 preparedStatement 向占位符中设置值，自动进行 java 类型和 jdbc 类型转换，`#{}` 可以有效防止 sql 注入。 `#{}` 可以接收简单类型值或 pojo 属性值。 如果 parameterType 传输单个简单类型值，`#{}` 括号中可以是 value 或其它名称。
* `${}` 表示拼接 Sql 串，将 parameterType 传入的内容拼接在 sql 中且不进行 jdbc 类型转换， `${}` 可以接收简单类型值或 pojo 属性值，如果 parameterType 传输单个简单类型值，`${}` 括号中只能是 value。

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void findByNameV2Test() {
        List<User> userList = userDAO.findByNameV2("Tim");
        userList.forEach(System.out::println);
    }
}
```

### 使用聚合函数查询

在持久层接口中添加 findTotal 方法：

```java
public interface UserDAO {

    /**
     * 使用聚合函数查询
     * @return
     */
    int findTotal();
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findTotal" resultType="int">
        select count(*) from user;
    </select>
</mapper>
```

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void findTotalTest() {
        int total = userDAO.findTotal();
        System.out.println(total);
    }
}
```

## MyBtis 参数深入

### parameterType

`parameterType 属性` 指定传入参数的类型，该属性的取值可以是**基本类型**，**引用类型**（例如 String ），还可以是实体类类型（例如 User ），同时也可以使用**实体类的包装**。
类

基本类型 和 String  可以直接写类型名称，也可以使用 `包名.类名` 的方式。实体类类型，目前只能使用全限定类名。

究其原因，是 MyBatis 在加载时已经把常用的数据类型注册了别名，从而在使用时可以不写包名，而我们的是实体类并没有注册别名，所以必须写全限定类名。

### 传递实体类包装对象

编写 QueryV：

```java

```

在持久层接口中添加 findByQueryV 方法：

```java
public interface UserDAO {

    /**
     * 根据 QueryV 中的条件查询用户
     * @param v
     * @return
     */
    List<User> findByQueryV(QueryV v);
}
```

在用户的映射配置文件中配置：

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <!--由于 将 ${} parameterType 传入的内容拼接在 sql 中且不进行 jdbc 类型转换
    所以，模糊查询操作的不能使用第二种配置方式-->
    <select id="findByQueryV" resultType="cn.parzulpan.domain.User" parameterType="cn.parzulpan.domain.QueryV">
        select * from user where username like #{user.username};    # user 是 QueryV 的属性
    </select>
</mapper>
```

在测试类添加测试：

```java
public class MyBatisCRUDTest {
    
    @Test
    public void findByQueryVTest() {
        User user = new User();
        user.setUsername("%Tim%");
        QueryV queryV = new QueryV();
        queryV.setUser(user);
        List<User> userList = userDAO.findByQueryV(queryV);
        userList.forEach(System.out::println);
    }
}
```

## MyBtis 输出结果封装

### resultType 配置结果类型

resultType 属性可以指定结果集的类型，它支持基本类型和实体类类型。

需要注意的是，它和 parameterType 一样，如果注册过类型别名的，可以直接使用别名。没有注册过的必须使用全限定类名。例如我们的实体类此时必须是全限定类名。

同时，当是实体类名称是，还有一个**要求**，**实体类中的属性名称必须和查询语句中的列名保持一致**，否则无法实现封装。

如果不能保持一致，可以修改映射配置，使用**别名查询**。

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findAll" resultType="cn.parzulpan.domain.User">
        select * from user;
    </select>

    <!-- 将上面的修改为 -->
    <select id="findAll" resultType="cn.parzulpan.domain.User">
        select id as userId, username as userName, birthday as userBirthday, sex as userSex, address as userAddress from user;
    </select>
</mapper>
```

这种方式，执行效率更快，但是开发效率稍低。

### resultMap 结果类型

如果查询很多，都使用别名的话写起来岂不是很麻烦？

resultMap 标签可以建立查询的列名和实体类的属性名称不一致时建立对应关系，从而实现封装。

```xml
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <!-- 建立 User 实体和数据库表的对应关系
         type 属性：指定实体类的全限定类名
         id 属性：给定一个唯一标识，是给查询 select 标签引用用的
    -->
    <resultMap type="cn.parzulpan.dao.User" id="userMap">
        <!--
            id 标签：用于指定主键字段
            result 标签：用于指定非主键字段
            column 属性：用于指定数据库列名
            property 属性：用于指定实体类属性名称
        -->
        <id column="id" property="userId"/>
        <result column="username" property="userName"/>
        <result column="sex" property="userSex"/>
        <result column="address" property="userAddress"/>
        <result column="birthday" property="userBirthday"/>
    </resultMap>

    <select id="findAll" rresultMap="userMap">
        select * from user;
    </select>
</mapper>
```

这种方式，开发效率更快，但是执行效率稍低。

## SqlMapConfig.xml 配置文件

### 配置内容

`SqlMapConfig.xml` 中配置的内容和顺序：

```
-properties（属性）
    --property
-settings（全局配置参数）
    --setting
-typeAliases（类型别名）
    --typeAliase
    --package
-typeHandlers（类型处理器）
-objectFactory（对象工厂）
-plugins（插件）
-environments（环境集合属性对象）
    --environment（环境子属性对象）
        ---transactionManager（事务管理）
        ---dataSource（数据源）
-mappers（映射器）
    --mapper
    --package
```

### properties 属性

在使用 properties 标签配置时，可以采用两种方式指定属性配置。

### typeAliases 类型别名

MyBatis 支持的默认别名，我们也可以采用自定义别名方式来开发。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!--Mybatis 的主配置文件-->
<configuration>

    <!-- 使用 typeAliases 配置别名，它只能配置 domain 中类的别名 -->
    <typeAliases>
        <!-- 单个别名定义，alias 指定别名，type 指定实体类全限定类名，当指定了别名就不区分大小了-->
<!--        <typeAlias alias="user" type="cn.parzulpan.domain.User"/>-->
        <!-- 批量别名定义，扫描整个包下的类，别名为类名，不区分大小-->
        <package name="cn.parzulpan.domain"/>
    </typeAliases>

</configuration>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!--持久层接口的映射文件 使用别名-->
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findAll" resultType="user">
        select * from user;
    </select>

    <select id="findById" resultType="USER" parameterType="java.lang.Integer">
        select * from user where id = #{id};
    </select>

    <insert id="saveUser" parameterType="cn.parzulpan.domain.User">
        # 扩展：新增用户 id 的返回值，在 insert 之后执行
        <selectKey keyColumn="id" keyProperty="id" resultType="int" order="AFTER">
            select last_insert_id();
        </selectKey>
        insert into user(username, birthday, sex, address) values (#{username}, #{birthday}, #{sex}, #{address});
    </insert>

</mapper>
```

### mappers 映射器

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!--Mybatis 的主配置文件-->
<configuration>
    <!--告知 MyBatis 映射配置的位置-->
    <mappers>
        <!-- XML 方式-->
        <!-- 使用相对于类路径的资源 -->
<!--        <mapper resource="cn/parzulpan/dao/UserDAO.xml"/>-->

        <!--指定被注解的 DAO 全限定类名-->
        <!-- 使用 mapper 接口类路径 -->
        <!-- 此种方法要求 mapper 接口名称 和 mapper 映射文件名称 相同，且放在同一个目录中 -->
<!--        <mapper class="cn.parzulpan.dao.UserDAO"/>-->

        <!-- 使用 package 标签，用于指定 DAO 接口所在的包，指定之后就不许写 mapper 以及 resource 或 class 了 -->
        <!-- 此种方法要求 mapper 接口名称 和 mapper 映射文件名称 相同，且放在同一个目录中 -->
        <package name="cn.parzulpan.dao"/>
    </mappers>
</configuration>
```

## 练习和总结
