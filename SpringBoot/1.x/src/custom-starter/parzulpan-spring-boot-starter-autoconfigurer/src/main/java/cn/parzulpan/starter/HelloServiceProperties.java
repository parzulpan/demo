package cn.parzulpan.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : HelloService 属性类
 */

@ConfigurationProperties(prefix = "parzulpan.hello")
public class HelloServiceProperties {
    private String prefix;  // 前置语
    private String suffix;  // 后置语

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
