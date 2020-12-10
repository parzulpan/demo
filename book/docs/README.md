# 书城网站 项目说明

[项目地址](https://github.com/parzulpan/demo)

## 阶段一 登录、注册的验证

* 使用 jQuery 技术对登录中的用户名、密码进行非空验证；

* 使用 jQuery 技术和正则表达式对注册中的用户名、密码、确认密码、邮箱进行格式验证，对验证码进行非空验证；

```html
<script type="text/javascript" src="static/script/jquery.js"></script>
<script type="text/javascript">
    $(function() {
        $("#sub_btn").click(function () {
            // 验证用户名：必须由字母，数字下划线组成，并且长度为 5 到 12 位
            // 1. 获取用户名输入框的内容
            let username = $("#username").val();
            // 2. 创建正则表达式对象
            let usernamePattern = /^\w{5,12}$/;
            // 3. 使用 test 方法验证
            if (!usernamePattern.test(username)) {
                // 4. 提示用户
                $("span.errorMsg").text("用户名格式错误！");

                return false;
            }

            // 验证密码：必须由字母，数字下划线组成，并且长度为 5 到 12 位
            let password = $("#password").val();
            let passwordPattern = /^\w{5,12}$/;
            if (!passwordPattern.test(password)) {
                $("span.errorMsg").text("密码格式错误！");

                return false;
            }

            // 验证确认密码：和密码相同
            let repwd = $("#repwd").val();
            if (repwd !== password) {
                $("span.errorMsg").text("密码不一致！");

                return false;
            }

            // 邮箱验证：xxxxx@xxx.com
            let email = $("#email").val();
            let emailPattern = /^[a-z\d]+(\.[a-z\d]+)*@([\da-z](-[\da-z])?)+(\.{1,2}[a-z]+)+$/;
            if (!emailPattern.test(email)) {
                $("span.errorMsg").text("邮箱格式错误！");

                return false;
            }
            // 验证码：现在只需要验证用户已输入。因为还没讲到服务器。验证码生成。
            let code = $("#code").val();
            let trimCode = $.trim(code);
            if (trimCode == null || trimCode === "") {
                $("span.errorMsg").text("验证码不能为空！");

                return false;
            }

            // 验证成功，去掉提示信息
            $("span.errorMsg").text("");
        })
    })
</script>

```

## 阶段二 实现登录、注册

### 软件的三层架构

* **UIL**（User Interface Layer 表现层）：主要是指用户交互的界面，用于接收用户输入的数据和显示处理后用户需要的数据；
* **BLL**（Business Logic Layer 业务逻辑层）：表现层和数据访问层之间的桥梁，实现业务逻辑，业务逻辑包括验证、计算、业务规划等；
* DAL（Date Access Layer 数据访问层）：主要实现对数据的增、删、改、查。将存储在数据库中的数据提交给业务层，同时将业务层处理的数据保存到数据库。

![软件的三层架构](../assets/软件的三层架构.png)

**三层架构的优点**：

* 结构清晰，耦合度低；
* 可维护性高，可扩展性高；
* 利于开发任务同步进行；
* 容易适应需求变化。

**三层架构的确定**：

* 降低了系统的性能，如果不采用分层式结构，很多业务可以直接造访数据库，以此获取相应的数据，如今却必须通过中间层来完成；
* 增加了代码量，增加了工作量；

### 书城的三层架构

* 表现层
  * HTML、Servlet
  * 接受用户的请求，调用业务逻辑层处理用户请求，显示处理结果
* 业务逻辑层
  * Service
  * 调用数据访问层处理业务逻辑
  * 采用面向接口编程的思想，先定义接口，再创建实现类
* 数据访问层
  * DAO
  * 用来操作数据库，对数据库进行增删改查
  * 采用面向接口编程的思想，先定义接口，再创建实现类

![项目架构](../assets/项目架构.png)

**用户注册**：

* 访问注册页面
* 填写注册信息，提交给服务器
* 服务器应该保存用户
* 当用户已经存在，提示用户注册失败，用户名已存在
* 当用户不存在，注册成功

**用户登录**：

* 访问登陆页面
* 填写用户名密码后提交
* 服务器判断用户是否存在
* 如果登陆失败，返回用户名或者密码错误信息
* 如果登录成功，返回登陆成功信息

## 阶段三 动态化及局部优化

为了动态提示信息，所以需要动态化。

### 页面 jsp 动态化

* 在 html 页面顶行添加 page 指令
* 修改 `.html` 文件后缀名为：`.jsp`
* 使用 IDEA 搜索替换各个文件内容的 `.html` 为 `.jsp` （快捷键：`Ctrl+Shift+R`）

### 抽取页面中相同内容

1. head 中 css、jquery、base 标签

```jsp
<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/10
  Time: 12:03 上午
  To change this template use File | Settings | File Templates.

  抽取页面中相同的内容 head 中 css、jquery、base 标签
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme()
            + "://"
            + request.getServerName()
            + ":"
            + request.getServerPort()
            + request.getContextPath()
            + "/";
%>

<!--写 base 标签，永远固定相对路径跳转的结果-->
<base href="<%=basePath%>">
<link type="text/css" rel="stylesheet" href="static/css/style.css" >
<script type="text/javascript" src="static/script/jquery-1.7.2.js"></script>

```

2. 每个页面的页脚

```jsp
<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/10
  Time: 12:12 上午
  To change this template use File | Settings | File Templates.
  抽取页面中相同的内容 每个页面的页脚
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="bottom">
		<span>
			购书城.Copyright &copy;2020
		</span>
</div>

```

3. 登录成功后的菜单

```jsp
<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/10
  Time: 12:12 上午
  To change this template use File | Settings | File Templates.
  抽取页面中相同的内容 登录成功后的菜单
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <span>欢迎<span class="um_span"> JackMa</span>光临购书城</span>
    <a href="pages/order/order.jsp">我的订单</a>
    <a href="index.jsp">注销</a>&nbsp;&nbsp;
    <a href="index.jsp">返回</a>
</div>

```

4. manager 模块的菜单

```jsp
<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/10
  Time: 12:12 上午
  To change this template use File | Settings | File Templates.
  抽取页面中相同的内容 manager 模块的菜单
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <a href="pages/manager/book_manager.jsp">图书管理</a>
    <a href="pages/manager/order_manager.jsp">订单管理</a>
    <a href="index.jsp">返回商城</a>
</div>

```

### 登录注册错误提示及表单回显

流程：

* 客户端：
  * 发送请求，登录或者注册
* 服务器：
  * 只要失败，就会跳回原来的页面，把错误信息和回显的表单项信息保存（Request 域），回传给客户端

### BaseServlet 抽取

在实际的项目开发中，一个模块，一般只使用一个 Servlet 程序。

1. 代码优化一：合并 LoginServlet 和 RegistServlet 程序为 UserServlet 程序
2. 优化代码二：使用反射优化大量 else if 代码
3. 代码优化三：抽取 BaseServlet 程序
  * 获取 action 参数值
  * 通过反射获取 action 对应的业务方法
  * 通过反射调用业务方法
  * 其他 Servlet 继承 BaseServlet

### 数据的封装和抽取 BeanUtils 的使用

BeanUtils 工具类，它可以一次性的把所有请求的参数注入到 JavaBean 中。

BeanUtils 工具类，经常用于把 Map 中的值注入到 JavaBean 中，或者是对象属性值的拷贝操作。

BeanUtils 它不是 Jdk 的类。而是第三方的工具类。所以需要导包。

1. 导入需要的 jar 包：
  * `commons-beanutils-1.8.0.jar`
  * `commons-logging-1.1.1.jar`
2. 编写 WebUtils 工具类使用
  
  ```java
  package cn.parzulpan.utils;
  
  import org.apache.commons.beanutils.BeanUtils;
  
  import java.util.Map;
  
  /**
   * @Author : parzulpan
   * @Time : 2020-12-10
   * @Desc : WebUtils 工具类
   */
  
  public class WebUtils {
  
      /**
       * 一次性的把所有请求的参数注入到 JavaBean 中
       * @param value
       * @param bean
       * @param <T>
       * @return
       */
      public static <T> T copyParamToBean(Map value, T bean) {
  
          try {
  //            System.out.println("注入之前：" + bean);
  
              BeanUtils.populate(bean, value);
  
  //            System.out.println("注入之后：" + bean);
          } catch (Exception e) {
              e.printStackTrace();
          }
  
          return bean;
      }
  }
  ```

## 阶段四 使用 EL 表达式修改表单回显

使用 EL 表达式可以简化表单的回显：

```jsp
                            <div class="msg_cont">
								<b></b>
								<span class="errorMsg">
<%--									输出回显信息--%>
<%--									<%=request.getAttribute("msg")==null?"请输入用户名和密码":request.getAttribute("msg")%>--%>
<%--									使用 EL 表达式 简化回显信息--%>
									${ empty requestScope.msg ? "请输入用户名和密码" : requestScope.msg}
								</span>
							</div>
							<div class="form">
<!--								修改注册表单的提交地址和请求方式-->
<!--								<form action="login_success.jsp">-->
<%--								<form action="loginServlet" method="post">--%>
<%--								添加隐藏域和修改请求地址--%>
								<form action="userServlet" method="post">
									<input type="hidden" name="action" value="login"/>
									<label>用户名称：</label>
									<input class="itxt" type="text" placeholder="请输入用户名"
										   autocomplete="off" tabindex="1" name="username"
<%--										   输出回显信息--%>
<%--										   value="<%=request.getAttribute("username")==null?"":request.getAttribute("username")%>"--%>
<%--										   使用 EL 表达式 简化回显信息--%>
										   value="${requestScope.username}"
									/>
									<br />
									<br />
									<label>用户密码：</label>
									<input class="itxt" type="password" placeholder="请输入密码"
										   autocomplete="off" tabindex="1" name="password" />
									<br />
									<br />
									<input type="submit" value="登录" id="sub_btn" />
								</form>
							</div>
```

## 阶段五 图书的增删改查

### MVC 概念

MVC 即 Model 模型、View 视图、Controller 控制器。MVC 最早出现在 JavaEE 三层中的 Web 层，它可以有效的指导 Web 层的代码如何有效分离，单独工作。

* Model 模型：将与业务逻辑相关的数据封装为具体的 JavaBean 类，其中不掺杂任何与数据处理相关的代码。（JavaBean、Domain、Entity）
* View 视图：只负责数据和界面的显示，不接受任何与显示数据无关的代码，便于程序员和美工的分工合作。（JSP、HTML）
* Controller 控制器：只负责接收请求，调用业务层的代码处理请求，然后派发页面（转到某个页面或者是重定向到某个页面），是一个“调度者”的角色。（Servlet）

MVC 的作用是为降低耦合，让代码合理分层，方便后期升级和维护。

### 图书模块

实现图书的增删改查。

#### 编写数据库表

```sql
drop table t_book;

create table t_book(
    `id` int primary key auto_increment,
    `name` varchar(200),
    `author` varchar(100),
    `price` decimal(11, 2),
    `sales` int,
    `stock` int,
    `imgPath` varchar(200)
);

insert into t_book(`id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath`) value
    (null , 'Java 从入门到放弃' , '大哥' , 80 , 9999 , 9 , 'static/img/default.jpg'),
    (null , '数据结构与算法' , '严敏君' , 78.5 , 6 , 13 , 'static/img/default.jpg'),
    (null , '怎样拐跑别人的媳妇' , '龙伍' , 68, 99999 , 52 , 'static/img/default.jpg'),
    (null , 'C++编程思想' , '二哥' , 45.5 , 14 , 95 , 'static/img/default.jpg'),
    (null , '蛋炒饭' , '周星星' , 9.9, 12 , 53 , 'static/img/default.jpg'),
    (null , '赌神' , '龙伍' , 66.5, 125 , 535 , 'static/img/default.jpg'),
    (null , 'Java编程思想' , '阳哥' , 99.5 , 47 , 36 , 'static/img/default.jpg'),
    (null , 'JavaScript从入门到精通' , '婷姐' , 9.9 , 85 , 95 , 'static/img/default.jpg'),
    (null , 'Cocos2d-x游戏编程入门' , '大哥' , 49, 52 , 62 , 'static/img/default.jpg'),
    (null , 'C语言程序设计' , '谭浩强' , 28 , 52 , 74 , 'static/img/default.jpg'),
    (null , 'Lua语言程序设计' , '雷丰阳' , 51.5 , 48 , 82 , 'static/img/default.jpg'),
    (null , '西游记' , '罗贯中' , 12, 19 , 9999 , 'static/img/default.jpg'),
    (null , '水浒传' , '华仔' , 33.05 , 22 , 88 , 'static/img/default.jpg'),
    (null , '操作系统原理' , '刘优' , 133.05 , 122 , 188 , 'static/img/default.jpg'),
    (null , '数据结构 java版' , '封大神' , 173.15 , 21 , 81 , 'static/img/default.jpg'),
    (null , 'UNIX高级环境编程' , '乐天' , 99.15 , 210 , 810 , 'static/img/default.jpg'),
    (null , 'JavaScript高级编程' , '大哥' , 69.15 , 210 , 810 , 'static/img/default.jpg'),
    (null , '大话设计模式' , '大哥' , 89.15 , 20 , 10 , 'static/img/default.jpg'),
    (null , '人月神话' , '二哥' , 88.15 , 20 , 80 , 'static/img/default.jpg');
```

#### 编写 JavaBean

```java
public class Book {
    private Integer id;
    private String name;
    private String author;
    private BigDecimal price;
    private Integer sales;
    private Integer stock;
    private String imgPath = "static/img/default.jpg";
}
```

#### 编写 Dao 和测试

1. BookDAO 接口

```java
package cn.parzulpan.dao;

import cn.parzulpan.bean.Book;

import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc : 用于规范 Book 表的常用操作
 */

public interface BookDAO {

    /**
     * 增加一本书
     * @param connection 数据库连接
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int addBook(Connection connection, Book book);

    /**
     * 根据 书的 id 删除一本书
     * @param connection 数据库连接
     * @param id 书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int deleteBookById(Connection connection, Integer id);

    /**
     * 更新一本书
     * @param connection 数据库连接
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int updateBook(Connection connection, Book book);

    /**
     * 根据 书的 id 查询一本书
     * @param connection 数据库连接
     * @param id 书的 id
     * @return Book Bean
     */
    public Book queryBookById(Connection connection, Integer id);

    /**
     * 查询所有书
     * @param connection 数据库连接
     * @return Book Bean List
     */
    public List<Book> queryBooks(Connection connection);
}
```

2. BookDAOImpl 实现类

```java
package cn.parzulpan.dao;

import cn.parzulpan.bean.Book;
import cn.parzulpan.utils.JDBCUtils;

import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookDAOImpl extends BaseDAO<Book> implements BookDAO {
    /**
     * 增加一本书
     *
     * @param connection 数据库连接
     * @param book       Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int addBook(Connection connection, Book book) {
        String sql = "insert into t_book(`name`, `author`, `price`, `sales`, `stock`, `imgPath`) values (?, ?, ?, ?, ?, ?)";
        return update(connection, sql,
                book.getName(), book.getAuthor(), book.getPrice(), book.getSales(), book.getStock(), book.getImgPath());
    }

    /**
     * 根据 书的 id 删除一本书
     *
     * @param connection 数据库连接
     * @param id         书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int deleteBookById(Connection connection, Integer id) {
        String sql = "delete from t_book where id = ?";
        return update(connection, sql, id);
    }

    /**
     * 更新一本书
     *
     * @param connection 数据库连接
     * @param book       Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int updateBook(Connection connection, Book book) {
        String sql = "update t_book set `name` = ?, `author` = ?, `price` = ?, `sales` = ?, `stock` = ?, `imgPath` = ? where id = ?";
        return update(connection, sql, book.getName(), book.getAuthor(), book.getPrice(), book.getSales(),
                book.getStock(), book.getImgPath(), book.getId());
    }

    /**
     * 根据 书的 id 查询一本书
     *
     * @param connection 数据库连接
     * @param id         书的 id
     * @return Book Bean
     */
    @Override
    public Book queryBookById(Connection connection, Integer id) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book where id = ?";
        return getBean(connection, sql, id);
    }

    /**
     * 查询所有书
     *
     * @param connection 数据库连接
     * @return Book Bean List
     */
    @Override
    public List<Book> queryBooks(Connection connection) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book";
        return getBeanList(connection, sql);
    }
}
```

3. BookDAOImplTest 单元测试

```java
package cn.parzulpan.test;

import cn.parzulpan.bean.Book;
import cn.parzulpan.dao.BookDAO;
import cn.parzulpan.dao.BookDAOImpl;
import cn.parzulpan.utils.JDBCUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;


/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookDAOImplTest {
    private BookDAO bookDAO = new BookDAOImpl();

    @Test
    public void addBook() {
        Connection connection = JDBCUtils.getConnection();
        int addBook = bookDAO.addBook(connection,
                new Book(null, "测试的书", "测试的作者",
                        new BigDecimal(120), 100, 10, null));
        System.out.println(addBook);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void deleteBookById() {
        Connection connection = JDBCUtils.getConnection();
        int deleteBookById = bookDAO.deleteBookById(connection, 20);
        System.out.println(deleteBookById);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void updateBook() {
        Connection connection = JDBCUtils.getConnection();
        int updateBook = bookDAO.updateBook(connection,
                new Book(2, "更新的书", "更新的作者",
                        new BigDecimal(120), 100, 10, null));
        System.out.println(updateBook);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void queryBookById() {
        Connection connection = JDBCUtils.getConnection();
        Book queryBookById = bookDAO.queryBookById(connection, 2);
        System.out.println(queryBookById);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void queryBooks() {
        Connection connection = JDBCUtils.getConnection();
        List<Book> books = bookDAO.queryBooks(connection);
        books.forEach(System.out::println);
        JDBCUtils.close(connection, null, null);
    }
}
```

#### 编写 Service 和测试

1. BookService 接口

```java
package cn.parzulpan.service;

import cn.parzulpan.bean.Book;
import cn.parzulpan.dao.BookDAOImpl;

import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public interface BookService {

    /**
     * 增加一本书
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int addBook(Book book);

    /**
     * 根据 书的 id 删除一本书
     * @param id 书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int deleteBookById(Integer id);

    /**
     * 更新一本书
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int updateBook(Book book);

    /**
     * 根据 书的 id 查询一本书
     * @param id 书的 id
     * @return Book Bean
     */
    public Book queryBookById(Integer id);

    /**
     * 查询所有书
     * @return Book Bean List
     */
    public List<Book> queryBooks();
}
```

2. BookServiceImpl 实现类

```java
package cn.parzulpan.service;

import cn.parzulpan.bean.Book;
import cn.parzulpan.dao.BookDAO;
import cn.parzulpan.dao.BookDAOImpl;
import cn.parzulpan.utils.JDBCUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookServiceImpl implements BookService {
    private BookDAO bookDAO = new BookDAOImpl();

    /**
     * 增加一本书
     *
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int addBook(Book book) {
        Connection connection = JDBCUtils.getConnection();
        int i = bookDAO.addBook(connection, book);
        JDBCUtils.close(connection, null, null);
        return i;
    }

    /**
     * 根据 书的 id 删除一本书
     *
     * @param id 书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int deleteBookById(Integer id) {
        Connection connection = JDBCUtils.getConnection();
        int deleteBookById = bookDAO.deleteBookById(connection, id);
        JDBCUtils.close(connection, null, null);
        return deleteBookById;
    }

    /**
     * 更新一本书
     *
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int updateBook(Book book) {
        Connection connection = JDBCUtils.getConnection();
        int updateBook = bookDAO.updateBook(connection, book);
        JDBCUtils.close(connection, null, null);
        return updateBook;
    }

    /**
     * 根据 书的 id 查询一本书
     *
     * @param id 书的 id
     * @return Book Bean
     */
    @Override
    public Book queryBookById(Integer id) {
        Connection connection = JDBCUtils.getConnection();
        Book queryBookById = bookDAO.queryBookById(connection, id);
        JDBCUtils.close(connection, null, null);
        return queryBookById;
    }

    /**
     * 查询所有书
     *
     * @return Book Bean List
     */
    @Override
    public List<Book> queryBooks() {
        Connection connection = JDBCUtils.getConnection();
        List<Book> books = bookDAO.queryBooks(connection);
        JDBCUtils.close(connection, null, null);
        return books;
    }
}
```

3. BookServiceImplTest 单元测试

```java
package cn.parzulpan.test;

import cn.parzulpan.bean.Book;
import cn.parzulpan.dao.BookDAO;
import cn.parzulpan.dao.BookDAOImpl;
import cn.parzulpan.utils.JDBCUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;


/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookDAOImplTest {
    private BookDAO bookDAO = new BookDAOImpl();

    @Test
    public void addBook() {
        Connection connection = JDBCUtils.getConnection();
        int addBook = bookDAO.addBook(connection,
                new Book(null, "测试的书", "测试的作者",
                        new BigDecimal(120), 100, 10, null));
        System.out.println(addBook);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void deleteBookById() {
        Connection connection = JDBCUtils.getConnection();
        int deleteBookById = bookDAO.deleteBookById(connection, 20);
        System.out.println(deleteBookById);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void updateBook() {
        Connection connection = JDBCUtils.getConnection();
        int updateBook = bookDAO.updateBook(connection,
                new Book(2, "更新的书", "更新的作者",
                        new BigDecimal(120), 100, 10, null));
        System.out.println(updateBook);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void queryBookById() {
        Connection connection = JDBCUtils.getConnection();
        Book queryBookById = bookDAO.queryBookById(connection, 2);
        System.out.println(queryBookById);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void queryBooks() {
        Connection connection = JDBCUtils.getConnection();
        List<Book> books = bookDAO.queryBooks(connection);
        books.forEach(System.out::println);
        JDBCUtils.close(connection, null, null);
    }
}
```


#### 编写 Web 和测试

##### 图书列表功能的实现

**实现步骤**：

* 在后台管理页面点击**图书管理**，所以需要修改图书管理请求地址 `<a href="bookServlet?action=list">图书管理</a>`
* 在 BookServlet 程序中添加 list 方法：
  * 查询全部图书
  * 保存到 Request 域中
  * **请求转发**到 book_manager.jsp 图书管理页面
* book_manager.jsp 图书管理页面展示所有的图书信息
  * 从 Request 域中获取全部图书信息
  * 使用 `JSTL` 标签库遍历输出
     * 导入 `taglibs-standard-impl-1.2.1.jar` 和 `taglibs-standard-spec-1.2.1.jar`
     * 修改 book_manager.jsp 页面的数据遍历输出

```java
@WebServlet(name = "BookServlet", urlPatterns = ("/bookServlet"))
public class BookServlet extends BaseServlet {
    private BookService bookService  = new BookServiceImpl();

    // 查询全部图书
    protected void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Book> books = bookService.queryBooks();
        request.setAttribute("books", books);
        // 请求转发，这里不能用请求重定向，想想为什么？
        // 因为请求转发的特点是：浏览器地址栏不发生变化；一次请求；共享 Request 域中的数据
        // 当用户提交完请求，浏览器会记录下最后一次请求的全部信息，当用户按下功能键 F5，就会发起浏览器记录的最后一次
           请求。
        // 所以必须是一次请求
        request.getRequestDispatcher("/pages/manager/book_manager.jsp").forward(request, response);
    }
}
```

##### 添加图书功能的实现

**实现步骤**：

* 在图书管理页面点击添加按钮，跳转到 book_edit.jsp 添加图书页面
* 在添加图书页面填写相关信息，点击提交按钮
* 在 BookServlet 程序中添加 add 方法：
  * 获取请求的参数，封装成为 Book 对象
  * 调用 bookService.addBook(book) 保存图书
  * **请求重定向**图书列表页面

```java
@WebServlet(name = "BookServlet", urlPatterns = ("/bookServlet"))
public class BookServlet extends BaseServlet {
    private BookService bookService  = new BookServiceImpl();

    // 添加图书
    protected void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 中文编码问题
        Book book = WebUtils.copyParamToBean(request.getParameterMap(), new Book());
        bookService.addBook(book);
        // 请求重定向，这里不能用请求转发，想想为什么？
        // 因为请求重定向的特点是：浏览器地址栏会发生变化；两次请求；不共享 Request 域中的数据
        response.sendRedirect("bookServlet?action=list");
    }
}
```

##### 删除图书功能的实现

**实现步骤**：

* 在图书管理页面点击删除按钮（`<td><a class="deleteClass" href="bookServlet?action=delete&id=${book.id}">删除</a></td>`）
* 给删除添加确认提示操作
* 在 BookServlet 程序中添加 delete 方法：
  * 获取请求的参数 id
  * 调用 bookService.deleteBookById(id) 删除图书
  * **请求重定向**图书列表页面

```java
@WebServlet(name = "BookServlet", urlPatterns = ("/bookServlet"))
public class BookServlet extends BaseServlet {
    private BookService bookService  = new BookServiceImpl();

    // 删除图书
    protected void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = WebUtils.parseInt(request.getParameter("id"));
        bookService.deleteBookById(id);
        response.sendRedirect("bookServlet?action=list");
    }
}
```

##### 修改图书功能的实现

**实现步骤**：

* 在图书管理页面点击修改按钮（`<td><a href="bookServlet?action=getBook&id=${book.id}">修改</a></td>`）
* 在 BookServlet 程序中添加 getBook 方法，获取要修改的图书信息：
  * 获取图书编号
  * 调用 bookService.queryBookById(id) 获取图书信息
  * 把信息保存到 Request 域中
  * **请求转发**到 `book_edit.jsp` 图书编辑页面
* 在 BookServlet 程序中添加 update 方法，保存修改图书的操作：
  * 获取请求的参数，封装成为 Book 对象
  * 调用 bookService.updateBook(book) 修改图书
  * **请求重定向**图书列表页面
* 解决 `book_edit.jsp` 图书编辑页面，即要实现添加，又要实现修改操作：
  * 方案一：可以请求发起时，附带上当前要操作的值，并注入到隐藏域中 `<td><a href="bookServlet?action=getBook&id=${book.id}&method=add">修改</a></td>
                                     					   <td><a href="pages/manager/book_edit.jsp&method=update">添加图书</a></td>`
  * **方案二**：可以通过判断当前请求参数中是否包含 id 参数，如果有说明是修改操作，否则是添加操作 `<input type="hidden" name="action" value="${ empty param.id ? "add" : "update" }"/>`
  * 方案三：可以通过判断 Request 域中是否包含要修改的图书信息对象，如果有说明是修改操作 `<input type="hidden" name="id" value="${ empty requestScope.book ? "add" : "update }"/>`

```java
@WebServlet(name = "BookServlet", urlPatterns = ("/bookServlet"))
public class BookServlet extends BaseServlet {
    private BookService bookService  = new BookServiceImpl();

    // 查询图书
    protected void getBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = WebUtils.parseInt(request.getParameter("id"));
        Book book = bookService.queryBookById(id);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/pages/manager/book_edit.jsp").forward(request, response);
    }

    // 更新图书
    protected void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Book book = WebUtils.copyParamToBean(request.getParameterMap(), new Book());
        bookService.updateBook(book);
        response.sendRedirect("bookServlet?action=list");
    }
}
```

### 图书分页

#### 分页模块的分析

#### 分页模型 Page 的抽取

#### 分页的初步实现

#### 首页、上一页、下一页、末页实现

#### 跳转到指定页实现

#### 页码显示规范

#### 增加回显页码

#### 首页的跳转

#### 分页条的抽取

#### 首页价格搜索

## 阶段六 登录、登出、验证码、购物车

## 阶段七 结账及添加事务
