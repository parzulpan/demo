package cn.parzulpan.test;

import cn.parzulpan.bean.Book;
import cn.parzulpan.service.BookService;
import cn.parzulpan.service.BookServiceImpl;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

public class BookServiceImplTest {
    private BookService bookService = new BookServiceImpl();

    @Test
    public void addBook() {
        System.out.println(bookService.addBook(new Book(null, "测试的书", "测试的作者",
                new BigDecimal(120), 100, 10, null)));
    }

    @Test
    public void deleteBookById() {
        System.out.println(bookService.deleteBookById(22));
    }

    @Test
    public void updateBook() {
        System.out.println(bookService.updateBook(new Book(2, "更新的书", "更新的作者",
                new BigDecimal(120), 100, 10, null)));
    }

    @Test
    public void queryBookById() {
        System.out.println(bookService.queryBookById(22));
    }

    @Test
    public void queryBooks() {
        List<Book> books = bookService.queryBooks();
        books.forEach(System.out::println);
    }
}