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
     * @param value
     * @param bean
     * @param <T>
     * @return
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
}
