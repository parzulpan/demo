# SpringBoot1.x RestfulCRUD

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/web-restful-crud)

## 添加资源

将所有的静态资源都添加到 `src/main/resources/static` 文件夹下，所有的模版资源都添加到 `src/main/resources/templates` 文件夹下。

创建数据库表，并编写对应实体类。

```sql
use web_restful_crud;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` int(11) primary key NOT NULL AUTO_INCREMENT,
  `departmentName` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

use web_restful_crud;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` int(11) primary key NOT NULL AUTO_INCREMENT,
  `lastName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` int(2) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `d_id` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

```java
public class Department {
    private Integer id;
    private String departmentName;
    // setter getter toString
}

public class Employee {
    private Integer id;
    private String lastName;
    private String email;
    private Integer gender; // 1 male, 0 female
    private Date birth;
    private Department department;
    // setter getter toString
}
```

## 默认访问首页

可以使用 WebMvcConfigurerAdapter 可以来扩展 SpringMVC 的功能，可以不用自己实现一个 ViewController。

src/main/java/cn/parzulpan/config/CustomMvcConfig.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自动以配置类，扩展 SpringMVC
 */

@Configuration
public class CustomMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送 /parzulpan 请求来到自定义 404 页面
        registry.addViewController("/parzulpan").setViewName("404");
    }

    // 将组件注册在容器，所有的 WebMvcConfigurerAdapter 组件都会一起起作用
    @Bean
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
        WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
            }
        };
        return adapter;
    }
}

```

## 国际化

国际化之前添加 Thymeleaf 支持。

```html
<html lang="en">

<link href="asserts/css/bootstrap.min.css" rel="stylesheet">
```

改为：

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">

<link href="asserts/css/bootstrap.min.css" th:href="@{/asserts/css/bootstrap.min.css}" rel="stylesheet">
```

这样做的好处是，当通过 `server.context-path=/crud` 更改项目路径时，静态文件可以自动匹配。

---

之前使用国际化的步骤：

* 编写国际化资源文件
* 使用 ResourceBundleMessageSource 管理国际化资源文件
* 在页面使用 `fmt:message` 取出国际化内容

SpringBoot 使用国际化的步骤：

* 编写国际化配置文件
* SpringBoot 自动配置好了管理国际化资源文件的组件 `MessageSourceAutoConfiguration`，它会根据浏览器语言设置的信息切换国际化，即默认的就是根据请求头带来的区域信息获取 Locale 进行国际化
* 如果想点击链接切换国际化，可以自定义 LocaleResolver（获取区域信息对象）

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 自定义区域信息解析器
    */

    public class CustomLocaleResolver implements LocaleResolver {
        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            // 获取请求中的语言参数
            String language = request.getParameter("locale");
            Locale locale = Locale.getDefault();    //如果没有就使用默认的
            // 如果请求的链接携带了国际化参数
            if (!StringUtils.isEmpty(language)){
                // zh_CN
                String[] split = language.split("_");
                // 国家，地区
                locale = new Locale(split[0], split[1]);
            }
            return locale;

        }

        @Override
        public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

        }
    }
    ```

* 然后在自定义配置类将自定义区域信息解析器添加到容器中

    ```java
    // 自定义区域信息解析器添加到容器中
    @Bean
    public LocaleResolver localResolver() {
        return new CustomLocaleResolver();
    }
    ```

## 登陆

开发期间模板引擎页面修改以后需要实时生效的步骤：

* 禁用模板引擎的缓存 `spring.thymeleaf.cache=false`
* 页面修改完成以后 `Ctrl+F9`，重新编译

使用拦截器进行**登陆检查**：

