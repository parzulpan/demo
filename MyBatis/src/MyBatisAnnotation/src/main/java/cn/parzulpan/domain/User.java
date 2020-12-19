package cn.parzulpan.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户的实体类
 */

public class User implements Serializable {
    private Integer userId; // 注意这里的和数据库表的列名不一致
    private String userName;
    private Date userBirthday;
    private String userSex;
    private String userAddress;


    private List<Account> accounts; //一对多关系映射：主表方法应该包含一个从表方的集合引用

    public User() {
    }

    public User(Integer userId, String userName, Date userBirthday, String userSex, String userAddress) {
        this.userId = userId;
        this.userName = userName;
        this.userBirthday = userBirthday;
        this.userSex = userSex;
        this.userAddress = userAddress;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userBirthday=" + userBirthday +
                ", userSex='" + userSex + '\'' +
                ", userAddress='" + userAddress + '\'' +
                '}';
    }
}
