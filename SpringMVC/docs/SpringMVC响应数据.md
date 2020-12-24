# SpringMVC 响应数据

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SpringMVCResponseData)

## 返回值分类

### 返回值是字符串

Controller 方法返回字符串可以指定逻辑视图的名称，通过视图解析器解析为物理视图的地址。

```java
@Controller
@RequestMapping(path = {"/return"})
public class ReturnController {

    @RequestMapping(path = {"/testReturnString"})
    public String testReturnString(Model model) {
        System.out.println("testReturnString");

        // 模拟从数据库中查询的数据
        User user = new User();
        user.setUsername("张三");
        user.setPassword("123");
        model.addAttribute("user", user);
        return "update";
    }
}
```

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>修改用户</title>
</head>
<body>
    修改用户成功

    ${ user.username }
    ${ user.password }

</body>
</html>
```

### 返回值是 void

如果控制器的方法返回值编写成 void，执行程序报 404 的异常，默认查找 JSP 页面没有找到。

可以使用请求转发或者重定向跳转到指定的页面。

```java
@Controller
@RequestMapping(path = {"/return"})
public class ReturnController {

    @RequestMapping(path = {"/testReturnVoid"})
    public void testReturnVoid(HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println("testReturnVoid");

        // 使用 request 请求转发
        request.getRequestDispatcher("/WEB-INF/views/update.jsp").forward(request, response);

        // 使用 response 重定向
        response.sendRedirect("testReturnString");

        // 使用 response 指定响应结果
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("name: value");
    }
}
```

### 返回值是 ModelAndView

ModelAndView 是 SpringMVC 为提供的一个对象，该对象可以用作控制器方法的返回值。

```java
@Controller
@RequestMapping(path = {"/return"})
public class ReturnController {

    @RequestMapping(path = {"testReturnModelAndView"})
    public ModelAndView testReturnModelAndView() {
        System.out.println("testReturnModelAndView");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("success");  // // 跳转到 success.jsp 的页面

        // 模拟从数据库中查询所有的用户信息
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("张三");
        user1.setPassword("123");
        User user2 = new User();
        user2.setUsername("赵四");
        user2.setPassword("456");
        users.add(user1);
        users.add(user2);

        mv.addObject("users", users);

        return mv;
    }
}
```

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>Success</title>
</head>
<body>
    ${users}
</body>
</html>
```

## 请求转发和重定向

**forward 请求转发**，Controller 方法返回 String 类型，想进行请求转发也可以编写成：

```java
@Controller
@RequestMapping(path = {"/"})
public class ForwardAndRedirectController {

    @RequestMapping(path = "/testForward")
    public String testForward() {
        System.out.println("请求转发");

        return "forward:/WEB-INF/views/success.jsp";
    }
}
```

**值得注意的是**，如果用了 `formward:` 则路径必须写成 实际视图 url，不能写逻辑视图。使用请求转发，既可以转发到 jsp，也可以转发到其他的控制器方法。

---

**redirect 重定向**，Controller 方法返回 String 类型，想进行重定向也可以编写成：

```java
@Controller
@RequestMapping(path = {"/"})
public class ForwardAndRedirectController {

    @RequestMapping(path = "/testRedirect")
    public String testRedirect() {
        System.out.println("请求重定向");

        return "redirect:/index.jsp";
    }
}
```

**值得注意的是**，jsp 页面不能写在 WEB-INF 目录中，否则无法找到。

---

**请求转发**，是指服务器收到请求后，从一个资源跳转到另一个资源的操作叫请求转发。

**请求重定向**，是指客户端给服务器发请求，然后服务器告诉客户端说，我给你一些地址，你去新地址访问（因为之前的地址可能已经被废弃）。

**请求转发的特点**：

* 浏览器地址栏没有变化；
* 它们是一次请求；
* 它们共享 Request 域中的数据；
* 可以转发到 WEB-INF 目录下；
* 不可以访问工程以外的资源。

**请求重定向的特点**：

* 浏览器地址栏会发生变化；
* 两次请求；
* 不共享 Request 域中的数据；
* 不能访问 WEB-INF 下的资源；
* 可以访问工程外的资源。

## @ResponseBody

这个注解用于将 Controller 的方法返回的对象，通过 HttpMessageConverter 接口转换为指定格式的数据如：**json**、xml 等，通过 Response 响应给客户端。

值得注意的是，SpringMVC 默认用 MappingJacksonHttpMessageConverter 对 json 数据进行转换，所以需要加入jackson 的依赖。

```xml
 <properties>
    <jsckson.version>2.10.5</jsckson.version>
  </properties>

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>${jsckson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>${jsckson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>${jsckson.version}</version>
    </dependency>
```

DispatcherServlet 会拦截到所有的资源，导致一个问题就是静态资源（img、css、js）也会被拦截到，从而不能被使用。解决方法就是需要配置静态资源不进行拦截，在 SpringMVC 配置文件添加如下配置：

```xml
    <!-- 设置静态资源不过滤 -->
    <mvc:resources mapping="/static/js/**" location="/static/js/"/>
    <mvc:resources mapping="/static/css/**" location="/static/css/"/>
    <mvc:resources mapping="/static/images/**" location="/static/images/"/>
```

**需求**：使用 @ResponseBody 注解实现将 Controller 方法返回对象转换为 json 响应给客户端。

---

responseBoby.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>ResponseBody响应json数据</title>
    <script type="text/javascript" src="static/js/jquery.js"></script>
<%--    <script src="${ctx}/webjars/jquery/3.5.1/jquery.min.js"></script>--%>
    <script>
        $(function () {
            $("#btn").click( function () {
                // alert("hello");

                $.ajax({
                    // 编写 JSON 格式，设置属性和值
                    url: "user/testAjax",
                    contentType: "application/json;charset=UTF-8",
                    data: '{"username": "parzulpan潘", "password": "parzulpan0101"}',
                    dataType: "json",
                    type: "post",
                    success: function (data) {
                        // 服务器端响应的 JSON 数据
                        alert(data);
                        alert(data.password);
                    }
                });
            });
        });
    </script>
</head>
<body>

    <button id="btn">发送 AJAX 请求</button>

</body>
</html>
```

---

UserController.java

```java
package cn.parzulpan.web.controller;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 
 */

@Controller
@RequestMapping(path = {"/user"})
public class UserController {

    /**
     * 模拟异步请求响应
     * @return
     */
    @RequestMapping(path = {"/testAjax"})
    public @ResponseBody User testAjax(@RequestBody User user) {
        System.out.println("called testAjax...");
        System.out.println(user);
        user.setPassword("Mparzulpan0101");
        return user;
    }
}

```

## 练习和总结
