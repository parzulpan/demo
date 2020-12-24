# SpringMVC 实现文件上传

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SpringMVCFileOperator)

## 文件上传回顾

[查看 JavaWeb 阶段的文件上传下载](https://www.cnblogs.com/parzulpan/p/14135671.html#%E6%96%87%E4%BB%B6%E7%9A%84%E4%B8%8A%E4%BC%A0%E5%92%8C%E4%B8%8B%E8%BD%BD)

**实现步骤：**

* 客户端：
  * 发送 post 请求，告诉服务器要上传什么文件
* 服务器：
  * 要有一个 `form` 标签，`method=post` 请求，form 标签的 **encType** 属性值必须为 `multipart/form-data` 值
  * 在 `form` 标签中使用 `input type=file` 添加上传的文件接收并处理上传的文件

**文件上传时 HTTP 协议说明：**

* `Content-type` 表示提交的数据类型
  * `multipart/form-data` 表示提交的数据，以多段（每一个表单项代表一个数据段）的形式进行拼接，然后以二进制流的形式发送给服务器
  * `boundary` 表示每段数据的分隔符，它的值是有浏览器随机生成的，它是每段数据的分割符

**实现上传下载功能常用两个包：**

* `commons-fileupload-1.3.1.jar`
* `commons-io-2.4.jar`

---

FileUploadController.java

```java
@Controller
@RequestMapping("/file")
public class FileUploadController {

    /**
     * 文件上传回顾
     * @return
     */
    @RequestMapping(path = {"/upload1"})
    public String upload1(HttpServletRequest request) throws Exception {
        System.out.println("called upload1...");

        String path = request.getSession().getServletContext().getRealPath("/uploads"); // 获取到要上传的文件目录
        System.out.println(path);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();    // 创建磁盘文件项工厂
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        List<FileItem> fileItems = fileUpload.parseRequest(request);    // 解析request对象
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {   // 判断文件项是普通字段，还是上传的文件
                System.out.println(fileItem.getName());
            } else {
                String itemName = fileItem.getName();   // 获取到上传文件的名称
                itemName = UUID.randomUUID().toString() + "-" + itemName;   // 把文件名唯一化
                fileItem.write(new File(file, itemName));   // 上传文件
                fileItem.delete();
            }
        }

        return "success";
    }
}
```

---

index.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
    <h3>文件上传回顾</h3>
    <form action="file/upload1" method="post" enctype="multipart/form-data">
        名称：<input type="text" name="picName"/><br/>
        图片：<input type="file" name="picFile"/><br>
        <input type="submit" value="上传文件"/>
    </form>
</body>
</html>
```

## SpringMVC 传统方式的文件上传

传统方式的文件上传，指的是我们上传的文件和访问的应用存在于**同一台服务器**上。并且上传完成之后，浏览器可能跳转。

SpringMVC 框架提供了 MultipartFile 对象，该对象表示上传的文件，要求变量名称必须和表单 file 标签的 name 属性**名称相同**。并且需要**配置文件解析器对象**。

```xml
    <!-- 配置 文件解析器，要求 id 名称必须是 multipartResolver -->
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <property name="maxUploadSize" value="10485760"/>
    </bean>
```

---

FileUploadController.java

```java
@Controller
@RequestMapping("/file")
public class FileUploadController {

    /**
     * SpringMVC 传统方式的文件上传
     * @return
     */
    @RequestMapping(path = {"/upload2"})
    public String upload2(HttpServletRequest request, String picName, MultipartFile picFile) throws Exception {
        System.out.println("called upload2...");
        String path = request.getSession().getServletContext().getRealPath("/uploads"); // 获取到要上传的文件目录
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        String fileName = picName + "-" + picFile.getOriginalFilename();
        fileName = UUID.randomUUID().toString() + "-" + fileName;
        picFile.transferTo(new File(file, fileName));   // 上传文件

        return "success";
    }
}
```

---

index.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
    <h3>SpringMVC 传统方式文件上传</h3>
    <form action="file/upload2" method="post" enctype="multipart/form-data">
        名称：<input type="text" name="picName"/><br/>
        图片：<input type="file" name="picFile"/><br>
        <input type="submit" value="上传文件"/>
    </form>
    <br>
</body>
</html>
```

## SpringMVC 跨服务器方式的文件上传

在实际开发中，可能会有很多处理不同功能的服务器。例如：

* 应用服务器：负责部署应用
* 数据库服务器：运行数据库
* 缓存和消息服务器：负责处理大并发访问的缓存和消息
* 文件服务器：负责存储用户上传文件的服务器

分服务器处理的目的是让服务器各司其职，从而提高项目的运行效率。

准备两个 Tomcat 服务器，注意 HTTP Port 和 JMX Port 不能相同。

一个用作**文件服务器**，并创建一个用于存放文件的 web 工程。[文件服务器源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SpringMVCFileServer)，并修改文件服务器的 web.xml 配置，使其可以支持写入操作，搜索 DefaultServlet，添加以下代码：

```xml
<init-param>
    <param-name>readonly</param-name>
    <param-value>false</param-value>
</init-param>
```

另一个 Tomcat 服务器，编写以下代码

---

FileUploadController.java

```java
@Controller
@RequestMapping("/file")
public class FileUploadController {

    /**
     * SpringMVC 跨服务器方式的文件上传
     * @return
     */
    @RequestMapping(path = {"/upload3"})
    public String upload3( String picName, MultipartFile picFile) throws Exception {
        System.out.println("called upload3...");

        String path = "http://localhost:9090/file-server/uploads/";  // 定义上传文件服务器路径

        String fileName = picName + "-" + picFile.getOriginalFilename();
        fileName = UUID.randomUUID().toString() + "-" + fileName;

        // 1. 创建客户端对象
        Client client = Client.create();

        // 2. 和文件服务器进行连接
        WebResource resource = client.resource(path + fileName);

        // 3. 上传文件，跨服务器的
        resource.put(picFile.getBytes());

        return "success";
    }
}
```

---

index.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
    <h3>SpringMVC 跨服务器方式文件上传</h3>
    <form action="file/upload3" method="post" enctype="multipart/form-data">
        名称：<input type="text" name="picName"/><br/>
        图片：<input type="file" name="picFile"/><br>
        <input type="submit" value="上传文件"/>
    </form>
</body>
</html>
```

## 练习和总结
