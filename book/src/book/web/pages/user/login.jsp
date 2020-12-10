<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>购会员登录页面</title>

	<%--静态包含css、jquery、base 标签--%>
	<%@ include file="/pages/common/head.jsp"%>

</head>
<body>
		<div id="login_header">
			<img class="logo_img" alt="" src="static/img/logo.png"  width="70" height="70">
		</div>
		
			<div class="login_banner">
			
				<div id="l_content">
					<span class="login_word">欢迎登录</span>
				</div>
				
				<div id="content">
					<div class="login_form">
						<div class="login_box">
							<div class="tit">
								<h1>购会员</h1>
								<a href="pages/user/regist.jsp">立即注册</a>
							</div>
							<div class="msg_cont">
								<b></b>
								<span class="errorMsg">
<%--									输出回显信息--%>
<%--									<%=request.getAttribute("msg")==null?"请输入用户名和密码":request.getAttribute("msg")%>--%>
<%--									使用 EL 表达式 简化回显信息--%>
									${ empty requestScope.msg ? "请输入用户名和密码" : requestScope.msg}
								</span>
							</div>
							<div class="form">
<!--								修改注册表单的提交地址和请求方式-->
<!--								<form action="login_success.jsp">-->
<%--								<form action="loginServlet" method="post">--%>
<%--								添加隐藏域和修改请求地址--%>
								<form action="userServlet" method="post">
									<input type="hidden" name="action" value="login"/>
									<label>用户名称：</label>
									<input class="itxt" type="text" placeholder="请输入用户名"
										   autocomplete="off" tabindex="1" name="username"
<%--										   输出回显信息--%>
<%--										   value="<%=request.getAttribute("username")==null?"":request.getAttribute("username")%>"--%>
<%--										   使用 EL 表达式 简化回显信息--%>
										   value="${requestScope.username}"
									/>
									<br />
									<br />
									<label>用户密码：</label>
									<input class="itxt" type="password" placeholder="请输入密码"
										   autocomplete="off" tabindex="1" name="password" />
									<br />
									<br />
									<input type="submit" value="登录" id="sub_btn" />
								</form>
							</div>
							
						</div>
					</div>
				</div>
			</div>

		<%--静态包含每个页面的页脚--%>
		<%@ include file="/pages/common/footer.jsp"%>

</body>
</html>