* 自定义登录拦截器

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 登录拦截器
    */

    public class LoginHandleInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            Object user = request.getSession().getAttribute("loginUser");
            if (user == null) {
                // 未登录，提示信息，并返回登录页面
                request.setAttribute("msg", "没有权限！请先登录！");
                request.getRequestDispatcher("/index").forward(request, response);
                return false;
            } else {
                // 已登录，放行请求
                return true;
            }
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        }
    }
    ```

* 注册拦截器到自动配置类

    ```java
    // 将组件注册在容器，所有的 WebMvcConfigurerAdapter 组件都会一起起作用
        @Bean
        public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
            WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
                // 注册拦截器
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    // SpringBoot 已经做好了静态资源映射
                    registry.addInterceptor(new LoginHandleInterceptor()).addPathPatterns("/**")
                            .excludePathPatterns("/", "/index", "/index.html", "/user/login");
                }
            };
            return adapter;
        }
    ```

## Thymeleaf 公共页面元素抽取

比如顶部栏和侧边栏都是公共页面，可以抽取出来。

* 抽取公共片段

    ```html
    <div th:fragment="copy">
    &copy; 2011 The Good Thymes Virtual Grocery
    </div>
    ```

* 引入公共片段

    ```html
    <!-- 
        ~{templatename::selector}：模板名::选择器
        ~{templatename::fragmentname}:模板名::片段名 
    -->
    <div th:insert="~{footer :: copy}"></div>
    ```

* 使用三种不同的引入公共片段的 `th属性`，引入公共片段后的效果
  * `th:insert` 将公共片段整个插入到声明引入的元素中

    ```html
    <div>
        <footer>
        &copy; 2011 The Good Thymes Virtual Grocery
        </footer>
    </div>
    ```

  * `th:replace` 将声明引入的元素替换为公共片段

    ```html
    <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
    ```

  * `th:include` 将被引入的片段的内容包含进这个标签中

    ```html
    <div>
    &copy; 2011 The Good Thymes Virtual Grocery
    </div>
    ```

## RestfulCRUD 分析

URI 格式 `/资源名称/资源标识`

HTTP **请求方式**区分对资源 CRUD 操作

| | 普通CRUD（uri来区分操作） | RestfulCRUD |
| :--- | :--- | :--- |
| 查询 | getEmp | emp & GET |
| 添加 | addEmp?xxx | emp & POST |
| 修改 | updateEmp?id=1 | emp/{id} & PUT |
| 删除 | deleteEmp?id=1 | emp/{id} & DELETE |

### 员工列表

* 查询所有员工
  * 请求URI `emps`
  * 请求方式 `GET`

```java
@Controller
public class EmployeeController {
    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /**
     * 查询所有员工，返回列表页面
     */
    @GetMapping("/emps")
    public String list(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps", employees);  // 结果放到请求域中
        return "emp/list";  // Thymeleaf 会自动拼串，classpath:/templates/emp/list.html
    }
}
```

### 员工添加

* 来到员工添加页面
  * 请求URI `emp`
  * 请求方式 `GET`
* 员工添加
  * 请求URI `emp`
  * 请求方式 `POST`

```java
@Controller
public class EmployeeController {
    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /**
     * 来到员工添加页面
     */
    @GetMapping("/emp")
    public String toAddPage(Model model) {
        // 查询所有部门，在页面显示
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "emp/add";
    }

    /**
     * 员工添加，SpringMVC 会自动进行参数绑定
     */
    @PostMapping("/emp")
    public String addEmp(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/emps";
    }
}
```

### 员工修改

* 来到员工修改页面
  * 请求URI `emp/{id}`
  * 请求方式 `GET`
* 员工修改
  * 请求URI `emp`
  * 请求方式 `PUT`

```java
@Controller
public class EmployeeController {
    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /**
     * 来到修改页面，查出当前员工，在页面回显
     */
    @GetMapping("/emp/{id}")
    public String toEditPage(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeDao.get(id);
        model.addAttribute("emp", employee);
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "emp/add";   // add.html 是一个修改和添加二合一的页面
    }

    /**
     * 员工修改，SpringMVC 会自动进行参数绑定
     */
    @PutMapping("/emp")
    public String updateEmp(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/emps";
    }
}
```

### 员工删除

* 查询所有员工
  * 请求URI `emp/{id}`
  * 请求方式 `DELETE`

```java
@Controller
public class EmployeeController {
    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /**
     * 员工删除
     */
    @DeleteMapping("/emp/{id}")
    public String deleteEmp(@PathVariable("id") Integer id) {
        employeeDao.delete(id);
        return "redirect:/emps";
    }
}
```

## 练习和总结
