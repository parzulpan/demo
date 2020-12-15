package cn.parzulpan.mybatis.cfg;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 核心配置类，包含数据库信息、sql 的 map 集合
 */

public class Configuration {
    private String username;    //用户名
    private String password;    //密码
    private String url; //地址
    private String driver;  //驱动

    /**
     要想让 **`selectList()`** 执行，需要给方法提供两个信息

     * 连接信息
     * 映射信息，包括 执行的 SQL 语句 和 封装结果的实体类全限定类名，可以把这两个信息组合起来定义成一个 **Mapper 对象**

     这个对象可以用用一个 Map 存储起来：

     * **key** 是一个 String，值为 `cn.parzulpan.dao.UserDAO.findAll`
     * **value** 即这个 Mapper 对象，属性有 `String sql` 和 `String domainClassPath`

     */

    private Map<String, Mapper> mappers = new HashMap<>();

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Map<String, Mapper> getMappers() {
        return mappers;
    }

    public void setMappers(Map<String, Mapper> mappers) {
        this.mappers.putAll(mappers);   // 注意这里是追加的方式，而不是覆盖
    }
}
