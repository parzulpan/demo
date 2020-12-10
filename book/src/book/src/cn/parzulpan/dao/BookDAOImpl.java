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
    /**
     * 增加一本书
     *
     * @param connection 数据库连接
     * @param book       Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int addBook(Connection connection, Book book) {
        String sql = "insert into t_book(`name`, `author`, `price`, `sales`, `stock`, `imgPath`) values (?, ?, ?, ?, ?, ?)";
        return update(connection, sql, book.getName(), book.getAuthor(), book.getPrice(), book.getSales(), book.getStock(), book.getImgPath());
    }

    /**
     * 根据 书的 id 删除一本书
     *
     * @param connection 数据库连接
     * @param id         书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int deleteBookById(Connection connection, Integer id) {
        String sql = "delete from t_book where id = ?";
        return update(connection, sql, id);
    }

    /**
     * 更新一本书
     *
     * @param connection 数据库连接
     * @param book       Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    @Override
    public int updateBook(Connection connection, Book book) {
        String sql = "update t_book set `name` = ?, `author` = ?, `price` = ?, `sales` = ?, `stock` = ?, `imgPath` = ? where id = ?";
        return update(connection, sql, book.getName(), book.getAuthor(), book.getPrice(), book.getSales(),
                book.getStock(), book.getImgPath(), book.getId());
    }

    /**
     * 根据 书的 id 查询一本书
     *
     * @param connection 数据库连接
     * @param id         书的 id
     * @return Book Bean
     */
    @Override
    public Book queryBookById(Connection connection, Integer id) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book where id = ?";
        return getBean(connection, sql, id);
    }

    /**
     * 查询所有书
     *
     * @param connection 数据库连接
     * @return Book Bean List
     */
    @Override
    public List<Book> queryBooks(Connection connection) {
        String sql = "select `id`, `name`, `author`, `price`, `sales`, `stock`, `imgPath` from t_book";
        return getBeanList(connection, sql);
    }
}
