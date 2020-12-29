package cn.parzulpan.component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义 Listener
 */

public class CustomListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("CustomListener contextInitialized... ");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("CustomListener contextDestroyed... ");
    }
}
