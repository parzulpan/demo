package cn.parzulpan.dao;

import cn.parzulpan.bean.Book;
import cn.parzulpan.utils.JDBCUtils;

import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookDAOImpl extends BaseDAO<Book> implements BookDAO {
    @Override
    public int addBook(Connection connection, Book book) {
        String sql = "insert into t_book(`name`, `author`, `price`, `sales`, `stock`, `imgPath`) values (?, ?, ?, ?, ?, ?)";
        return update(connection, sql, book.getName(), book.getAuthor(), book.getPrice(), book.getSales(), book.getStock(), book.getImgPath());
    }

    @Override
    public int deleteBookById(Connection connection, Integer id) {
        String sql = "delete from t_book where id = ?";
        return update(connection, sql, id);
    }

    @Override
    public int updateBook(Connection connection, Book book) {
        String sql = "update t_book set `name` = ?, `author` = ?, `price` = ?, `sales` = ?, `stock` = ?, `imgPath` = ? where id = ?";
        return update(connection, sql, book.getName(), book.getAuthor(), book.getPrice(), book.getSales(),
                book.getStock(), book.getImgPath(), book.getId());
    }

    @Override
    public Book queryBookById(Connection connection, Integer id) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book where id = ?";
        return getBean(connection, sql, id);
    }

    @Override
    public List<Book> queryBooks(Connection connection) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book";
        return getBeanList(connection, sql);
    }

    @Override
    public Integer queryForPageTotalCount(Connection connection) {
        String sql = "select count(*) from t_book";
        Number count = (Number) getValue(connection, sql);
        return count.intValue();
    }

    @Override
    public List<Book> queryForPageItems(Connection connection, int begin, int pageSize) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book limit ?, ?";
        return getBeanList(connection, sql, begin, pageSize);
    }

    @Override
    public Integer queryForPageTotalCount(Connection connection, int min, int max) {
        String sql = "select count(*) from t_book where price between ? and ?";
        Number count = (Number) getValue(connection, sql, min, max);
        return count.intValue();
    }

    @Override
    public List<Book> queryForPageItems(Connection connection, int begin, int pageSize, int min, int max) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book where price between ? and ? order by price limit ?, ?";
        return getBeanList(connection, sql, min, max, begin, pageSize);
    }
}
