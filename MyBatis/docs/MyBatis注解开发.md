# MyBatis 注解开发

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/MyBatisAnnotation)

## 环境搭建

Mybatis 也可以使用注解开发方式，这样就可以减少编写 Mapper 映射文件。

常用注解说明：

* `@Insert` 实现新增
* `@Update` 实现更新
* `@Delete` 实现删除
* `@Select` 实现查询
* `@Result` 实现结果集封装
* `@Results` 可以与 `@Result` 一起使用，封装多个结果集
* `@ResultMap` 实现引用 `@Results` 定义的封装
* `@One` 实现一对一结果集封装
* `@Many` 实现一对多结果集封装
* `@SelectProvider` 实现动态 SQL 映射
* `@CacheNamespace` 实现注解二级缓存的使用

## 单表 CRUD

实现复杂关系映射之前可以在映射文件中通过配置 `<resultMap>` 来实现，在使用注解开发时需要借助 `@Results` 注解，`@Result` 注解，`@One` 注解，`@Many` 注解。

* `@Results` 代替了 `<id>` 标签和 `<result>` 标签，属性介绍：
  * id 是否是主键字段
  * column 数据库的列名
  * property 需要装配的属性名
  * one 需要使用的 @One 注解 `@Result(one=@One)()`
  * many 需要使用的 @Many 注解（`@Result(many=@Many)()`
* `@One`（一对一） 代替了 `<assocation>` 标签，是多表查询的关键，在注解中用来指定子查询返回单一对象，属性介绍：
  * select 指定用来多表查询的 sqlmapper
  * fetchType 会覆盖全局的配置参数 lazyLoadingEnabled
  * 使用格式：`@Result(column=" ", property="", one=@One(select=""))`
* `@Many`（多对一）代替了 `<collection>` 标签，是是多表查询的关键，在注解中用来指定子查询返回对象集合，
  * 使用格式：`@Result(property="", column="", many=@Many(select=""))`

* 编写实体类

    ```java
    public class User implements Serializable {
        private Integer id;
        private String username;
        private Date birthday;
        private String sex;
        private String address;
    }
    ```

* 使用注解方式开发持久层接口

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.User;
    import org.apache.ibatis.annotations.*;

    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 用户的持久层接口，使用注解开发
    */

    public interface UserDAO {

        /**
        * 查询所有用户
        * @return
        */
        @Select("select * from user")
        @Results(id = "userMap",
                value = {
                @Result(id = true, column = "id", property = "userId"),
                @Result(column = "username", property = "userName"),
                @Result(column = "birthday", property = "userBirthday"),
                @Result(column = "sex", property = "userSex"),
                @Result(column = "address", property = "userAddress")
                })
        List<User> findAll();

        /**
        * 根据 id 查询一个用户
        * @param userId
        * @return
        */
        @Select("select * from user where id = #{uid}")
        @ResultMap(value = {"userMap"})
        User findById(Integer userId);

        /**
        * 插入操作
        * @param user
        * @return
        */
        @Insert("insert into user(username, birthday, sex, address) values (#{userName}, #{userBirthday}, #{userSex}, #{userAddress})")
        @SelectKey(keyColumn = "id", keyProperty = "userId", resultType = Integer.class, before = false,
                statement = {"select last_insert_id()"})
        int saveUser(User user);

        /**
        * 更新操作
        * @param user
        * @return
        */
        @Update("update user set username = #{userName}, birthday = #{userBirthday}, sex = #{userSex}, " +
                "address = #{userAddress} where id = #{userId}")
        int updateUser(User user);

        /**
        * 删除操作
        * @param userId
        * @return
        */
        @Delete("delete from user where id = #{uid}")
        int deleteUser(Integer userId);

        /**
        * 使用聚合函数查询
        * @return
        */
        @Select("select count(*) from user")
        int findTotal();

        /**
        *
        * @param name
        * @return
        */
        @Select("select * from user where username like #{username}")
        @ResultMap(value = {"userMap"})
        List<User> findByName(String name);

    }
    ```

* 编写 SqlMapConfig 配置文件

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
            PUBLIC "-//mabatis.org//DTD Congfig 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">

    <configuration>
        <!-- 配置 properties 文件的位置 -->
        <properties resource="JDBCConfig.properties"/>

        <!-- 配置别名 -->
        <typeAliases>
            <package name="cn.parzulpan.domain"/>
        </typeAliases>

        <!-- 配置 MySQL 环境 -->
        <environments default="mysql">
            <environment id="mysql">
                <transactionManager type="JDBC"/>
                <dataSource type="POOLED">
                    <property name="driver" value="${jdbc.driver}"/>
                    <property name="url" value="${jdbc.url}"/>
                    <property name="username" value="${jdbc.username}"/>
                    <property name="password" value="${jdbc.password}"/>
                </dataSource>
            </environment>
        </environments>

        <!-- 配置映射信息 -->
        <mappers>
            <package name="cn.parzulpan.dao"/>
        </mappers>
    </configuration>
    ```

