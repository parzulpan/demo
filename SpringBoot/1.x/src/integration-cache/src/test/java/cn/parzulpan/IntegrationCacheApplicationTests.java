package cn.parzulpan;

import cn.parzulpan.bean.Employee;
import cn.parzulpan.mapper.EmployeeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationCacheApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    RedisTemplate redisTemplate;    // k-v 都是对象

    @Autowired
    RedisTemplate employeeRedisTemplate;    // 使用自定义的 RedisTemplate

    @Autowired
    StringRedisTemplate stringRedisTemplate;    // k-v 都是字符串

    @Test
    public void contextLoads() {
        Employee employee = employeeMapper.getEmpById(1);
        System.out.println(employee);
    }

    @Test
    public void testRedisString() {
        // 字符串操作
        // String 类型 是 Redis 中最基本的数据类型，一个 key 对应一个 value 。

        stringRedisTemplate.opsForValue().set("stringMsg", "hello");
        stringRedisTemplate.opsForValue().append("stringMsg", "world");

        String msg = stringRedisTemplate.opsForValue().get("stringMsg");
        System.out.println(msg);
    }

    @Test
    public void testRedisList() {
        // 列表操作
        // List 类型 是简单的字符串列表，按照插入顺序排序。可以添加一个元素到列表的头部（左边）或者尾部（右边）。
        ListOperations<String, String> ops = redisTemplate.opsForList();
        ops.leftPush("listMsg", "hello");
        ops.leftPushAll("listMsg", "world", "parzulpan");
        List<String> listMsg = ops.range("listMsg", 0, 2);// 索引 0 到2的 listMsg
        System.out.println(listMsg.toString());
    }

    @Test
    public void testRedisSet() {
        // 集合操作
        // Set 类型 是 String 类型 的无序集合。它的特点是无序且唯一，它是通过哈希表实现的，所以添加、删除、查找的复杂度都是 O(1)。
        SetOperations<String, String> ops = redisTemplate.opsForSet();
        ops.add("setMsg", "hello");
        ops.add("setMsg", "world", "parzulpan");
        Set<String> setMsg = ops.members("setMsg"); //  取 set
        System.out.println(setMsg.toString());
    }

    @Test
    public void testRedisZSet() {
        // 有序集合操作
        // ZSet 类型 和 Set 类型 一样，也是 String 类型元素的集合，且不允许有重复的成员。
        // 不同的是每个元素都会关联一个 double 类型 的分数，它正是通过分数来为集合中的成员进行从小到大的排序。
        // ZSet 类型的成员是唯一的，但分数(score) 却可以重复。
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        ops.add("zsetMsg", "hello", 1);
        ops.add("zsetMsg", "parzulpan", 3);
        ops.add("zsetMsg", "world", 2);
        Set<String> zsetMsg = ops.range("zsetMsg", 0, 2);
        System.out.println(zsetMsg.toString());
    }

    @Test
    public void testRedisHash() {
        // 哈希操作
        // Hash 类型 是一个键值对的集合。它是一个 String 类型 的 field 和 value 组合的映射表，它特别适合用于存储对象。
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ops.put("hashMsg", "key1", "hello");
        ops.put("hashMsg", "key2", "world");
        ops.put("hashMsg", "key3", "parzulpan");
        String key2 = ops.get("hashMsg", "key2");
        System.out.println(key2);
    }

    @Test
    public void testEmployeeRedisTemplate() {
        ValueOperations ops = employeeRedisTemplate.opsForValue();
        Employee employee = employeeMapper.getEmpById(1);
        ops.set("emp-01", employee);
    }

}
