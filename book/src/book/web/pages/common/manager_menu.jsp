<%--
  Created by IntelliJ IDEA.
  User: parzulpan
  Date: 2020/12/10
  Time: 12:12 上午
  To change this template use File | Settings | File Templates.
  抽取页面中相同的内容 manager 模块的菜单
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
<%--    <a href="pages/manager/book_manager.jsp">图书管理</a>--%>

<%--    修改图书管理 请求地址--%>
<%--    <a href="bookServlet?action=list">图书管理</a>--%>

<%--    不用 list，用上分页--%>
    <a href="bookServlet?action=page">图书管理</a>

    <a href="pages/manager/order_manager.jsp">订单管理</a>
    <a href="index.jsp">返回商城</a>
</div>
