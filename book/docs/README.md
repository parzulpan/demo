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

## 阶段四 动态化及局部优化

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

## 阶段六 登录、登出、验证码、购物车

## 阶段七 结账及添加事务
