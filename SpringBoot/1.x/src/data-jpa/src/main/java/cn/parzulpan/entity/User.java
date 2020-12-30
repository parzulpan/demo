package cn.parzulpan.entity;

import javax.persistence.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 使用 JPA 注解配置映射关系
 */

@Entity // 告诉 JPA 这是一个实体类，即和数据库映射的表
@Table  // 指定和哪个数据表对应，如果没有这个表在配置中可以指定自动创建，如果省略默认表名就是 user，即类名首字母小写
public class User {

    @Id // 这是一个主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 策略是自增
    private Integer id;

    @Column(name = "last_name", length = 50)    // 指定和数据表对应的一个列，如果省略默认列名就是属性名
    private String lastName;

    @Column
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
