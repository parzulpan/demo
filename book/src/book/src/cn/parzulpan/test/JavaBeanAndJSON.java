package cn.parzulpan.test;

import cn.parzulpan.bean.User;
import com.google.gson.Gson;

/**
 * @Author : parzulpan
 * @Time : 2020-12-14
 * @Desc : JavaBean 和 JSON 的互转
 */

public class JavaBeanAndJSON {
    public static void main(String[] args) {
        // 创建 JavaBean 对象
        User user = new User(1, "parzulpan", "123456", "parzulpan@321.com");
        // 创建 Gson 对象
        Gson gson = new Gson();

        // toJson 方法可以把 java 对象转换成为 json 字符串
        String userJsonString = gson.toJson(user);
        System.out.println(userJsonString);

        // fromJson 把 json 字符串转换回 Java 对象
        // 第一个参数是 json 字符串
        // 第二个参数是转换回去的 Java 对象类型，对于 JavaBean 可以直接 .class
        User user1 = gson.fromJson(userJsonString, User.class);
        System.out.println(user1);
    }
}
