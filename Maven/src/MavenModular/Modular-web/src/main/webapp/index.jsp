<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>欢迎</title>
</head>
<body>
    欢迎使用～
    <br>
    <h2>用户登录</h2>
    <form action="user/login" method="post">
        用户名：<input type="text" name="username"/><br>
        密  码：<input type="password" name="password"><br>
        <input type="submit" value="登录">
    </form>
    <br>
    <h2>用户注册</h2>
    <form action="user/registration" method="post">
        用户名：<input type="text" name="username"/><br>
        密  码：<input type="password" name="password"><br>
        <input type="submit" value="注册">
    </form>
</body>
</html>
