package cn.parzulpan.config;

import cn.parzulpan.component.CustomFilter;
import cn.parzulpan.component.CustomListener;
import cn.parzulpan.component.CustomServlet;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义服务配置类
 */

@Configuration
public class CustomServerConfig {

    // 自定义嵌入式 Servlet 容器 添加到容器中
    @Bean
    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer(){
        return new EmbeddedServletContainerCustomizer() {
            // 定制嵌入式的Servlet容器相关的规则
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setPort(8083);
            }
        };
    }

    // 自定义 Servlet 添加到容器中
    @Bean
    public ServletRegistrationBean customServlet() {
        ServletRegistrationBean srb = new ServletRegistrationBean(new CustomServlet(), "/customServlet");
        srb.setLoadOnStartup(1);    // 可以设置各种属性
        return srb;
    }

    // 自定义 Filter 添加到容器中
    @Bean
    public FilterRegistrationBean customFilter() {
        FilterRegistrationBean frb = new FilterRegistrationBean();
        frb.setFilter(new CustomFilter());
        frb.setUrlPatterns(Arrays.asList("/hello", "/customServlet"));
        return frb;
    }

    // 自定义 Listener 添加到容器中
    @Bean
    public ServletListenerRegistrationBean customListener() {
        ServletListenerRegistrationBean<CustomListener> lrb = new ServletListenerRegistrationBean<>(new CustomListener());
        return lrb;
    }
}
