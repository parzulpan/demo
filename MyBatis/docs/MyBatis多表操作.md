# MyBatis 多表操作

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/MyBatisCRUD)

## 一对一查询

**需求**：查询所有账户信息，关联查询下单用户信息。

**注意**：因为一个账户信息只能供某个用户使用，所以从**查询账户信息出发**关联查询用户信息为**一对一**查询。如果从**用户信息出发**关联查询用户下的账户信息则为**一对多**查询，因为一个用户可以有多个账户。

可以使用 resultMap，定义专门的 resultMap 用于映射一对一查询结果。

* 定义账户信息的数据表

    ```sql
    DROP TABLE IF EXISTS `account`;

    CREATE TABLE `account` (
                            `ID` int(11) NOT NULL COMMENT '编号',
                            `UID` int(11) default NULL COMMENT '用户编号',
                            `MONEY` double default NULL COMMENT '金额',
                            PRIMARY KEY  (`ID`),
                            KEY `FK_Reference_8` (`UID`),
                            CONSTRAINT `FK_Reference_8` FOREIGN KEY (`UID`) REFERENCES `user` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    insert  into `account`(`ID`,`UID`,`MONEY`) values (1,41,1000),(2,45,1000),(3,41,2000);
    ```

* 定义账户信息的实体类

    ```java
    package cn.parzulpan.domain;

    import java.io.Serializable;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    public class Account implements Serializable {
        private Integer id;
        private Integer uid;
        private Double money;
        private User user;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUid() {
            return uid;
        }

        public void setUid(Integer uid) {
            this.uid = uid;
        }

        public Double getMoney() {
            return money;
        }

        public void setMoney(Double money) {
            this.money = money;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "id=" + id +
                    ", uid=" + uid +
                    ", money=" + money +
                    ", user=" + user +
                    '}';
        }
    }
    ```

* 编写 SQL

    ```sql
    select u.*, a.id as aid, a.uid, a.money
    from account a, user u
    where a.uid = u.id;
    ```

* 定义账户的持久层 DAO 接口

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.Account;

    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    public interface AccountDAO {

        /**
        * 查询所有账户，同时获取账户的所属用户名称以及它的地址信息
        * @return
        */
        List<Account> findAll();
    }
    ```

* 配置 AccountDAO.xml 文件

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="cn.parzulpan.dao.AccountDAO">

        <!-- 建立对应关系 -->
        <resultMap id="accountMap" type="account">
            <id column="aid" property="id"/>
            <result column="uid" property="uid"/>
            <result column="money" property="money"/>

            <!-- 用于指定从数据表方的引用实体属性 -->
            <association property="user" javaType="user">
                <id column="id" property="id"/>
                <result column="username" property="username"/>
                <result column="sex" property="sex"/>
                <result column="birthday" property="birthday"/>
                <result column="address" property="address"/>
            </association>
        </resultMap>

        <select id="findAll" resultMap="accountMap">
            select u.*, a.id as aid, a.uid, a.money
            from account a, user u
            where a.uid = u.id;
        </select>
    </mapper>
    ```

* 测试

    ```java
    package cn.parzulpan.test;

    import cn.parzulpan.dao.AccountDAO;
    import cn.parzulpan.domain.Account;
    import org.apache.ibatis.io.Resources;
    import org.apache.ibatis.session.SqlSession;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.apache.ibatis.session.SqlSessionFactoryBuilder;
    import org.junit.After;
    import org.junit.Before;
    import org.junit.Test;

    import java.io.IOException;
    import java.io.InputStream;
    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 多表查询
    */

    public class MyBatisQueryTest {
        private InputStream is;
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        private SqlSessionFactory factory;
        private SqlSession session;
        private AccountDAO accountDAO;

        @Before
        public void init() throws IOException {
            is = Resources.getResourceAsStream("SqlMapConfig.xml");
            factory = builder.build(is);
            session = factory.openSession();
            accountDAO = session.getMapper(AccountDAO.class);
        }

        @After
        public void destroy() throws IOException {
            session.commit();   // 事务提交
            session.close();
            is.close();
        }

        @Test
        public void findAllTest() {
            List<Account> accounts = accountDAO.findAll();
            for (Account account : accounts) {
                System.out.println(account);
                System.out.println(account.getUser());
            }
        }
    }

    ```

## 一对多查询

**需求**：查询所有用户信息及用户关联的账户信息。

**分析**：用户信息和他的账户信息为**一对多**关系，并且查询过程中如果用户没有账户信息，此时也要将用户信息查询出来，所以**左外连接**查询比较合适。

* 编写 SQL

    ```sql
    select u.*, a.id as id, a.uid, a.money
    from user u left join account a on u.id = a. uid;
    ```

* User 类加入 `List<Account>`
* 用户持久层 DAO 接口中加入查询方法

    ```java
    /**
    * 查询所有用户，同时获取出每个用户下的所有账户信息
    * @return
    */
    List<User> findAllAndAccount();
    ```

