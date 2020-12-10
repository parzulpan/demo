<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>购会员注册页面</title>

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
</style>
</head>
<body>
		<div id="header">
				<img class="logo_img" alt="" src="static/img/logo.png" width="70" height="70" >

				<%--静态包含 登录成功后的菜单--%>
				<%@ include file="/pages/common/login_success_menu.jsp"%>

		</div>
		
		<div id="main">
		
			<h1>欢迎回来 <a href="index.jsp">转到主页</a></h1>
	
		</div>


		<%--静态包含每个页面的页脚--%>
		<%@ include file="/pages/common/footer.jsp"%>

</body>
</html>