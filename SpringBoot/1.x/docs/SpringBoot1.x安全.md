# SpringBoot1.x 安全

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-security)

## 环境搭建

SpringSecurity 是针对 Spring 项目的安全框架，也是 SpringBoot 底层安全模块默认的技术选型。他可以实现强大的 web 安全控制。对于安全控制，我们仅需引入 `spring-boot-starter-security` 模块，进行少量的配置，即可实现强大的安全管理。

## 登录 注销 认证 授权 权限控制

应用程序的两个主要区域是 **“认证”** 和 **“授权（或者访问控制）”**。这两个主要区域是 SpringSecurity 的两个目标。

通过继承 WebSecurityConfigurerAdapter，可以自定义 Security 策略。使用 @EnableWebSecurity，开启 WebSecurity 模式。

“认证”（Authentication），是建立一个它声明的主体的过程（一个“主体”一般是指用户，设备或一些可以在你的应用程序中执行动作的其他系统）。

“授权”（Authorization），指确定一个主体是否允许在你的应用程序执行一个动作的过程。为了抵达需要授权的点，主体的身份已经由认证过程建立。

Thymeleaf 提供的 SpringSecurity 标签支持，需要引入依赖：

```xml
        <!-- Thymeleaf 提供的 SpringSecurity 标签支持 -->
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity4</artifactId>
        </dependency>
```

---

* 首页 welcome.html

    ```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org"
        xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
    </head>
    <body>
    <h1 align="center">欢迎光临武林秘籍管理系统</h1>
    <div sec:authorize="!isAuthenticated()">
        <h2 align="center">游客您好，如果想查看武林秘籍 <a th:href="@{/userlogin}">请登录</a> </h2>
    </div>
    <div sec:authorize="isAuthenticated()">
        <h2 th:align="center">用户：<span sec:authentication="name"></span>，角色：<span sec:authentication="principal.authorities"></span></h2>
        <form th:align="center" th:action="@{/logout}" method="post">
            <input type="submit" value="注销">
        </form>
    </div>

    <hr>

    <div sec:authorize="hasRole('VIP1')">
        <h3>普通武功秘籍</h3>
        <ul>
            <li><a th:href="@{/level1/1}">罗汉拳</a></li>
            <li><a th:href="@{/level1/2}">武当长拳</a></li>
            <li><a th:href="@{/level1/3}">全真剑法</a></li>
        </ul>
    </div>

    <div sec:authorize="hasRole('VIP2')">
        <h3>高级武功秘籍</h3>
        <ul>
            <li><a th:href="@{/level2/1}">太极拳</a></li>
            <li><a th:href="@{/level2/2}">七伤拳</a></li>
            <li><a th:href="@{/level2/3}">梯云纵</a></li>
        </ul>
    </div>

    <div sec:authorize="hasRole('VIP3')">
        <h3>绝世武功秘籍</h3>
        <ul>
            <li><a th:href="@{/level3/1}">葵花宝典</a></li>
            <li><a th:href="@{/level3/2}">龟派气功</a></li>
            <li><a th:href="@{/level3/3}">独孤九剑</a></li>
        </ul>
    </div>

    </body>
    </html>
    ```

* 登录页 login.html

    ```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
    </head>
    <body>
        <h1 align="center">欢迎登陆武林秘籍管理系统</h1>
        <hr>
        <div align="center">
            <form th:action="@{/userlogin}" method="post">
                用户名:<input name="user"/><br>
                密码:<input name="pwd"><br/>
                <input type="checkbox" name="remember"> 记住我 <br>
                <input type="submit" value="登陆">
            </form>
        </div>
    </body>
    </html>
    ```

* 自定义安全配置类 CustomSecurityConfig.java

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2021-01
    * @Desc : 自定义安全配置类
    */

    @EnableWebSecurity  // 开启 WebSecurity 模式
    public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

        // 定制请求的授权规则
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/").permitAll()
                    .antMatchers("/level1/**").hasRole("VIP1")
                    .antMatchers("/level2/**").hasRole("VIP2")
                    .antMatchers("/level3/**").hasRole("VIP3");

            // 开启自动配置的登录功能，如果没有登录或者没有权限就会来到登录页面
            // 1. 访问 /login 来到登录页
            // 2. 重定向到 login?error 表示登录失败
            // 3. ...
            // http.formLogin();

            // 指定自定义的登录页
            // 1. 默认 post 形式，/login 代表处理登录
            // 2. 一旦定制 loginPage，那么 loginPage 的 post 请求就是登录
            http.formLogin().usernameParameter("user").passwordParameter("pwd")
                    .loginPage("/userlogin");

            // 关闭 CSRF（Cross-site request forgery）跨站请求伪造
            // http.csrf().disable();

            // 开启自动配置的注销功能
            // 1. 访问 /logout 表示用户注销，清空 session
            // 2. 注销成功，会自动重定向到 login?logout
            // 3. logoutSuccessUrl 注销成功，自动回到首页
            http.logout().logoutSuccessUrl("/");

            // 开启记住我功能
            // 1. 登录成功以后，将 cookie 发给浏览器保存，以后访问页面会带上这个 cookie，只要通过检查就可以免登录
            // 2. 点击注销就会删除 cookie
            http.rememberMe().rememberMeParameter("remember");

        }

        // 定制认证规则
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("User01").password("123456").roles("VIP1", "VIP2")
                .and()
                .withUser("User02").password("123456").roles("VIP2", "VIP3")
                .and()
                .withUser("User03").password("123456").roles("VIP3", "VIP1");
        }
    }
    ```

* 书籍控制类 KungfuController.java

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2021-01
    * @Desc : 书籍控制类
    */

    @Controller
    public class KungfuController {
        private final String PREFIX = "pages/";

        /**
            * 欢迎页
            * @return
            */
        @GetMapping("/")
        public String index() {
            return "welcome";
        }

        /**
            * 登陆页
            * @return
            */
        @GetMapping("/userlogin")
        public String loginPage() {
            return PREFIX+"login";
        }

        /**
            * level1页面映射
            * @param path
            * @return
            */
        @GetMapping("/level1/{path}")
        public String level1(@PathVariable("path")String path) {
            return PREFIX+"level1/"+path;
        }

        /**
            * level2页面映射
            * @param path
            * @return
            */
        @GetMapping("/level2/{path}")
        public String level2(@PathVariable("path")String path) {
            return PREFIX+"level2/"+path;
        }

        /**
            * level3页面映射
            * @param path
            * @return
            */
        @GetMapping("/level3/{path}")
        public String level3(@PathVariable("path")String path) {
            return PREFIX+"level3/"+path;
        }
    }
    ```

## 总结和练习
