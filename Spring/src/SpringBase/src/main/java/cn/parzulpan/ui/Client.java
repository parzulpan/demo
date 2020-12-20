package cn.parzulpan.ui;

import cn.parzulpan.factory.BeanFactory;
import cn.parzulpan.service.AccountService;
import cn.parzulpan.service.AccountServiceImpl;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 模拟一个表现层，用于调用业务层，实际开发中应该是一个 Servlet 等
 */

public class Client {
    public static void main(String[] args) {
//        AccountServiceImpl accountService = new AccountServiceImpl();   // 这里发生了耦合

        AccountService accountService = (AccountService) BeanFactory.getBean("accountService"); // 通过 Factory 解耦

        if (accountService != null) {
            accountService.saveAccount();
        }

        // 通过 Factory 解耦存在的问题
        for (int i = 0; i < 5; ++i) {
            System.out.println(BeanFactory.getBean("accountService"));  // 对象被创建多次
        }
    }
}
