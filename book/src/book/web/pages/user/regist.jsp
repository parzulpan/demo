<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>购会员注册页面</title>

	<%--静态包含css、jquery、base 标签--%>
	<%@ include file="/pages/common/head.jsp"%>

	<style type="text/css">
		.login_form{
			height:420px;
			margin-top: 25px;
		}
	</style>
	<script type="text/javascript">
		$(function() {
			$("#sub_btn").click(function () {
				// 验证用户名：必须由字母，数字下划线组成，并且长度为 5 到 12 位
				// 1. 获取用户名输入框的内容
				let username = $("#username").val();
				// 2. 创建正则表达式对象
				let usernamePattern = /^\w{5,12}$/;
				// 3. 使用 test 方法验证
				if (!usernamePattern.test(username)) {
					// 4. 提示用户
					$("span.errorMsg").text("用户名格式错误！");

					return false;
				}

				// 验证密码：必须由字母，数字下划线组成，并且长度为 5 到 12 位
				let password = $("#password").val();
				let passwordPattern = /^\w{5,12}$/;
				if (!passwordPattern.test(password)) {
					$("span.errorMsg").text("密码格式错误！");

					return false;
				}

				// 验证确认密码：和密码相同
				let repwd = $("#repwd").val();
				if (repwd !== password) {
					$("span.errorMsg").text("密码不一致！");

					return false;
				}

				// 邮箱验证：xxxxx@xxx.com
				let email = $("#email").val();
				let emailPattern = /^[a-z\d]+(\.[a-z\d]+)*@([\da-z](-[\da-z])?)+(\.{1,2}[a-z]+)+$/;
				if (!emailPattern.test(email)) {
					$("span.errorMsg").text("邮箱格式错误！");

					return false;
				}
				// 验证码：现在只需要验证用户已输入。因为还没讲到服务器。验证码生成。
				let code = $("#code").val();
				let trimCode = $.trim(code);
				if (trimCode == null || trimCode === "") {
					$("span.errorMsg").text("验证码不能为空！");

					return false;
				}

				// 验证成功，去掉提示信息
				$("span.errorMsg").text("");
			})
		})
	</script>

</head>
<body>
		<div id="login_header">
			<img class="logo_img" alt="" src="static/img/logo.png" width="70" height="70" >
		</div>
		
			<div class="login_banner">
			
				<div id="l_content">
					<span class="login_word">欢迎注册</span>
				</div>
				
				<div id="content">
					<div class="login_form">
						<div class="login_box">
							<div class="tit">
								<h1>注册购会员</h1>
								<span class="errorMsg">
<%--									输出回显信息--%>
<%--									<%=request.getAttribute("msg")==null?"":request.getAttribute("msg")%>--%>
<%--									使用 EL 表达式 简化回显信息--%>
									${requestScope.msg}
								</span>
							</div>
							<div class="form">
<!--								修改注册表单的提交地址和请求方式-->
<!--								<form action="regist_success.jsp">-->
<%--								<form action="registServlet" method="post">--%>
<%--								添加隐藏域和修改请求地址--%>
								<form action="userServlet" method="post">
									<input type="hidden" name="action" value="regist"/>
									<label>用户名称：</label>
									<label for="username"></label><input class="itxt" type="text" placeholder="请输入用户名"
<%--																		 输出回显信息--%>
<%--																		 value="<%=request.getAttribute("username")==null?"":request.getAttribute("username")%>"--%>
<%--																		 使用 EL 表达式 简化回显信息--%>
																		 value="${requestScope.username}"
																		 autocomplete="off" tabindex="1" name="username" id="username" />
									<br />
									<br />
									<label>用户密码：</label>
									<label for="password"></label><input class="itxt" type="password" placeholder="请输入密码"
																		 autocomplete="off" tabindex="1" name="password" id="password" />
									<br />
									<br />
									<label>确认密码：</label>
									<label for="repwd"></label><input class="itxt" type="password" placeholder="确认密码"
																	  autocomplete="off" tabindex="1" name="repwd" id="repwd" />
									<br />
									<br />
									<label>电子邮件：</label>
									<label for="email"></label><input class="itxt" type="text" placeholder="请输入邮箱地址"
<%--																	  输出回显信息--%>
<%--																	  value="<%=request.getAttribute("email")==null?"":request.getAttribute("email")%>"--%>
<%--																	  使用 EL 表达式 简化回显信息--%>
																	  value="${requestScope.email}"
																	  autocomplete="off" tabindex="1" name="email" id="email" />
									<br />
									<br />
									<label>验证码：</label>
									<label for="code"></label><input class="itxt" type="text" style="width: 150px;"
																	 name="code"  id="code"/>
									<img alt="" src="static/img/code.bmp" style="float: right; margin-right: 40px">
									<br />
									<br />
									<input type="submit" value="注册" id="sub_btn" />
									
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