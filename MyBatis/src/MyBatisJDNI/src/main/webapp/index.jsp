<%@ page import="java.io.InputStream" %>
<%@ page import="org.apache.ibatis.io.Resources" %>
<%@ page import="org.apache.ibatis.session.SqlSessionFactoryBuilder" %>
<%@ page import="org.apache.ibatis.session.SqlSessionFactory" %>
<%@ page import="org.apache.ibatis.session.SqlSession" %>
<%@ page import="cn.parzulpan.dao.UserDAO" %>
<%@ page import="cn.parzulpan.domain.User" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<body>
<h2>Hello World!</h2>
<%
    // 1. 读取配置文件
    InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
    // 2. 创建 SqlSessionFactory 的构建者对象
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    // 3. 使用构建者创建工厂对象 SqlSessionFactory
    SqlSessionFactory factory = builder.build(is);
    // 4. 使用 SqlSessionFactory 生产 SqlSession 对象
    SqlSession sqlSession = factory.openSession();
    // 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
    UserDAO userDAO = sqlSession.getMapper(UserDAO.class);
    // 6. 使用代理对象执行方法
    List<User> users = userDAO.findAll();
    for (User user : users) {
        System.out.println(user);
    }
    // 7. 释放资源
    sqlSession.close();
    is.close();
%>
</body>
</html>
