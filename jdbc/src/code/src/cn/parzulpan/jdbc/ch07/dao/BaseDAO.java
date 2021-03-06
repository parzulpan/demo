package cn.parzulpan.jdbc.ch07.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-01
 * @Desc : 对数据表的通用操作
 */

public abstract class BaseDAO<T> {
    // 泛型的类型
    private Class<T> type;
    private QueryRunner queryRunner = new QueryRunner();

    // 获取 T 类对象，获取泛型的类型，泛型是在被子类继承时才确定的
    public BaseDAO() {
        // 获取子类的类型
        Class<? extends BaseDAO> clazz = this.getClass();

        // 获取父类的类型
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();

        // 获取具体的泛型
        Type[] types = parameterizedType.getActualTypeArguments();

        this.type = (Class<T>)types[0];
    }

    /**
     * 通用的增删改操作
     * @param connection 数据库连接
     * @param sql   sql 语句
     * @param args 参数
     * @return 更新的条数
     */
    public int update(Connection connection, String sql, Object ... args) {
        int update = 0;
        try {
            update = queryRunner.update(connection, sql, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return update;
    }

    /**
     * 通用的查询操作
     * @param connection 数据库连接
     * @param sql   sql 语句
     * @param args 参数
     * @return 返回一个对象
     */
    public T getBean(Connection connection, String sql, Object ... args) {
        T t = null;
        BeanHandler<T> beanHandler = new BeanHandler<>(type);
        try {
            t = queryRunner.query(connection, sql, beanHandler, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 通用的查询操作
     * @param connection 数据库连接
     * @param sql   sql 语句
     * @param args 参数
     * @return 返回一个对象列表
     */
    public List<T> getBeanList(Connection connection, String sql, Object ... args) {
        List<T> list = null;
        BeanListHandler<T> beanListHandler = new BeanListHandler<>(type);
        try {
            list = queryRunner.query(connection, sql, beanListHandler, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询特殊值，类似于最大的，最小的，平均的，总和，个数相关的操作
     * @param connection 数据库连接
     * @param sql   sql 语句
     * @param args 参数
     * @return 特殊值
     */
    public Object getValue(Connection connection, String sql, Object ... args) {
        Object obj = null;
        ScalarHandler scalarHandler = new ScalarHandler();
        try {
            obj = queryRunner.query(connection, sql, scalarHandler, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
