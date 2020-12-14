package cn.parzulpan.test;

import cn.parzulpan.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12-14
 * @Desc : Map 和 JSON 的互转
 */

public class MapAndJSON {
    public static void main(String[] args) {
        Map<Integer, User> userMap = new LinkedHashMap<>();

        userMap.put(1, new User(1, "parzulpan", "123456", "parzulpan@321.com"));
        userMap.put(2, new User(null, "tom", "214531", "tom@321.com"));

        Gson gson = new Gson();

        String userMapJsonString = gson.toJson(userMap);
        System.out.println(userMapJsonString);

//        Map<Integer, User> userMap1 = gson.fromJson(userMapJsonString, new UserMapType().getType());
        // 或者用匿名类，推荐这种做法
        Map<Integer, User> userMap1 = gson.fromJson(userMapJsonString, new TypeToken<LinkedHashMap<Integer, User>>() {
        }.getType());

        System.out.println(userMap1);
        User user = userMap1.get(1);
        System.out.println(user);
    }
}

class UserMapType extends TypeToken<LinkedHashMap<Integer, User>> {

}
