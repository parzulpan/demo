package cn.parzulpan;

import cn.parzulpan.bean.Employee;
import cn.parzulpan.mapper.EmployeeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationCacheApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    RedisTemplate redisTemplate;    // k-v 都是对象

    @Autowired
    StringRedisTemplate stringRedisTemplate;    // k-v 都是字符串

    @Test
    public void contextLoads() {
        Employee employee = employeeMapper.getEmpById(1);
        System.out.println(employee);
    }

    @Test
    public void testString() {
        String msg = stringRedisTemplate.opsForValue().get("msg");
        System.out.println(msg);
    }

}
