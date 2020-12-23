<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>请求参数的绑定</title>
</head>
<body>
    <a href="params/stringAndIntegerParams?username=parzulpan啊哈哈&password=1024">基本类型和 String 类型作为参数</a>
    <br>
    <form action="params/javaBeanParams" method="post">
        账户名称：<input type="text" name="name" ><br/>
        账户金额：<input type="text" name="money" ><br/>
        账户省份：<input type="text" name="address.provinceName" ><br/>
        账户城市：<input type="text" name="address.cityName" ><br/>
        <input type="submit" value="保存">
    </form>
    <br>
    <form action="params/collectionParams" method="post">
        用户名称：<input type="text" name="username" ><br/>
        用户密码：<input type="password" name="password" ><br/>
        用户年龄：<input type="text" name="age" ><br/>
        账户 1 名称：<input type="text" name="accounts[0].name" ><br/>
        账户 1 金额：<input type="text" name="accounts[0].money" ><br/>
        账户 2 名称：<input type="text" name="accounts[1].name" ><br/>
        账户 2 金额：<input type="text" name="accounts[1].money" ><br/>
        账户 3 名称：<input type="text" name="accountMap['one'].name" ><br/>
        账户 3 金额：<input type="text" name="accountMap['one'].money" ><br/>
        账户 4 名称：<input type="text" name="accountMap['two'].name" ><br/>
        账户 4 金额：<input type="text" name="accountMap['two'].money" ><br/>
        <input type="submit" value="保存">
    </form>

</body>
</html>
