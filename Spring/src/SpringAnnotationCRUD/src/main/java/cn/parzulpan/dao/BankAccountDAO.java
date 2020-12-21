package cn.parzulpan.dao;

import cn.parzulpan.domain.BankAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的持久层接口
 */

public interface BankAccountDAO {

    List<BankAccount> findAll();

    BankAccount findById(Integer id);

    void save(BankAccount bankAccount);

    void update(BankAccount bankAccount);

    void deleteById(Integer id);
}
