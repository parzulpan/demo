package cn.parzulpan.domain;

import java.io.Serializable;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户实体类
 */

public class Account implements Serializable {
    private Integer id;
    private String name;
    private Float money;
    private Address address;

    public Account() {
    }

    public Account(Integer id, String name, Float money, Address address) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", address=" + address +
                '}';
    }
}
