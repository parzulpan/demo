package cn.parzulpan.mybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义 Select 注解
 */

// 生命周期为 RUNTIME，出现位置为 METHOD
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value(); // 配置 SQL 语句
}
