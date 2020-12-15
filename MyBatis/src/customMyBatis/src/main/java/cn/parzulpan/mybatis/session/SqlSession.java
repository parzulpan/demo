package cn.parzulpan.mybatis.session;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSession 接口，操作数据库的核心对象
 */

public interface SqlSession {

    /**
     * 创建 DAO 接口的的代理对象
     * @param DAOInterfaceClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> DAOInterfaceClass);

    /**
     * 释放资源
     */
    void close();
}
