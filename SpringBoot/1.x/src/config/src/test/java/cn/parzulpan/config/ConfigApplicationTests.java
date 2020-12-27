package cn.parzulpan.config;

import cn.parzulpan.config.bean.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigApplicationTests {

    @Autowired
    Person person;

    @Autowired
    ApplicationContext ioc;

    public void testHasHelloService() {
        boolean helloService = ioc.containsBean("helloService");
        System.out.println(helloService);
    }

    @Test
    public void contextLoads() {
        System.out.println(person);
    }

}
