package cn.parzulpan.service;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.domain.Account;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口的实现类，事务控制应该在业务层
 */

public class AccountServiceImpl implements AccountService {
    private AccountDAO accountDAO;

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account findById(Integer accountId) {
        return accountDAO.findById(accountId);
    }

    public void transfer(String sourceName, String targetName, Double money) {
        System.out.println("开始进行转账...");

        Account source = accountDAO.findByName(sourceName);
        Account target = accountDAO.findByName(targetName);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        accountDAO.update(source);
        int i = 1 / 0;  // 模拟转账故障
        accountDAO.update(target);

        System.out.println("转账完成...");
    }
}
