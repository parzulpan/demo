package cn.parzulpan.test;

import cn.parzulpan.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * @Author : parzulpan
 * @Time : 2020-12-14
 * @Desc : List 和 JSON 的互转
 */

public class ListAndJSON {
    public static void main(String[] args) {
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User(1, "parzulpan", "123456", "parzulpan@321.com"));
        userList.add(new User(null, "tom", "214531", "tom@321.com"));

        Gson gson = new Gson();

        // 把 List 转换为 JSON 字符串
        String userListJsonString = gson.toJson(userList);
        System.out.println(userListJsonString);

        // 把 JSON 字符串 转换为 List，对于集合，第二个参数需要 Type typeOfT
        // 为了确保范型，需要实现一个类继承 TypeToken
        System.out.println(new UserListType().getType());   // class java.util.ArrayList
        System.out.println(new UserListType().getRawType());    // class cn.parzulpan.test.UserListType
        ArrayList<User> list = gson.fromJson(userListJsonString, new UserListType().getType());
        System.out.println(list);
        User user = list.get(0);
        System.out.println(user);
    }
}

class UserListType extends TypeToken<ArrayList<User>> {

}