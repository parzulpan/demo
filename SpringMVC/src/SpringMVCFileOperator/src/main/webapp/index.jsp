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
    <br>
    <h3>SpringMVC 传统方式文件上传</h3>
    <form action="file/upload2" method="post" enctype="multipart/form-data">
        名称：<input type="text" name="picName"/><br/>
        图片：<input type="file" name="picFile"/><br>
        <input type="submit" value="上传文件"/>
    </form>
    <br>
    <h3>SpringMVC 跨服务器方式文件上传</h3>
    <form action="file/upload3" method="post" enctype="multipart/form-data">
        名称：<input type="text" name="picName"/><br/>
        图片：<input type="file" name="picFile"/><br>
        <input type="submit" value="上传文件"/>
    </form>
</body>
</html>
