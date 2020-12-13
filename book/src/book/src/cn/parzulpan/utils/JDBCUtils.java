package cn.parzulpan.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * @Author : parzulpan
 * @Time : 2020-12-08
 * @Desc : JDBCUtils 工具类
 */

public class JDBCUtils {
    private static DataSource dataSource = null;
    private static ThreadLocal<Connection> conns = new ThreadLocal<>();  // 保存从数据库连接池中获取的连接的连接对象

    static {
        try {
            Properties properties = new Properties();
//            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties");
            properties.load(is);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接，使用 Druid 数据库连接池
     * @return 数据库连接
     */
    public static Connection getConnection() {
        Connection connection = conns.get();
        if (connection == null) {
            try {
                connection = dataSource.getConnection();
                conns.set(connection);
                connection.setAutoCommit(false);    // 设置为手动管理事务
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    /**
     * 使用DbUtils，静默关闭数据库资源
     * @param connection 数据库连接
     * @param statement 声明
     * @param resultSet 结果集
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        DbUtils.closeQuietly(connection);
        DbUtils.closeQuietly(statement);
        DbUtils.closeQuietly(resultSet);
    }

    public static void commitAndClose() {
        Connection connection = conns.get();
        if (connection != null) {   // 如果不等于 null，说明 之前使用过连接，操作过数据库
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

        // 一定要执行 remove 操作，否则就会出错。（因为 Tomcat 服务器底层使用了线程池技术）
        conns.remove();
    }

    public static void rollbackAndClose() {
        Connection connection = conns.get();
        if (connection != null) {   // 如果不等于 null，说明 之前使用过连接，操作过数据库
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

        // 一定要执行 remove 操作，否则就会出错。（因为 Tomcat 服务器底层使用了线程池技术）
        conns.remove();
    }

    /**
     * 获取 SQL 格式的日期
     * @param dateStr 日期字符串
     * @return SQL 格式的日期
     */
    public static Date getSqlDate(String dateStr) {
        java.sql.Date date = null;
        try {
            date = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
