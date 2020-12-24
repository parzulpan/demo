# SpringMVC 异常处理

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SpringMVCException)

## 异常处理思路

系统中异常包括两类：预期异常和运行时异常，前者通过捕获异常从而获取异常信息，后者主要通过规范代码开发、测试通过手段减少运行时异常的发生。

Controller 调用 service，service 调用 dao，异常都是向上抛出的，最终由 DispatcherServlet 寻找异常处理器进行异常的处理。

![异常处理思路](https://images.cnblogs.com/cnblogs_com/parzulpan/1905354/o_201224123237%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86%E6%80%9D%E8%B7%AF.png)

## 异常处理流程

### 自定义异常类和相关页面

```java
package cn.parzulpan.exception;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义异常类
 */

public class CustomException extends Exception{
    private String message; // 异常信息

    public CustomException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

---

src/main/webapp/WEB-INF/views/error.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>出错啦</title>
</head>
<body>
    出错啦~，${message}

</body>
</html>
```

---

src/main/webapp/WEB-INF/views/success.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>成功啦</title>
</head>
<body>
    成功啦...
</body>
</html>
```

### 自定义异常处理器

```java
package cn.parzulpan.exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义异常处理器
 */

public class CustomExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {

        e.printStackTrace();
        CustomException customException = null;
        if (e instanceof CustomException) {
            customException = (CustomException) e;
        } else {
            customException = new CustomException("系统错误！请联系相关人员！");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", customException.getMessage());
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
```

### 配置异常处理器

src/main/webapp/WEB-INF/dispatcher-servlet.xml

```xml
    <!-- 配置 异常处理器 -->
    <bean id="customExceptionResolver" class="cn.parzulpan.exception.CustomExceptionResolver"/>
```

### 测试使用

```java
package cn.parzulpan.web.controller;

import cn.parzulpan.exception.CustomException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/testException")
    public String testException() throws CustomException {
        System.out.println("called testException...");

        try {
            int a = 10 / 0; //模拟异常
        } catch (Exception e) {
            throw new CustomException("执行用户查询时出错了...");
        }

        return "success";
    }
}
```

---

src/main/webapp/index.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

    <a href="user/testException">模拟异常处理</a>

</body>
</html>
```

## 练习和总结
