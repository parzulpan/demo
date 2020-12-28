package cn.parzulpan;


import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebRestfulCrudApplicationTests {

    @Test
    public void contextLoads() {
        LoggerFactory.getLogger(WebRestfulCrudApplicationTests.class).debug("WebRestfulCrudApplicationTests Log");
    }

}
