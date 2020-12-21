package cn.parzulpan.service;

import cn.parzulpan.dao.BankAccountDAO;
import cn.parzulpan.domain.BankAccount;
import cn.parzulpan.utils.ConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的业务层接口的实现类，使用 ConnectionUtil 事务控制，使用动态代理
 */

@Service("bankAccountService")
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private BankAccountDAO bankAccountDAO;
//    @Autowired
//    private ConnectionUtil connectionUtil;

    public List<BankAccount> findAll() {
//        使用事务管理
//        List<BankAccount> accounts = null;
//        try {
//            accounts =  bankAccountDAO.findAll();
//            connectionUtil.commitAndClose();
//        } catch (Exception e) {
//            connectionUtil.rollbackAndClose();
//            throw new RuntimeException(e);
//        }
//        return accounts;

        // 使用动态代理
        return bankAccountDAO.findAll();
    }

    public BankAccount findById(Integer id) {
//        BankAccount account = null;
//        try {
//            account = bankAccountDAO.findById(id);
//            connectionUtil.commitAndClose();
//        } catch (Exception e) {
//            connectionUtil.rollbackAndClose();
//            throw new RuntimeException(e);
//        }
//        return account;

        return bankAccountDAO.findById(id);
    }

    public void save(BankAccount bankAccount) {
//        try {
//            bankAccountDAO.save(bankAccount);
//            connectionUtil.commitAndClose();
//        } catch (Exception e) {
//            connectionUtil.rollbackAndClose();
//            throw new RuntimeException(e);
//        }

        bankAccountDAO.save(bankAccount);
    }

    public void update(BankAccount bankAccount) {
//        try {
//            bankAccountDAO.update(bankAccount);
//            connectionUtil.commitAndClose();
//        } catch (Exception e) {
//            connectionUtil.rollbackAndClose();
//            throw new RuntimeException(e);
//        }

        bankAccountDAO.update(bankAccount);
    }

    public void deleteById(Integer id) {
//        try {
//            bankAccountDAO.deleteById(id);
//            connectionUtil.commitAndClose();
//        } catch (Exception e) {
//            connectionUtil.rollbackAndClose();
//            throw new RuntimeException(e);
//        }

        bankAccountDAO.deleteById(id);
    }

    public void transfer(String sourceName, String targetName, Double money) {
//        try {
//            BankAccount source = bankAccountDAO.findByName(sourceName);
//            BankAccount target = bankAccountDAO.findByName(targetName);
//            source.setMoney(source.getMoney() - money);
//            target.setMoney(target.getMoney() + money);
//            bankAccountDAO.update(source);
//            int i = 1 / 0;  //  模拟转账异常
//            bankAccountDAO.update(target);
//            connectionUtil.commitAndClose();
//        } catch (Exception e) {
//            connectionUtil.rollbackAndClose();
//            throw new RuntimeException(e);
//        }

        BankAccount source = bankAccountDAO.findByName(sourceName);
        BankAccount target = bankAccountDAO.findByName(targetName);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        bankAccountDAO.update(source);
        int i = 1 / 0;  //  模拟转账异常
        bankAccountDAO.update(target);
    }
}
