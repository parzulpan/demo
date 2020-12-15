package cn.parzulpan.mybatis.session.impl;

import cn.parzulpan.mybatis.cfg.Configuration;
import cn.parzulpan.mybatis.session.SqlSession;
import cn.parzulpan.mybatis.session.SqlSessionFactory;
import cn.parzulpan.mybatis.utils.XMLConfigBuilder;

import java.io.InputStream;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSessionFactory 接口的实现类
 */

public class SqlSessionFactoryImpl implements SqlSessionFactory {
    private InputStream is = null;

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    /**
     * 创建一个新的 SqlSession 对象
     *
     * @return
     */
    @Override
    public SqlSession openSession() {
        SqlSessionImpl session = new SqlSessionImpl();
        Configuration cfg = XMLConfigBuilder.loadConfiguration(session, is);    // 调用工具类解析 xml 文件
        session.setCfg(cfg);
        return session;
    }
}
