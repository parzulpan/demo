package cn.parzulpan.dao;

import domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层实现类，继承 JdbcDaoSupport
 *
 * JdbcDaoSupport 是 Spring 框架提供的一个类，该类中定义了一个 JdbcTemplate 对象，
 * 可以直接获取使用，但是要想创建该对象，需要为其提供一个数据源。
 *
 * 这种的好处是当有很多个 DAO 时，不需要注入很多 jdbcTemplate
 *
 * 注意：这种方式只能使用 xml 配置，因为 JdbcDaoSupport 中已经定义了 jdbcTemplate，且提供其 setter
 */

public class AccountDAOImpl2 extends JdbcDaoSupport implements AccountDAO {
    @Override
    public List<Account> findAll() {
        return super.getJdbcTemplate().query("select * from bankAccount",
                new BeanPropertyRowMapper<>(Account.class));
    }

    @Override
    public Account findById(Integer id) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<>(Account.class),
                id);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account findByName(String name) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where name = ?",
                new BeanPropertyRowMapper<>(Account.class),
                name);
        if (accounts.isEmpty()) {
            return null;
        }

        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一！");
        }

        return accounts.get(0);
    }

    @Override
    public void update(Account account) {
        getJdbcTemplate().update("update bankAccount set name = ? , money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }

    @Override
    public void insert(Account account) {
        getJdbcTemplate().update("insert into bankAccount(name, money) values (?, ?)",
                account.getName(), account.getMoney());
    }

    @Override
    public void delete(Integer id) {
        getJdbcTemplate().update("delete from bankAccount where id = ?",
                id);
    }

    @Override
    public Long getCount() {
        return getJdbcTemplate().queryForObject("select count(*) from bankAccount;",
                Long.class);
    }
}
