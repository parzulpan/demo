package cn.parzulpan.web;

import cn.parzulpan.bean.Book;
import cn.parzulpan.bean.Cart;
import cn.parzulpan.bean.CartItem;
import cn.parzulpan.service.BookService;
import cn.parzulpan.service.BookServiceImpl;
import cn.parzulpan.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-13
 * @Desc :
 */

@WebServlet(name = "CartServlet", urlPatterns = ("/cartServlet"))
public class CartServlet extends BaseServlet {
    BookService bookService = new BookServiceImpl();


    // 加入购物车
    protected void addItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求的参数
        int id = WebUtils.parseInt(request.getParameter("id"), 0);
        // 得到图书信息
        Book book = bookService.queryBookById(id);
        // 把图书信息转换为 CartItem
        CartItem cartItem = new CartItem(book.getId(), book.getName(), 1, book.getPrice(), book.getPrice());
        // 调用 Cart.addItem(CartItem); 添加商品项
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            request.getSession().setAttribute("cart", cart);
        }
        cart.addItem(cartItem);

        // 购物车数据回显，最后一个添加的商品名称
        request.getSession().setAttribute("lastName", cartItem.getName());

        // 如果跳回添加商品的页面？
        // 在 HTTP 协议中有一个请求头，叫 Referer，它可以把请求发起时的浏览器地址栏的地址发送给服务器
        // 重定向回添加商品所在的页面
//        response.sendRedirect(request.getContextPath());
        response.sendRedirect(request.getHeader("Referer"));
    }

    // 删除购物车中商品
    protected void deleteItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求的参数
        int id = WebUtils.parseInt(request.getParameter("id"), 0);
        // 获得购物车对象
        Cart cart = (Cart)request.getSession().getAttribute("cart");

        if (cart != null) {
            cart.deleteItem(id);
            // 重定向回原来购物车展示页面
            response.sendRedirect(request.getHeader("Referer"));
        }
    }

    // 清空购物车
    protected void clear(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获得购物车对象
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        if (cart != null) {
            cart.clear();
            // 重定向回原来购物车展示页面
            response.sendRedirect(request.getHeader("Referer"));
        }
    }

    // 修改购物车中商品
    protected void updateCount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求的参数
        int id = WebUtils.parseInt(request.getParameter("id"), 0);
        int count = WebUtils.parseInt(request.getParameter("count"), 1);
        // 获得购物车对象
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        if (cart != null) {
            cart.updateCount(id, count);
            // 重定向回原来购物车展示页面
            response.sendRedirect(request.getHeader("Referer"));
        }
    }


}
