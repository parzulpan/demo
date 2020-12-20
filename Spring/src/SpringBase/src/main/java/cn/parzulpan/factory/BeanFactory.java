package cn.parzulpan.factory;

import java.io.InputStream;
import java.util.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 工厂类，负责给从容器中获取指定对象的类
 */

public class BeanFactory {
    private static Properties properties;

    private static Map<String, Object> beans;   // Factory 解耦的优化，存放创建的对象，称为容器

    static {
        try {
            // 实例化对象
            properties = new Properties();
            // 获取文件流对象，使用类加载器
            InputStream is = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
            properties.load(is);

            beans = new HashMap<>();
            Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                String beanPath = properties.getProperty(key);
                Object instance = Class.forName(beanPath).newInstance();
                beans.put(key, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("初始化 Properties 失败！");
        }
    }

    /**
     * 获取指定对象的类
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName){
        try {
//            return Class.forName(properties.getProperty(beanName)).newInstance(); // 两个步骤
            System.out.println(beanName + " " + beans.get(beanName));
            return beans.get(beanName); // 两个步骤
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
