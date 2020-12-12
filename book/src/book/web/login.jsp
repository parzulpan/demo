<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/12
  Time: 5:47 下午
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>免输入用户名登录</title>
</head>
<body>

    <form action="cookieServlet" method="get">
        <input type="hidden" name="action" value="loginWithCookie"/>
        用户名：<input type="text" name="username" value="${cookie.username.value}"> <br>
        密码：<input type="password" name="password"> <br>
        <input type="submit" value="登录">
    </form>

</body>
</html>
