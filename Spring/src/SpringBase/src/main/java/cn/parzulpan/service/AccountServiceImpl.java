package cn.parzulpan.service;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.dao.AccountDAOImpl;
import cn.parzulpan.factory.BeanFactory;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户业务层接口的实现类
 */

public class AccountServiceImpl implements AccountService{
//    private AccountDAO accountDAO = new AccountDAOImpl();   // 这里发生了耦合

    /**
     * 模拟保存账户
     */
    public void saveAccount() {
        AccountDAO accountDAO = (AccountDAO) BeanFactory.getBean("accountDAO"); // 通过 Factory 解耦
        if (accountDAO != null) {
            accountDAO.saveAccount();
        }
    }
}
