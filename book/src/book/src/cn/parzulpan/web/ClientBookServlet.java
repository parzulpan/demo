package cn.parzulpan.web;

import cn.parzulpan.bean.Book;
import cn.parzulpan.bean.Page;
import cn.parzulpan.service.BookService;
import cn.parzulpan.service.BookServiceImpl;
import cn.parzulpan.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-11
 * @Desc :
 */

@WebServlet(name = "ClientBookServlet", urlPatterns = ("/clientBookServlet"))
public class ClientBookServlet extends BaseServlet {
    private BookService bookService  = new BookServiceImpl();

    // 处理分页
    protected void page(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageNo = WebUtils.parseInt(request.getParameter("pageNo"), 1);
        int pageSize = WebUtils.parseInt(request.getParameter("pageSize"), Page.PAGE_SIZE);
        Page<Book> page = bookService.page(pageNo, pageSize);
        page.setUrl("clientBookServlet?action=page");
        request.setAttribute("page", page);
        request.getRequestDispatcher("/pages/client/index.jsp").forward(request, response);
    }

    // 处理价格区间分页
    protected void pageByPrice(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageNo = WebUtils.parseInt(request.getParameter("pageNo"), 1);
        int pageSize = WebUtils.parseInt(request.getParameter("pageSize"), Page.PAGE_SIZE);
        int min = WebUtils.parseInt(request.getParameter("min"), 0);
        int max = WebUtils.parseInt(request.getParameter("max"), 100);

        Page<Book> page = bookService.pageByPrice(pageNo, pageSize, min, max);
//        page.setUrl("clientBookServlet?action=pageByPrice");
        // 解决分页条中不带价格区间的 Bug
        StringBuilder sb= new StringBuilder("clientBookServlet?action=pageByPrice");
        // 如果有价格区间的参数，则添加到分页条的地址参数中
        if (request.getParameter("min") != null) {
            sb.append("&min=").append(request.getParameter("min"));
        }
        if (request.getParameter("max") != null) {
            sb.append("&max=").append(request.getParameter("max"));
        }
        page.setUrl(sb.toString());
        request.setAttribute("page", page);
        request.getRequestDispatcher("/pages/client/index.jsp").forward(request, response);
    }
}
