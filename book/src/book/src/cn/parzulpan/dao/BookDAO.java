package cn.parzulpan.dao;

import cn.parzulpan.bean.Book;

import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc : 用于规范 Book 表的常用操作
 */

public interface BookDAO {

    /**
     * 增加一本书
     * @param connection 数据库连接
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int addBook(Connection connection, Book book);

    /**
     * 根据 书的 id 删除一本书
     * @param connection 数据库连接
     * @param id 书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int deleteBookById(Connection connection, Integer id);

    /**
     * 更新一本书
     * @param connection 数据库连接
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int updateBook(Connection connection, Book book);

    /**
     * 根据 书的 id 查询一本书
     * @param connection 数据库连接
     * @param id 书的 id
     * @return Book Bean
     */
    public Book queryBookById(Connection connection, Integer id);

    /**
     * 查询所有书
     * @param connection 数据库连接
     * @return Book Bean List
     */
    public List<Book> queryBooks(Connection connection);

    /**
     * 求总记录数
     * @param connection 数据库连接
     * @return 总记录数
     */
    public Integer queryForPageTotalCount(Connection connection);

    /**
     * 求当前页数据
     * @param connection 数据库连接
     * @param begin 起始
     * @param pageSize 当前页显示数量
     * @return 当前页数据
     */
    public List<Book> queryForPageItems(Connection connection, int begin, int pageSize);

    /**
     * 求价格区间的总记录数
     * @param connection 数据库连接
     * @return 总记录数
     */
    public Integer queryForPageTotalCount(Connection connection, int min, int max);

    /**
     * 求价格区间的当前页数据
     * @param connection 数据库连接
     * @param begin 起始
     * @param pageSize 当前页显示数量
     * @return 当前页数据
     */
    public List<Book> queryForPageItems(Connection connection, int begin, int pageSize, int min, int max);
}
