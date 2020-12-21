package cn.parzulpan.utils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 连接对象的工具类，它用于从数据中获取一个连接，并且实现和线程的绑定。
 */

@Component
public class ConnectionUtil {
    private ThreadLocal<Connection> conns = new ThreadLocal<Connection>();

    @Autowired
    private DataSource dataSource;

    /**
     * 获取一个连接
     * @return connection
     */
    public Connection getThreadConnection() {
        // 1. 从 ThreadLocal 中获取
        Connection connection = conns.get();
        // 2. 判断当前线程上是否有连接
        if (connection == null) {
            try {
                // 3. 从数据源中获取一个连接，并且存入 ThreadLocal
                connection = dataSource.getConnection();
                conns.set(connection);
                connection.setAutoCommit(false);    // 设置这个连接为手动管理事务
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 4. 返回当前线程上的连接
        return connection;
    }

    /**
     * 提交事务并关闭连接
     */
    public void commitAndClose() {
        Connection connection = conns.get();
        if (connection != null) {   // 如果不等于 null，说明之前使用过这个连接，操作过数据库
            try {
                connection.commit();    // 提交事务
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close(); // 关闭连接，资源资源
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        conns.remove(); // 对于用了线程池技术的，需要将连接与线程解绑
    }

    /**
     * 回滚事务并关闭连接
     */
    public void rollbackAndClose() {
        Connection connection = conns.get();
        if (connection != null) {   // 如果不等于 null，说明之前使用过这个连接，操作过数据库
            try {
                connection.rollback();    // 回滚事务
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close(); // 关闭连接，资源资源
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        conns.remove(); // 对于用了线程池技术的，需要将连接与线程解绑
    }
}
