# SpringMVC 拦截器

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SpringMVCHandlerInterceptor)

## 拦截器的作用

SpringMVC 的处理器拦截器类似于 Servlet 开发中的过滤器 Filter，用于**对处理器进行预处理和后处理**。

谈到拦截器，还有另外一个概念 —— **拦截器链（Interceptor Chain）**。拦截器链就是将拦截器按一定的顺序联结成一条链，在访问被拦截的方法或字段时，拦截器链中的拦截器就会按其之前定义的顺序被调用。

处理器拦截器和过滤器的异同：

* 过滤器是 Servlet 规范中的一部分，任何 JavaWeb 工程都可以使用。而拦截器是 SpringMVC 框架独有的，只有使用了 SpringMVC 框架的工程才能使用。
* 过滤器在 `url-pattern` 中配置了 `/*` 之后，可以对所有要访问的资源拦截。而拦截器它是只会拦截访问的**控制器方法**，如果访问的是静态资源则不会进行拦截。

自定义拦截器，要求必须实现 `HandlerInterceptor` 接口。

## 自定义拦截器

第一步 编写一个普通类实现 HandlerInterceptor 接口：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义拦截器
 */

public class CustomInterceptor1 implements HandlerInterceptor {

    /**
     * 预处理，Controller 方法执行前
     * 可以使用转发或者重定向直接跳转到指定的页面
     * @param request
     * @param response
     * @param handler
     * @return true 代表放行，执行下一个拦截器，如果没有则执行 Controller 中的方法；false 代表不放行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("called CustomInterceptor1 preHandle...");

//        request.getRequestDispatcher("/WEB_INF/views/error.jsp").forward(request, response);

        return true;
    }

    /**
     * 后处理，Controller 方法执行后，success.jsp 执行前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("called CustomInterceptor1 postHandle...");

//        request.getRequestDispatcher("/WEB_INF/views/error.jsp").forward(request, response);
    }

    /**
     * success.jsp 执行后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("called CustomInterceptor1 afterCompletion...");
    }
}
```

---

第二步 配置拦截器：

```xml
    <!-- 配置 处理器拦截器 -->
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>   <!-- 要拦截的方法 -->
            <!-- <mvc:exclude-mapping path="/**"/>  不要拦截的方法 -->
            <bean id="customInterceptor1" class="cn.parzulpan.interceptor.CustomInterceptor1"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

---

第三步 测试结果：

```java
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>拦截器的使用</title>
</head>
<body>

    <a href="user/testInterceptor">拦截器的使用</a>

</body>
</html>

```

输出：

```txt
called CustomInterceptor1 preHandle...
called testInterceptor...
called CustomInterceptor1 postHandle...
called success.jsp ...
called CustomInterceptor1 afterCompletion...
```

## 拦截器的细节

* **preHandle 方法**：
  * 按拦截器定义顺序调用
  * 只要配置了就会调用
  * 如果决定该拦截器对请求进行拦截处理后还要调用其他的拦截器，或者是业务处理器去进行处理，则返回 true。如果决定不需要再调用其他的组件去处理请求，则返回 false。
* **postHandle 方法**：
  * 按拦截器定义逆序调用
  * 在拦截器链内所有拦截器返回 true 就会调用
  * 在业务处理器处理完请求后，但是 DispatcherServlet 向客户端返回响应前被调用，可以在该方法中对用户请求 request 进行处理
* **afterCompletion 方法**：
  * 按拦截器定义逆序调用
  * 只有 preHandle 返回 true 才调用
  * 在 DispatcherServlet 完全处理完请求后被调用，可以在该方法中进行一些资源清理的操作

**多个拦截器的执行顺序**：

假设多个拦截器配置为：

```xml
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>
            <bean id="customInterceptor1" class="cn.parzulpan.interceptor.CustomInterceptor1"/>
        </mvc:interceptor>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>
            <bean id="customInterceptor2" class="cn.parzulpan.interceptor.CustomInterceptor2"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

则 执行顺序为：

![多个拦截器的执行顺序](https://images.cnblogs.com/cnblogs_com/parzulpan/1905354/o_201225024846%E5%A4%9A%E4%B8%AA%E6%8B%A6%E6%88%AA%E5%99%A8%E7%9A%84%E6%89%A7%E8%A1%8C%E9%A1%BA%E5%BA%8F.png)

## 拦截器的简单使用

**需求**：

验证用户是否登录

**实现**：

* 1、有一个登录页面，需要写一个 controller 访问页面
* 2、登录页面有一提交表单的动作，需要在 controller 中处理。
  * 2.1、判断用户名密码是否正确
  * 2.2、如果正确 向 session 中写入用户信息
  * 2.3、返回登录成功。
* 3、拦截用户请求，判断用户是否登录
  * 3.1、如果用户已经登录。放行
  * 3.2、如果用户未登录，跳转到登录页面

---

用户登录控制器 LoginController.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户登录控制器
 */

@Controller
public class LoginController {

    /**
     * 登录页面
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    public String login(Model model) throws Exception {
        System.out.println("called login...");

        return "login";
    }

    /**
     * 提交登录
     * @param session
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping("/loginSubmit")
    public String loginSubmit(HttpSession session, String username, String password) throws Exception {
        System.out.println("called loginSubmit...");
        System.out.println(username + " " + password);

        session.setAttribute("activeUser", username);   // 向 session 记录用户身份信息

        return "forward:/WEB-INF/views/main.jsp";
    }

    /**
     * 退出登录
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("logout")
    public String logout(HttpSession session) throws Exception {
        System.out.println("called logout...");
        session.invalidate();   // 设置 session 过期

        return "redirect:/index.jsp";
    }

    /**
     * 测试页面
     * @return
     * @throws Exception
     */
    @RequestMapping("/test")
    public String test() throws Exception {
        System.out.println("called test...");

        return "test";
    }
```

---

用户登录拦截器 LoginInterceptor.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户登录拦截器
 */

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("called LoginInterceptor preHandle...");

        //如果是登录页面则放行
        if (request.getRequestURI().contains("login")) {
            System.out.println("登录页面");
            return true;
        }

        // 如果用户已登录则放行
        HttpSession session = request.getSession();
        if (session.getAttribute("activeUser") != null) {
            System.out.println("用户已登录");
            return true;
        }

        // 用户没有登录跳转到登录页面
        System.out.println("用户没有登录");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

        return false;
    }
}
```

---

views 页面

src/main/webapp/WEB-INF/views/login.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
</head>
<body>
    欢迎登录～
    <br>
    <form action="loginSubmit" method="post">
        用户名：<input type="text" name="username"/><br>
        密码：<input type="password" name="password"><br>
        <input type="submit" value="登录">
    </form>
</body>
</html>
```

src/main/webapp/WEB-INF/views/main.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>首页</title>
</head>
<body>
    恭喜您，登录成功～
    <br>
    <a href="logout">退出登录</a>
    <a href="test">进入测试页面</a>
</body>
</html>
```

src/main/webapp/WEB-INF/views/test.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    我已登录，可以进入测试页面～
    <br>
    <a href="logout">退出登录</a>
</body>
</html>
```

---

测试页面

src/main/webapp/index.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>拦截器的使用</title>
</head>
<body>

    <a href="login">用户登录</a>
    <a href="test">测试页面</a>

</body>
</html>
```

## 练习和总结
