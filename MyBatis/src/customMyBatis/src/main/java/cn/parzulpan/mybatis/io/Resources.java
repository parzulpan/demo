package cn.parzulpan.mybatis.io;

import java.io.InputStream;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 使用类加载器读取配置文件
 */

public class Resources {

    /**
     * 用于加载 xml 文件，并且得到一个流对象
     * @param xmlPath xml 文件路径
     * @return 流对象
     */
    public static InputStream getResourceAsStream(String xmlPath) {
        return Resources.class.getClassLoader().getResourceAsStream(xmlPath);
    }
}
