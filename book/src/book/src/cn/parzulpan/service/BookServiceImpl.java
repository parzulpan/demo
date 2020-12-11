package cn.parzulpan.service;

import cn.parzulpan.bean.Book;
import cn.parzulpan.bean.Page;
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

    @Override
    public int addBook(Book book) {
        Connection connection = JDBCUtils.getConnection();
        int i = bookDAO.addBook(connection, book);
        JDBCUtils.close(connection, null, null);
        return i;
    }

    @Override
    public int deleteBookById(Integer id) {
        Connection connection = JDBCUtils.getConnection();
        int deleteBookById = bookDAO.deleteBookById(connection, id);
        JDBCUtils.close(connection, null, null);
        return deleteBookById;
    }

    @Override
    public int updateBook(Book book) {
        Connection connection = JDBCUtils.getConnection();
        int updateBook = bookDAO.updateBook(connection, book);
        JDBCUtils.close(connection, null, null);
        return updateBook;
    }

    @Override
    public Book queryBookById(Integer id) {
        Connection connection = JDBCUtils.getConnection();
        Book queryBookById = bookDAO.queryBookById(connection, id);
        JDBCUtils.close(connection, null, null);
        return queryBookById;
    }

    @Override
    public List<Book> queryBooks() {
        Connection connection = JDBCUtils.getConnection();
        List<Book> books = bookDAO.queryBooks(connection);
        JDBCUtils.close(connection, null, null);
        return books;
    }

    @Override
    public Page<Book> page(int pageNo, int pageSize) {
        Page<Book> page = new Page<>();
        Connection connection = JDBCUtils.getConnection();

//        page.setPageNo(pageNo); // 设置当前页码
        page.setPageSize(pageSize); // 设置每页显示的数量
        Integer pageTotalCount = bookDAO.queryForPageTotalCount(connection);
        page.setPageTotalCount(pageTotalCount); // 设置总记录数
        int pageTotal = pageTotalCount / pageSize;
        if (pageTotalCount % pageSize > 0) {
            pageTotal += 1;
        }
        page.setPageTotal(pageTotal);   // 设置总页码
        page.setPageNo(pageNo); // 设置当前页码
        int begin = (page.getPageNo() - 1) * pageSize;  // 求当前页数据的开始索引
        List<Book> items = bookDAO.queryForPageItems(connection, begin,pageSize);   // 求当前页数据
        page.setItems(items);

        JDBCUtils.close(connection, null, null);

        return page;
    }

    @Override
    public Page<Book> pageByPrice(int pageNo, int pageSize, int min, int max) {
        Page<Book> page = new Page<>();
        Connection connection = JDBCUtils.getConnection();

//        page.setPageNo(pageNo); // 设置当前页码
        page.setPageSize(pageSize); // 设置每页显示的数量
        Integer pageTotalCount = bookDAO.queryForPageTotalCount(connection, min, max);
        page.setPageTotalCount(pageTotalCount); // 设置总记录数
        int pageTotal = pageTotalCount / pageSize;
        if (pageTotalCount % pageSize > 0) {
            pageTotal += 1;
        }
        page.setPageTotal(pageTotal);   // 设置总页码
        page.setPageNo(pageNo); // 设置当前页码
        int begin = (page.getPageNo() - 1) * pageSize;  // 求当前页数据的开始索引
        List<Book> items = bookDAO.queryForPageItems(connection, begin, pageSize, min, max);   // 求当前页数据
        page.setItems(items);

        JDBCUtils.close(connection, null, null);

        return page;
    }
}
