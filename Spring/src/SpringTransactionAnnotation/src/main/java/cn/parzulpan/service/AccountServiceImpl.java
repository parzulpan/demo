package cn.parzulpan.service;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口的实现类，事务控制应该在业务层
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Resource(name = "accountDAO")
    private AccountDAO accountDAO;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Account findById(Integer accountId) {
        return accountDAO.findById(accountId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
