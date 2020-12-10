package cn.parzulpan.service;

import cn.parzulpan.bean.Book;
import cn.parzulpan.dao.BookDAO;
import cn.parzulpan.dao.BookDAOImpl;
import cn.parzulpan.utils.JDBCUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookServiceImpl implements BookService {
    private BookDAO bookDAO = new BookDAOImpl();

    /**
     * 增加一本书
     *
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int addBook(Book book) {
        Connection connection = JDBCUtils.getConnection();
        int i = bookDAO.addBook(connection, book);
        JDBCUtils.close(connection, null, null);
        return i;
    }

    /**
     * 根据 书的 id 删除一本书
     *
     * @param id 书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int deleteBookById(Integer id) {
        Connection connection = JDBCUtils.getConnection();
        int deleteBookById = bookDAO.deleteBookById(connection, id);
        JDBCUtils.close(connection, null, null);
        return deleteBookById;
    }

    /**
     * 更新一本书
     *
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int updateBook(Book book) {
        Connection connection = JDBCUtils.getConnection();
        int updateBook = bookDAO.updateBook(connection, book);
        JDBCUtils.close(connection, null, null);
        return updateBook;
    }

    /**
     * 根据 书的 id 查询一本书
     *
     * @param id 书的 id
     * @return Book Bean
     */
    @Override
    public Book queryBookById(Integer id) {
        Connection connection = JDBCUtils.getConnection();
        Book queryBookById = bookDAO.queryBookById(connection, id);
        JDBCUtils.close(connection, null, null);
        return queryBookById;
    }

    /**
     * 查询所有书
     *
     * @return Book Bean List
     */
    @Override
    public List<Book> queryBooks() {
        Connection connection = JDBCUtils.getConnection();
        List<Book> books = bookDAO.queryBooks(connection);
        JDBCUtils.close(connection, null, null);
        return books;
    }
}
