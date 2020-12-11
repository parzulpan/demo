package cn.parzulpan.service;

import cn.parzulpan.bean.Book;
import cn.parzulpan.bean.Page;
import cn.parzulpan.dao.BookDAOImpl;

import java.sql.Connection;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public interface BookService {

    /**
     * 增加一本书
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int addBook(Book book);

    /**
     * 根据 书的 id 删除一本书
     * @param id 书的 id
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int deleteBookById(Integer id);

    /**
     * 更新一本书
     * @param book Book Bean
     * @return 返回 -1 表示操作失败；否则返回 sql 语句影响的行数
     */
    public int updateBook(Book book);

    /**
     * 根据 书的 id 查询一本书
     * @param id 书的 id
     * @return Book Bean
     */
    public Book queryBookById(Integer id);

    /**
     * 查询所有书
     * @return Book Bean List
     */
    public List<Book> queryBooks();

    /**
     * 得到 page 对象
     * @param pageNo 起始
     * @param pageSize 当前页显示数量
     * @return page 对象
     */
    public Page<Book> page(int pageNo, int pageSize);

    /**
     * 得到 价格区间page 对象
     * @param pageNo 起始
     * @param pageSize 当前页显示数量
     * @return page 对象
     */
    public Page<Book> pageByPrice(int pageNo, int pageSize, int min, int max);
}
