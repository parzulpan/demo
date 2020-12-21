package cn.parzulpan.service;

import cn.parzulpan.dao.BankAccountDAO;
import cn.parzulpan.domain.BankAccount;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的业务层接口的实现类
 */

public class BankAccountServiceImpl implements BankAccountService {
    private BankAccountDAO bankAccountDAO;

    public void setBankAccountDAO(BankAccountDAO bankAccountDAO) {
        this.bankAccountDAO = bankAccountDAO;
    }

    public List<BankAccount> findAll() {
        return bankAccountDAO.findAll();
    }

    public BankAccount findById(Integer id) {
        return bankAccountDAO.findById(id);
    }

    public void save(BankAccount bankAccount) {
        bankAccountDAO.save(bankAccount);
    }

    public void update(BankAccount bankAccount) {
        bankAccountDAO.update(bankAccount);
    }

    public void deleteById(Integer id) {
        bankAccountDAO.deleteById(id);
    }
}
