package cn.parzulpan.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 自定义安全配置类
 */

@EnableWebSecurity  // 开启 WebSecurity 模式
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

    // 定制请求的授权规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll()
                .antMatchers("/level1/**").hasRole("VIP1")
                .antMatchers("/level2/**").hasRole("VIP2")
                .antMatchers("/level3/**").hasRole("VIP3");

        // 开启自动配置的登录功能，如果没有登录或者没有权限就会来到登录页面
        // 1. 访问 /login 来到登录页
        // 2. 重定向到 login?error 表示登录失败
        // 3. ...
        // http.formLogin();

        // 指定自定义的登录页
        // 1. 默认 post 形式，/login 代表处理登录
        // 2. 一旦定制 loginPage，那么 loginPage 的 post 请求就是登录
        http.formLogin().usernameParameter("user").passwordParameter("pwd")
                .loginPage("/userlogin");

        // 关闭 CSRF（Cross-site request forgery）跨站请求伪造
        // http.csrf().disable();

        // 开启自动配置的注销功能
        // 1. 访问 /logout 表示用户注销，清空 session
        // 2. 注销成功，会自动重定向到 login?logout
        // 3. logoutSuccessUrl 注销成功，自动回到首页
        http.logout().logoutSuccessUrl("/");

        // 开启记住我功能
        // 1. 登录成功以后，将 cookie 发给浏览器保存，以后访问页面会带上这个 cookie，只要通过检查就可以免登录
        // 2. 点击注销就会删除 cookie
        http.rememberMe().rememberMeParameter("remember");

    }

    // 定制认证规则
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.inMemoryAuthentication()
               .withUser("User01").password("123456").roles("VIP1", "VIP2")
               .and()
               .withUser("User02").password("123456").roles("VIP2", "VIP3")
               .and()
               .withUser("User03").password("123456").roles("VIP3", "VIP1");
    }
}