* 编写测试方法

    ```java
    package cn.parzulpan;

    import cn.parzulpan.dao.UserDAO;
    import cn.parzulpan.domain.User;
    import org.apache.ibatis.io.Resources;
    import org.apache.ibatis.session.SqlSession;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.apache.ibatis.session.SqlSessionFactoryBuilder;
    import org.junit.After;
    import org.junit.Before;
    import org.junit.Test;

    import java.io.InputStream;
    import java.util.Date;
    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    public class MyBatisAnnotationCRUDTest {
        private InputStream is;
        private SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        private SqlSessionFactory sqlSessionFactory;
        private SqlSession sqlSession;
        private UserDAO userDAO;

        @Before
        public void init() throws Exception {
            System.out.println("Before...");
            is = Resources.getResourceAsStream("SqlMapConfig.xml");
            sqlSessionFactory = builder.build(is);
            sqlSession = sqlSessionFactory.openSession();
            userDAO = sqlSession.getMapper(UserDAO.class);
        }

        @After
        public void destroy() throws Exception {
            System.out.println("After...");
            sqlSession.commit();
            sqlSession.close();
            is.close();
        }

        @Test
        public void findAllTest() {
            List<User> users = userDAO.findAll();
            for (User user : users) {
                System.out.println(user);
            }
        }

        @Test
        public void findByIdTest() {
            User user = userDAO.findById(41);
            System.out.println(user);
        }

        @Test
        public void saveUserTest() {
            User user = new User(null, "annotation username", new Date(), "男", "Beijing");
            System.out.println("save before: " + user); // User{id=null, ...}
            int i = userDAO.saveUser(user);
            System.out.println(i);
            System.out.println("save after: " + user); // User{id=53, ...}
        }

        @Test
        public void updateUserTest() {
            User user = userDAO.findById(42);
            user.setUserName("Tom Tim Tom AA");
            user.setUserAddress("瑞典");
            int i = userDAO.updateUser(user);
            System.out.println(i);
        }

        @Test
        public void deleteUserTest() {
            int i = userDAO.deleteUser(53);
            System.out.println(i);
        }

        @Test
        public void findTotalTest() {
            int total = userDAO.findTotal();
            System.out.println(total);
        }

        @Test
        public void findByNameTest() {
            List<User> users = userDAO.findByName("%Tim%");
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    ```

## 多表查询

### 一对一

**需求**：加载账户信息时并且加载该账户的用户信息，根据情况可实现延迟加载。

* 添加 User 实体类及 Account 实体类

    ```java
    public class Account implements Serializable {
        private Integer id;
        private Integer uid;
        private Double money;

        private User user;  // 一对一
    }
    ```

* 添加账户的持久层接口并使用注解配置

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.Account;
    import org.apache.ibatis.annotations.One;
    import org.apache.ibatis.annotations.Result;
    import org.apache.ibatis.annotations.Results;
    import org.apache.ibatis.annotations.Select;
    import org.apache.ibatis.mapping.FetchType;

    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    public interface AccountDAO {

        /**
        * 查询所有账户，采用延迟加载的方式查询账户的所属用户
        * @return
        */
        @Select("select * from account")
        @Results(id = "accountMap", value = {
                @Result(id = true, column = "id", property = "id"),
                @Result(column = "uid", property = "uid"),
                @Result(column = "money", property = "money"),
                @Result(column = "uid", property = "user", one = @One(select = "cn.parzulpan.dao.UserDAO.findById", fetchType = FetchType.LAZY))
        })
        List<Account> findAll();
    }

    ```

* 测试一对一关联及延迟加载

    ```java
    @Test
        public void findAllTest() {
            List<Account> accounts = accountDAO.findAll();
            for (Account account : accounts) {
                System.out.println();
                System.out.println(account);
                System.out.println(account.getUser());
            }
        }
    ``

