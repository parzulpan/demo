<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>图书管理</title>

	<%--静态包含css、jquery、base 标签--%>
	<%@ include file="/pages/common/head.jsp"%>

	<script type="text/javascript">
		// 给删除添加确认提示操作
		$(function () {
			$("a.deleteClass").click(function () {
				return confirm("您确定要删除【" + $(this).parent().parent().find("td:first").text() + "】吗？")
			});
		});
	</script>

</head>
<body>
	
	<div id="header">
			<img class="logo_img" alt="" src="static/img/logo.png" width="70" height="70" >
			<span class="wel_word">图书管理系统</span>

			<%--静态包含 manager 模块的菜单--%>
			<%@ include file="/pages/common/manager_menu.jsp"%>

	</div>
	
	<div id="main">
		<table>
			<tr>
				<td>名称</td>
				<td>价格</td>
				<td>作者</td>
				<td>销量</td>
				<td>库存</td>
				<td colspan="2">操作</td>
			</tr>		

<%--			使用 JSTL 标签库遍历输出--%>
<%--			<c:forEach items="${requestScope.books}" var="book">--%>
			<c:forEach items="${requestScope.page.items}" var="book">
				<tr>
					<td>${book.name}</td>
					<td>${book.price}</td>
					<td>${book.author}</td>
					<td>${book.sales}</td>
					<td>${book.stock}</td>
<%--					<td><a href="pages/manager/book_edit.jsp">修改</a></td>--%>
<%--					<td><a href="bookServlet?action=getBook&id=${book.id}">修改</a></td>--%>
					<td><a href="bookServlet?action=getBook&id=${book.id}&pageNo=${requestScope.page.pageNo}">修改</a></td>
					<td><a class="deleteClass" href="bookServlet?action=delete&id=${book.id}&pageNo=${requestScope.page.pageNo}">删除</a></td>
				</tr>
			</c:forEach>
			
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td><a href="pages/manager/book_edit.jsp">添加图书</a></td>
			</tr>	
		</table>

		<%--静态包含分页条--%>
		<%@include file="/pages/common/page_nav.jsp"%>

	</div>

	<%--静态包含每个页面的页脚--%>
	<%@ include file="/pages/common/footer.jsp"%>

</body>
</html>