* 配置 UserDAO.xml 文件

    ```xml
        <resultMap id="userMap" type="user">
            <id column="id" property="id"/>
            <result column="username" property="username"/>
            <result column="address" property="address"/>
            <result column="sex" property="sex"/>
            <result column="birthday" property="birthday"/>
            
            <!-- collection 是用于建立一对多中集合属性的对应关系 ofType 用于指定集合元素的数据类型 -->
            <collection property="accounts" ofType="account">
                <id column="aid" property="id"/>
                <result column="uid" property="uid"/>
                <result column="money" property="money"/>
            </collection>
        </resultMap>

        <select id="findAllAndAccount" resultMap="userMap">
            select u.*, a.id as aid, a.uid, a.money
            from user u left join account a on u.id = a. uid;
        </select>
    ```

* 测试

    ```java
    @Test
    public void findAllAndAccountTest() {
        List<User> users = userDAO.findAllAndAccount();
        for (User user : users) {
            System.out.println();
            System.out.println("----- " + user.getUsername() + " -----");
            System.out.println(user);
            System.out.println(user.getAccounts());
        }
    }
    ```

## 多对多查询

**需求**：实现查询所有对象并且加载它所分配的用户信息。

**注意**：查询角色我们需要用到Role表，但角色分配的用户的信息我们并不能直接找到用户信息，而是要通过**中间表**（USER_ROLE 表）才能关联到用户信息。

* 定义相关的数据表

    ```sql
    # -----

    DROP TABLE IF EXISTS `role`;

    CREATE TABLE `role` (
                            `ID` int(11) NOT NULL COMMENT '编号',
                            `ROLE_NAME` varchar(30) default NULL COMMENT '角色名称',
                            `ROLE_DESC` varchar(60) default NULL COMMENT '角色描述',
                            PRIMARY KEY  (`ID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    insert  into `role`(`ID`,`ROLE_NAME`,`ROLE_DESC`) values (1,'院长','管理整个学院'),(2,'总裁','管理整个公司'),(3,'校长','管理整个学校');

    # -----

    DROP TABLE IF EXISTS `user_role`;

    CREATE TABLE `user_role` (
                                `UID` int(11) NOT NULL COMMENT '用户编号',
                                `RID` int(11) NOT NULL COMMENT '角色编号',
                                PRIMARY KEY  (`UID`,`RID`),
                                KEY `FK_Reference_10` (`RID`),
                                CONSTRAINT `FK_Reference_10` FOREIGN KEY (`RID`) REFERENCES `role` (`ID`),
                                CONSTRAINT `FK_Reference_9` FOREIGN KEY (`UID`) REFERENCES `user` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    insert  into `user_role`(`UID`,`RID`) values (41,1),(45,1),(41,2);
    ```

* 定义角色信息的实体类

    ```java
    package cn.parzulpan.domain;

    import java.io.Serializable;
    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 角色实体类
    */

    public class Role implements Serializable {
        private Integer roleId;
        private String roleName;
        private String roleDesc;

        private List<User> users;   //多对多的关系映射：一个角色可以赋予多个用户

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleDesc() {
            return roleDesc;
        }

        public void setRoleDesc(String roleDesc) {
            this.roleDesc = roleDesc;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        @Override
        public String toString() {
            return "Role{" +
                    "roleId=" + roleId +
                    ", roleName='" + roleName + '\'' +
                    ", roleDesc='" + roleDesc + '\'' +
                    ", users=" + users +
                    '}';
        }
    }
    ```

* 编写 RoleDAO 持久层接口

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.Role;

    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    public interface RoleDAO {

        /**
        * 查询所有角色
        * @return
        */
        List<Role> findAll();
    }

    ```

* 编写映射文件

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="cn.parzulpan.dao.RoleDAO">

        <!-- 建立对应关系 -->
        <resultMap id="roleMap" type="role">
            <id property="roleId" column="rid"/>
            <result property="roleName" column="role_name"/>
            <result property="roleDesc" column="role_desc"/>
            <collection property="users" ofType="user">
                <id column="id" property="id"/>
                <result column="username" property="username"/>
                <result column="address" property="address"/>
                <result column="sex" property="sex"/>
                <result column="birthday" property="birthday"/>
            </collection>
        </resultMap>

        <select id="findAll" resultMap="roleMap">
            select r.id as rid, r.role_name, r.role_desc, u.*
            from role r left outer join user_role ur on r.id = ur.rid left outer join user u on u.id = ur.uid
        </select>
    </mapper>
    ```

* 测试

    ```java
    package cn.parzulpan.test;

    import cn.parzulpan.dao.AccountDAO;
    import cn.parzulpan.dao.RoleDAO;
    import cn.parzulpan.domain.Account;
    import cn.parzulpan.domain.Role;
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
    import java.util.List;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 多表查询
    */

    public class MyBatisManyQueryTest {
        private InputStream is;
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        private SqlSessionFactory factory;
        private SqlSession session;
        private RoleDAO roleDAO;

        @Before
        public void init() throws IOException {
            is = Resources.getResourceAsStream("SqlMapConfig.xml");
            factory = builder.build(is);
            session = factory.openSession();
            roleDAO = session.getMapper(RoleDAO.class);
        }

        @After
        public void destroy() throws IOException {
            session.commit();   // 事务提交
            session.close();
            is.close();
        }

        @Test
        public void findAllTest() {
            List<Role> roles = roleDAO.findAll();
            for (Role role : roles) {
                System.out.println();
                System.out.println("----- " +  " -----");
                System.out.println(role);
                if (role != null) {
                    System.out.println(role.getUsers());
                }
            }
        }
    }
    ```

## 练习和总结
