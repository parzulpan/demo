package cn.parzulpan.mybatis.session.impl;

import cn.parzulpan.mybatis.cfg.Configuration;
import cn.parzulpan.mybatis.session.SqlSession;
import cn.parzulpan.mybatis.session.handler.MapperInvocationHandler;
import cn.parzulpan.mybatis.utils.DataSourceUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSession 接口的实现类
 */

public class SqlSessionImpl implements SqlSession {
    private Configuration cfg;  // 核心配置对象
    private Connection connection;  // 连接对象

    public Configuration getCfg() {
        return cfg;
    }

    public void setCfg(Configuration cfg) {
        this.cfg = cfg;
        this.connection = DataSourceUtil.getConnection(this.cfg);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 创建 DAO 接口的的代理对象
     *
     * @param DAOInterfaceClass DAO 接口的字节码
     * @return
     */
    @Override
    public <T> T getMapper(Class<T> DAOInterfaceClass) {

//        Proxy.newProxyInstance(DAOInterfaceClass.getClassLoader(),
//                DAOInterfaceClass.getInterfaces(),
//                new MapperInvocationHandler(cfg.getMappers(), connection));

        T DAOProxy = (T)Proxy.newProxyInstance(DAOInterfaceClass.getClassLoader(),
                new Class[]{DAOInterfaceClass},
                new MapperInvocationHandler(cfg.getMappers(), connection));

        return DAOProxy;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
