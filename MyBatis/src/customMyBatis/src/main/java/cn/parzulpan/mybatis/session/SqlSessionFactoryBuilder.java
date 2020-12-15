package cn.parzulpan.mybatis.session;

import cn.parzulpan.mybatis.session.impl.SqlSessionFactoryImpl;

import java.io.InputStream;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于 SqlSessionFactory 的创建
 */

public class SqlSessionFactoryBuilder {

    /**
     * 根据传入的流，实现对 SqlSessionFactory 的创建
     * @param is
     * @return
     */
    public SqlSessionFactory build(InputStream is) {
        SqlSessionFactoryImpl factory = new SqlSessionFactoryImpl();
        factory.setIs(is);  // //给 factory 中 is 赋值
        return factory;
    }
}
