<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>编辑图书</title>

	<%--静态包含css、jquery、base 标签--%>
	<%@ include file="/pages/common/head.jsp"%>

	<style type="text/css">
		h1 {
			text-align: center;
			margin-top: 200px;
		}

		h1 a {
			color:red;
		}

		input {
			text-align: center;
		}
	</style>
</head>
<body>
		<div id="header">
			<img class="logo_img" alt="" src="static/img/logo.png"  width="70" height="70">
			<span class="wel_word">编辑图书</span>

			<%--静态包含 manager 模块的菜单--%>
			<%@ include file="/pages/common/manager_menu.jsp"%>

		</div>
		
		<div id="main">
<%--			<form action="book_manager.jsp">--%>
				<form action="bookServlet" method="get">
<%--					<input type="hidden" name="action" value="add"/>--%>
<%--					解决 book_edit.jsp 页面，即要实现添加，又要实现修改操作--%>
					<input type="hidden" name="action" value="${ empty param.id ? "add" : "update" }"/>
					<input type="hidden" name="id" value="${ requestScope.book.id }"/>
					<input type="hidden" name="pageNo" value="${param.pageNo}"/>

				<table>
					<tr>
						<td>名称</td>
						<td>价格</td>
						<td>作者</td>
						<td>销量</td>
						<td>库存</td>
						<td colspan="2">操作</td>
					</tr>		
					<tr>
<%--						<td><input name="name" type="text" value="时间简史"/></td>--%>
<%--						<td><input name="price" type="text" value="30.00"/></td>--%>
<%--						<td><input name="author" type="text" value="霍金"/></td>--%>
<%--						<td><input name="sales" type="text" value="200"/></td>--%>
<%--						<td><input name="stock" type="text" value="300"/></td>--%>
						<td><input name="name" type="text" value="${requestScope.book.name}"/></td>
						<td><input name="price" type="text" value="${requestScope.book.price}"/></td>
						<td><input name="author" type="text" value="${requestScope.book.author}"/></td>
						<td><input name="sales" type="text" value="${requestScope.book.sales}"/></td>
						<td><input name="stock" type="text" value="${requestScope.book.stock}"/></td>
						<td><input type="submit" value="提交"/></td>
					</tr>	
				</table>
			</form>
			
	
		</div>

		<%--静态包含每个页面的页脚--%>
		<%@ include file="/pages/common/footer.jsp"%>

</body>
</html>