package cn.parzulpan.utils;

import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc : WebUtils 工具类
 */

public class WebUtils {

    /**
     * 一次性的把所有请求的参数注入到 JavaBean 中
     * @param value request.getParameterMap()
     * @param bean Java Bean
     * @param <T> Class Type
     * @return Java Bean
     */
    public static <T> T copyParamToBean(Map value, T bean) {

        try {
//            System.out.println("注入之前：" + bean);

            BeanUtils.populate(bean, value);

//            System.out.println("注入之后：" + bean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 将字符串转换为整型
     * @param str 字符串
     * @param value 默认值
     * @return 整型数
     */
    public static int parseInt(String str, int value) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
//            e.printStackTrace();
        }
        return value;
    }
}
