package cn.parzulpan.web;

import cn.parzulpan.bean.User;
import cn.parzulpan.service.UserService;
import cn.parzulpan.service.UserServiceImpl;
import cn.parzulpan.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc : 合并 LoginServlet 和 RegistServlet 为 UserServlet 程序
 */

@WebServlet(name = "UserServlet", urlPatterns = ("/userServlet"))
public class UserServlet extends BaseServlet {  // 继承 BaseServlet 程序
    private UserService userService = new UserServiceImpl();

//    // 直接继承父类
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//
////        if ("login".equals(action)) {
////            login(request, response);
////        } else if ("regist".equals(action)) {
////            regist(request, response);
////        }
//
//        // 使用反射优化，省去大量的判断代码
//        try {
//            // 获取 action 业务鉴别字符串，获得相应的业务员方法反射对象
//            Method method = this.getClass().getDeclaredMethod(action, HttpServletRequest.class, HttpServletResponse.class);
//            method.invoke(this, request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//    }

    /**
     * 处理登录的功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//        User user = userService.login(new User(null, username, password, null));

        // 数据的封装和抽取 BeanUtils 的使用
        User userB = WebUtils.copyParamToBean(request.getParameterMap(), new User());
        User user = userService.login(userB);
        if (user == null) {
            // 登录失败
            System.out.println("登录失败！");
            // 把错误信息，回显的表单项信息，保存到 Request 域中
            request.setAttribute("msg", "用户名或密码错误！");
//            request.setAttribute("username", username);
            request.setAttribute("username", request.getParameterMap().get("username")[0]);
            request.getRequestDispatcher("/pages/user/login.jsp").forward(request, response);
        } else {
            System.out.println("登录成功！");
            // 保存用户登录的信息到 Session 域中
            request.getSession().setAttribute("user", userB);
            request.getRequestDispatcher("/pages/user/login_success.jsp").forward(request, response);
        }
    }

    // 处理登出
    protected void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath());
    }

    /**
     * 处理注册的功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // 获取请求的参数
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//        String email = request.getParameter("email");
//        String code = request.getParameter("code");

        // 数据的封装和抽取 BeanUtils 的使用
        User user = WebUtils.copyParamToBean(request.getParameterMap(), new User());
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String code = request.getParameterMap().get("code")[0];

        // 获取 Session 中的验证码
        String token = (String)request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        // 删除 Session 中的验证码
        request.getSession().removeAttribute(KAPTCHA_SESSION_KEY);

        // 由于目前还没有使用服务器生成验证码，写死要求验证码为 bnbnp
        // 使用谷歌生成的验证码
        if (token != null && token.equalsIgnoreCase(code)) {
            // 检查用户名是否可用
            if (userService.checkUsername(username)) {
                // 打印信息
                System.out.println("用户名 " + username + " 已存在！");

                // 把错误信息，回显的表单项信息，保存到 Request 域中
                request.setAttribute("msg", "用户名已存在！");
                request.setAttribute("username", username);
                request.setAttribute("email", email);

                // 跳回注册页面
                request.getRequestDispatcher("/pages/user/regist.jsp").forward(request, response);
            } else {
                // 保存到数据库
//                userService.regist(new User(null, username, password, email));
                userService.regist(user);
                // 跳到注册成功页面
                request.getRequestDispatcher("/pages/user/regist_success.jsp").forward(request, response);
            }
        } else {
            // 打印信息
            System.out.println("验证码 " + code + " 错误！");

            // 把错误信息，回显的表单项信息，保存到 Request 域中
            request.setAttribute("msg", "验证码错误！");
            request.setAttribute("username", username);
            request.setAttribute("email", email);

            // 跳回注册页面
            request.getRequestDispatcher("/pages/user/regist.jsp").forward(request, response);
        }
    }
}
