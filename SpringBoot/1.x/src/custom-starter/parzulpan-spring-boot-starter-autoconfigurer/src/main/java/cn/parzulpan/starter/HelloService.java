package cn.parzulpan.starter;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : HelloService
 */

public class HelloService {

    HelloServiceProperties helloServiceProperties;

    public HelloServiceProperties getHelloServiceProperties() {
        return helloServiceProperties;
    }

    public void setHelloServiceProperties(HelloServiceProperties helloServiceProperties) {
        this.helloServiceProperties = helloServiceProperties;
    }

    public String sayHelloName(String name) {
        return helloServiceProperties.getPrefix() + " - " + name + " - " + helloServiceProperties.getSuffix();
    }
}
