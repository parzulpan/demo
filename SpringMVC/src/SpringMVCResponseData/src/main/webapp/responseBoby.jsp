<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>ResponseBody响应json数据</title>
    <script type="text/javascript" src="static/js/jquery.js"></script>
<%--    <script src="${ctx}/webjars/jquery/3.5.1/jquery.min.js"></script>--%>
    <script>
        $(function () {
            $("#btn").click( function () {
                // alert("hello");

                $.ajax({
                    // 编写 JSON 格式，设置属性和值
                    url: "user/testAjax",
                    contentType: "application/json;charset=UTF-8",
                    data: '{"username": "parzulpan潘", "password": "parzulpan0101"}',
                    dataType: "json",
                    type: "post",
                    success: function (data) {
                        // 服务器端响应的 JSON 数据
                        alert(data);
                        alert(data.password);
                    }
                });
            });
        });
    </script>
</head>
<body>

    <button id="btn">发送 AJAX 请求</button>

</body>
</html>
