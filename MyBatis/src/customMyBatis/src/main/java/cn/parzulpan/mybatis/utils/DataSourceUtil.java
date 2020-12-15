package cn.parzulpan.mybatis.utils;

import cn.parzulpan.mybatis.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于创建数据源的工具类
 */

public class DataSourceUtil {

    public static Connection getConnection(Configuration cfg) {
        // JDBC 那一套，加载驱动，获取连接
        // 也可以使用 数据库连接池那一套，参考：https://www.cnblogs.com/parzulpan/p/14130004.html JDBCUtils 类
        try {
            Class.forName(cfg.getDriver());
            return DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(), cfg.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
