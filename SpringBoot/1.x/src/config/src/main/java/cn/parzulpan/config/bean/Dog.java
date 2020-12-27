package cn.parzulpan.config.bean;

import org.springframework.stereotype.Component;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Component
public class Dog {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
