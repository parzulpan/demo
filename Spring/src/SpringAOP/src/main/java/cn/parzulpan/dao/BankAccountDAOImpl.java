package cn.parzulpan.dao;

import cn.parzulpan.domain.BankAccount;
import cn.parzulpan.utils.ConnectionUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的持久层接口的实现类，使用 ConnectionUtil 事务控制
 */

@Repository("bankAccountDAO")
public class BankAccountDAOImpl implements BankAccountDAO {
    @Autowired
    private QueryRunner runner;
    @Autowired
    private ConnectionUtil connectionUtil;

    public List<BankAccount> findAll() {
        try {
            return runner.query(connectionUtil.getThreadConnection(), "select * from bankAccount",
                    new BeanListHandler<BankAccount>(BankAccount.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BankAccount findById(Integer id) {
        try {
            return runner.query(connectionUtil.getThreadConnection(), "select * from bankAccount where id = ?",
                    new BeanHandler<BankAccount>(BankAccount.class), id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(BankAccount bankAccount) {
        try {
            runner.update(connectionUtil.getThreadConnection(), "insert into bankAccount(name, money) values (?, ?)",
                    bankAccount.getName(), bankAccount.getMoney());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(BankAccount bankAccount) {
        try {
            runner.update(connectionUtil.getThreadConnection(), "update bankAccount set name = ?, money = ? where id = ?",
                    bankAccount.getName(), bankAccount.getMoney(), bankAccount.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Integer id) {
        try {
            runner.update(connectionUtil.getThreadConnection(), "delete from bankAccount where id = ?",
                    id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public BankAccount findByName(String accountName) {
        try {
            List<BankAccount> accounts = runner.query(connectionUtil.getThreadConnection(), "select * from bankAccount where name = ?",
                    new BeanListHandler<BankAccount>(BankAccount.class), accountName);
            if (accounts == null || accounts.size() == 0) {
                return null;
            }
            if (accounts.size() > 1) {
                throw new RuntimeException("结果集不一致，请检查账户名称！");
            }
            return accounts.get(0);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
