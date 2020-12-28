package cn.parzulpan.config;

import cn.parzulpan.component.CustomLocaleResolver;
import cn.parzulpan.component.LoginHandleInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义配置类，扩展 SpringMVC
 */

@Configuration
public class CustomMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送 /parzulpan 请求来到自定义 404 页面
        registry.addViewController("/parzulpan").setViewName("404");
    }

    // 将组件注册在容器，所有的 WebMvcConfigurerAdapter 组件都会一起起作用
    @Bean
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
        WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
            // 注册视图控制器
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                registry.addViewController("/main.html").setViewName("dashboard");
            }

            // 注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                // SpringBoot 已经做好了静态资源映射
                registry.addInterceptor(new LoginHandleInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/", "/index", "/index.html", "/user/login");
            }
        };
        return adapter;
    }

    // 自定义区域信息解析器添加到容器中
    @Bean
    public LocaleResolver localResolver() {
        return new CustomLocaleResolver();
    }
}
