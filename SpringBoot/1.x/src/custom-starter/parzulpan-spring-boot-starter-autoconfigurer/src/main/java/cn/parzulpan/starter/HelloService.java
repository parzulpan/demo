package cn.parzulpan.starter;

import java.util.ArrayList;

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
        ArrayList<Integer> tmp = new ArrayList<>();
        Integer[] array = tmp.toArray(new Integer[0]);
        int[] ints = tmp.stream().mapToInt(Integer::valueOf).toArray();
    }

    public String sayHelloName(String name) {
        return helloServiceProperties.getPrefix() + " - " + name + " - " + helloServiceProperties.getSuffix();
    }
}
