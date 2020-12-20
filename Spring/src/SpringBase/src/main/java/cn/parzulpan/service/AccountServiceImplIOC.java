package cn.parzulpan.service;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.factory.BeanFactory;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户业务层接口的实现类，使用IOC
 */

public class AccountServiceImplIOC implements AccountService{
//    private AccountDAO accountDAO = new AccountDAOImpl();   // 这里发生了耦合

    /**
     * 模拟保存账户
     */
    public void saveAccount() {
//        AccountDAO accountDAO = (AccountDAO) BeanFactory.getBean("accountDAO"); // 通过 Factory 解耦
//        if (accountDAO != null) {
//            accountDAO.saveAccount();
//        }
        System.out.println("call saveAccount()");
    }

    public void init() {
        System.out.println("AccountService 对象初始化...");
    }

    public void destroy() {
        System.out.println("AccountService 对象销毁...");
    }
}
