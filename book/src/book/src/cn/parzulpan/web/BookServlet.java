package cn.parzulpan.web;

import cn.parzulpan.bean.Book;
import cn.parzulpan.bean.Page;
import cn.parzulpan.service.BookService;
import cn.parzulpan.service.BookServiceImpl;
import cn.parzulpan.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc :
 */

@WebServlet(name = "BookServlet", urlPatterns = ("/bookServlet"))
public class BookServlet extends BaseServlet {
    private BookService bookService  = new BookServiceImpl();

    // 查询全部图书
    protected void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Book> books = bookService.queryBooks();
        request.setAttribute("books", books);
        // 请求转发，这里不能用请求重定向，想想为什么？
        request.getRequestDispatcher("/pages/manager/book_manager.jsp").forward(request, response);
    }

    // 添加图书
    protected void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 中文编码问题，待解决！
        Book book = WebUtils.copyParamToBean(request.getParameterMap(), new Book());
        bookService.addBook(book);
        // 请求重定向，这里不能用请求转发，想想为什么？
//        response.sendRedirect("bookServlet?action=list");
        response.sendRedirect("bookServlet?action=page&pageNo=" + request.getParameter("pageNo"));
    }

    // 删除图书
    protected void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = WebUtils.parseInt(request.getParameter("id"), 0);
        bookService.deleteBookById(id);
//        response.sendRedirect("bookServlet?action=list");
        response.sendRedirect("bookServlet?action=page&pageNo=" + request.getParameter("pageNo"));
    }

    // 查询图书
    protected void getBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = WebUtils.parseInt(request.getParameter("id"), 0);
        Book book = bookService.queryBookById(id);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/pages/manager/book_edit.jsp").forward(request, response);
    }

    // 更新图书
    protected void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Book book = WebUtils.copyParamToBean(request.getParameterMap(), new Book());
        bookService.updateBook(book);
//        response.sendRedirect("bookServlet?action=list");
        response.sendRedirect("bookServlet?action=page&pageNo=" + request.getParameter("pageNo"));
    }

    // 处理分页
    protected void page(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageNo = WebUtils.parseInt(request.getParameter("pageNo"), 1);
        int pageSize = WebUtils.parseInt(request.getParameter("pageSize"), Page.PAGE_SIZE);
        Page<Book> page = bookService.page(pageNo, pageSize);
        page.setUrl("bookServlet?action=page");
        request.setAttribute("page", page);
        request.getRequestDispatcher("/pages/manager/book_manager.jsp").forward(request, response);
    }
}
