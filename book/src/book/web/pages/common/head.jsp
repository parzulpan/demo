<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/10
  Time: 12:03 上午
  To change this template use File | Settings | File Templates.

  抽取页面中相同的内容 head 中 css、jquery、base 标签
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme()
            + "://"
            + request.getServerName()
            + ":"
            + request.getServerPort()
            + request.getContextPath()
            + "/";
%>

<!--写 base 标签，永远固定相对路径跳转的结果-->
<base href="<%=basePath%>">
<link type="text/css" rel="stylesheet" href="static/css/style.css" >
<script type="text/javascript" src="static/script/jquery-1.7.2.js"></script>
