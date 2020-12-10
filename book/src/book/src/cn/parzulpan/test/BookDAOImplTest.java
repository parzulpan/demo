package cn.parzulpan.test;

import cn.parzulpan.bean.Book;
import cn.parzulpan.dao.BookDAO;
import cn.parzulpan.dao.BookDAOImpl;
import cn.parzulpan.utils.JDBCUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;


/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookDAOImplTest {
    private BookDAO bookDAO = new BookDAOImpl();

    @Test
    public void addBook() {
        Connection connection = JDBCUtils.getConnection();
        int addBook = bookDAO.addBook(connection,
                new Book(null, "测试的书", "测试的作者",
                        new BigDecimal(120), 100, 10, null));
        System.out.println(addBook);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void deleteBookById() {
        Connection connection = JDBCUtils.getConnection();
        int deleteBookById = bookDAO.deleteBookById(connection, 20);
        System.out.println(deleteBookById);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void updateBook() {
        Connection connection = JDBCUtils.getConnection();
        int updateBook = bookDAO.updateBook(connection,
                new Book(2, "更新的书", "更新的作者",
                        new BigDecimal(120), 100, 10, null));
        System.out.println(updateBook);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void queryBookById() {
        Connection connection = JDBCUtils.getConnection();
        Book queryBookById = bookDAO.queryBookById(connection, 2);
        System.out.println(queryBookById);
        JDBCUtils.close(connection, null, null);
    }

    @Test
    public void queryBooks() {
        Connection connection = JDBCUtils.getConnection();
        List<Book> books = bookDAO.queryBooks(connection);
        books.forEach(System.out::println);
        JDBCUtils.close(connection, null, null);
    }
}