### 一对多

**需求**：查询用户信息时，也要查询他的账户列表。使用注解方式实现。

* User 实体类加入 `List<Account>`

    ```java
    public class User implements Serializable {
        private Integer userId; // 注意这里的和数据库表的列名不一致
        private String userName;
        private Date userBirthday;
        private String userSex;
        private String userAddress;


        private List<Account> accounts; //一对多关系映射：主表方法应该包含一个从表方的集合引用
    }
    ```

* 编写用户的持久层接口并使用注解配置

    ```java
    public interface UserDAO {
        /**
        * 查询所有用户，包括账户列表
        * @return
        */
        @Select("select * from user")
        @Results(id = "userMapWithAccount",
                value = {
                        @Result(id = true, column = "id", property = "userId"),
                        @Result(column = "username", property = "userName"),
                        @Result(column = "birthday", property = "userBirthday"),
                        @Result(column = "sex", property = "userSex"),
                        @Result(column = "address", property = "userAddress"),
                        @Result(column = "id", property = "accounts", many = @Many(
                                select = "cn.parzulpan.dao.AccountDAO.findByUid",
                                fetchType = FetchType.LAZY
                        ))
                })
        List<User> findAllWithAccount();
    }
    ```

* 编写账户的持久层接口并使用注解配置

    ```java
    public interface AccountDAO {

        /**
        * 根据用户 id 查询用户下的所有账户
        * @param userId
        * @return
        */
        @Select("select * from account where uid = #{uid} ")
        List<Account> findByUid(Integer userId);
    }
    ```

* 添加测试方法

    ```java
    package cn.parzulpan;

    import cn.parzulpan.dao.UserDAO;
    import cn.parzulpan.domain.Account;
    import cn.parzulpan.domain.User;
    import org.apache.ibatis.io.Resources;
    import org.apache.ibatis.session.SqlSession;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.apache.ibatis.session.SqlSessionFactoryBuilder;
    import org.junit.After;
    import org.junit.Before;
    import org.junit.Test;

    import java.io.InputStream;
    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    public class MyBatisAccountManyTest {
        private InputStream is;
        private SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        private SqlSessionFactory sqlSessionFactory;
        private SqlSession sqlSession;
        private UserDAO userDAO;

        @Before
        public void init() throws Exception {
            System.out.println("Before...");
            is = Resources.getResourceAsStream("SqlMapConfig.xml");
            sqlSessionFactory = builder.build(is);
            sqlSession = sqlSessionFactory.openSession();
            userDAO = sqlSession.getMapper(UserDAO.class);
        }

        @After
        public void destroy() throws Exception {
            System.out.println("After...");
            sqlSession.commit();
            sqlSession.close();
            is.close();
        }

        @Test
        public void findAllWithAccountTest() {
            List<User> users = userDAO.findAllWithAccount();
            for (User user : users) {
                System.out.println();
                System.out.println(user);
                System.out.println(user.getAccounts());
            }
        }
    }

    ```

## 缓存配置

* 在 SqlMapConfig 中开启二级缓存支持

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
            PUBLIC "-//mabatis.org//DTD Congfig 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">

    <configuration>
        <!-- 配置二级缓存 -->
        <settings>
            <!-- 开启二级缓存的支持 -->
            <setting name="cacheEnabled" value="true"/>
        </settings>
    </configuration>
    ```

* 在持久层接口中使用注解配置二级缓存

    ```java
    @CacheNamespace(blocking = true)    // 基于注解方式实现配置二级缓存
    public interface UserDAO {
    }
    ```

* 测试

    ```java
    @Test
    public void findByIdHighCacheTest() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        UserDAO dao1 = sqlSession1.getMapper(UserDAO.class);
        User user1 = dao1.findById(41);
        System.out.println(user1.hashCode());   // 765284253
        sqlSession1.close();    // 一级缓存消失

        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        UserDAO dao2 = sqlSession2.getMapper(UserDAO.class);
        User user2 = dao2.findById(41);
        System.out.println(user2.hashCode());   // 1043351526
        sqlSession1.close();    // 一级缓存消失

        System.out.println(user1 == user2); // false

    }
    ```

## 练习和总结
