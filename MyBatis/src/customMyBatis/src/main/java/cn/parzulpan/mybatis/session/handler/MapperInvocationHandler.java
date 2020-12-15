package cn.parzulpan.mybatis.session.handler;

import cn.parzulpan.mybatis.cfg.Mapper;
import cn.parzulpan.mybatis.utils.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于创建代理对象是增强方法
 */

public class MapperInvocationHandler implements InvocationHandler {
    private Map<String, Mapper> mappers;    //  key 包含实体类全限定类名和方法名
    private Connection connection;

    public MapperInvocationHandler(Map<String, Mapper> mappers, Connection connection) {
        this.mappers = mappers;
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 获取方法名
        String methodName = method.getName();
        // 2. 获取方法所在类名
        String className = method.getDeclaringClass().getName();
        // 3. 组合 key
        String key = className + "." + methodName;
        // 4. 获取 mappers 中的 Mapper 对象
        Mapper mapper = mappers.get(key);
        // 5. 判断是否有 mapper
        if (mapper == null) {
            throw new IllegalArgumentException("传入的参数有误，无法获取执行的必要条件。");
        }
        // 6. 创建 Executor 对象，负责执行 SQL 语句，并且封装结果集
        return new Executor().selectList(mapper, connection);
    }
}
