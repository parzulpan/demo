# 操作 BLOB 类型字段

## MySQL BLOB 类型

* MySQL 中，BLOB 是一个二**进制大型对象**，是一个可以存储大量数据的容器，它能容纳不同大小的数据。
* 插入 BLOB 类型的数据必须使用 **PreparedStatement** ，因为 BLOB 类型的数据无法使用字符串拼接写的。
* MySQL 的四种 BLOB 类型（除了在存储的最大信息量上不同外，他们是等同的）：
  * TinyBlob，最大 255 Byte；
  * Blob，最大 65 KB；
  * MediumBlod，最大 16 MB；
  * LongBlod，最大 4 GB。

如果在指定了相关的 Blob 类型以后，还报错：`xxx too large`，那么在 mysql 的安装目录下，找 my.ini（my.cnf） 文件加上如下的配置参数： **max_allowed_packet=16M**。同时注意，修改了 my.ini 文件之后，需要重新启动 mysql 服务。

```java
package cn.parzulpan.jdbc.ch04;

import cn.parzulpan.jdbc.bean.Customer;
import cn.parzulpan.jdbc.util.JDBCUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12-01
 * @Desc : 操作 BLOB 类型字段
 */

public class BLOBTypeTest {

    // 向数据表中插入大数据类型
    @Test
    public void insertBLOBType() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        String sql = "insert into customers(name, email, birth, photo)values(?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, "猫猫");
        ps.setString(2, "cat@gmail.com");
        ps.setDate(3, new Date(new java.util.Date().getTime()));

        //
        FileInputStream fis = new FileInputStream(new File("cat.png"));
        ps.setBlob(4, fis);

        ps.execute();

        fis.close();
        JDBCUtils.closeResource(connection, ps);
    }

    // 修改数据表中的大数据类型
    @Test
    public void updateBLOBType() throws Exception{
        Connection connection = JDBCUtils.getConnection();
        String sql = "update customers set photo = ? where name = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        // 批量插入
        FileInputStream fis = new FileInputStream(new File("girl.jpg"));
        ps.setBlob(1, fis);
        ps.setString(2, "猫猫");

        ps.execute();

        fis.close();
        JDBCUtils.closeResource(connection, ps);
    }

    // 从数据表中读取大数据类型
    @Test
    public void queryBLOBType() throws Exception{
        Connection connection = JDBCUtils.getConnection();
        String sql = "select id, name, email, birth, photo from customers where id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 22);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            Date birth = rs.getDate("birth");
            Customer customer = new Customer(id, name, email, birth);
            System.out.println(customer);

            //
            Blob photo = rs.getBlob("photo");
            InputStream is = photo.getBinaryStream();
            FileOutputStream fos = new FileOutputStream(new File("cat-sql.png"));
            byte[] buffer = new byte[1024];
            int data = 0;
            while ((data = is.read(buffer)) != -1) {
                fos.write(buffer, 0, data);
            }

            JDBCUtils.closeResource(connection, ps, rs);

            fos.close();
            is.close();

        }
    }
}
```

## 总结和练习
