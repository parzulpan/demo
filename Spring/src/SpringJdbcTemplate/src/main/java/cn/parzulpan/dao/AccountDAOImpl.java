package cn.parzulpan.dao;

import domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层实现类，这种方式即可以实现注解配置，也可以使用 xml 配置
 */

@Repository("accountDAO")
public class AccountDAOImpl implements AccountDAO {
    @Resource(name = "jdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Account> findAll() {
        return jdbcTemplate.query("select * from bankAccount",
                new BeanPropertyRowMapper<>(Account.class));
    }

    @Override
    public Account findById(Integer id) {
        List<Account> accounts = jdbcTemplate.query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<>(Account.class),
                id);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account findByName(String name) {
        List<Account> accounts = jdbcTemplate.query("select * from bankAccount where name = ?",
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
        jdbcTemplate.update("update bankAccount set name = ? , money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }

    @Override
    public void insert(Account account) {
        jdbcTemplate.update("insert into bankAccount(name, money) values (?, ?)",
                account.getName(), account.getMoney());
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("delete from bankAccount where id = ?",
                id);
    }

    @Override
    public Long getCount() {
        return jdbcTemplate.queryForObject("select count(*) from bankAccount;",
                Long.class);
    }
}
