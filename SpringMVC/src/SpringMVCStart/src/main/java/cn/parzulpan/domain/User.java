package cn.parzulpan.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户实体类
 */

public class User implements Serializable {
    private String username;
    private String password;
    private Integer age;
    private List<Account> accounts;
    private Map<String, Account> accountMap;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Map<String, Account> getAccountMap() {
        return accountMap;
    }

    public void setAccountMap(Map<String, Account> accountMap) {
        this.accountMap = accountMap;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", accounts=" + accounts +
                ", accountMap=" + accountMap +
                '}';
    }
}
