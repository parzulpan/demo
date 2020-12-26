<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>成功</title>
</head>
<body>

    操作成功啦～<br>

    <h3>所有账户</h3><br>
    <c:forEach items="${users}" var="user">
        ID: ${user.id} <br>
        用户名：${user.username} <br>
        密  码：${user.password} <br>
    </c:forEach>

    <br>
    <a href="returnIndex">返回起始页</a>

</body>
</html>
