package cn.parzulpan.mybatis.session;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSessionFactory 接口
 */

public interface SqlSessionFactory {

    /**
     * 创建一个新的 SqlSession 对象
     * @return
     */
    SqlSession openSession();
}
