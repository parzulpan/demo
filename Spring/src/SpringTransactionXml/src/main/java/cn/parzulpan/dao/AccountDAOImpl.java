package cn.parzulpan.dao;

import cn.parzulpan.domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的持久层接口的实现类
 */

public class AccountDAOImpl extends JdbcDaoSupport implements AccountDAO {

    public Account findById(Integer accountId) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<Account>(Account.class),
                accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public Account findByName(String name) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where name = ?",
                new BeanPropertyRowMapper<Account>(Account.class),
                name);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    public void update(Account account) {
        getJdbcTemplate().update("update bankAccount set name = ?, money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }
}
