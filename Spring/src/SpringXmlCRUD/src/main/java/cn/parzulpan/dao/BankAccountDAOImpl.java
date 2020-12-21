package cn.parzulpan.dao;

import cn.parzulpan.domain.BankAccount;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的持久层接口的实现类
 */

public class BankAccountDAOImpl implements BankAccountDAO {
    private QueryRunner runner;

    public void setRunner(QueryRunner runner) {
        this.runner = runner;
    }

    public List<BankAccount> findAll() {
        try {
            return runner.query("select * from bankAccount",
                    new BeanListHandler<BankAccount>(BankAccount.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BankAccount findById(Integer id) {
        try {
            return runner.query("select * from bankAccount where id = ?",
                    new BeanHandler<BankAccount>(BankAccount.class), id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(BankAccount bankAccount) {
        try {
            runner.update("insert into bankAccount(name, money) values (?, ?)",
                    bankAccount.getName(), bankAccount.getMoney());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(BankAccount bankAccount) {
        try {
            runner.update("update bankAccount set name = ?, money = ? where id = ?",
                    bankAccount.getName(), bankAccount.getMoney(), bankAccount.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Integer id) {
        try {
            runner.update("delete from bankAccount where id = ?",
                    id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
