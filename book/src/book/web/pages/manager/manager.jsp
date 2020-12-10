<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>后台管理</title>

	<%--静态包含css、jquery、base 标签--%>
	<%@ include file="/pages/common/head.jsp"%>

	<style type="text/css">
		h1 {
			text-align: center;
			margin-top: 200px;
		}
	</style>
</head>
<body>
	
	<div id="header">
			<img class="logo_img" alt="" src="static/img/logo.png" width="70" height="70">
			<span class="wel_word">后台管理系统</span>

			<%--静态包含 manager 模块的菜单--%>
			<%@ include file="/pages/common/manager_menu.jsp"%>

	</div>
	
	<div id="main">
		<h1>欢迎管理员进入后台管理系统</h1>
	</div>


	<%--静态包含每个页面的页脚--%>
	<%@ include file="/pages/common/footer.jsp"%>

</body>
</html>