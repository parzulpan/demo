# MySQL高级 查询截取分析

## 查询优化

### MySQL 优化原则

#### 调优步骤

1. 开启并捕获慢查询；
2. 使用 explain 分析 SQL 性能；
3. 使用 show profile 查询 SQL 执行细节和生命周期情况；
4. 进行 SQL 数据库服务器参数调优。

#### in 和 exists

* **exists 语法**：`select ... from table where exists(subQuery)`，将查询的数据放到子查询中做条件验证，根据验证结果（ture 或 false）来决定主查询的数据结果是否得以保留。

* exists 子查询的实际执行过程可能经过了优化，它也可以用条件表达式、其他子查询或者 JOIN 来替代。

* exists 和 in 的**使用原则**：

  * 永远是小表驱动大表，即小的数据集驱动大的数据集
  * 当 A 表 数据集 小于 B 表数据集时，使用 exist：`select * from A where exists (select id from B where B.id = A.id)`
  * 当 A 表 数据集 大于 B 表数据集时，使用 in：`select * from A where id in (select id from B)`

* exists 和 in 的**使用案例**：

  ```sql
  mysql> select count(*) from tbl_emp;
  +----------+
  | count(*) |
  +----------+
  |        8 |
  +----------+
  1 row in set (0.00 sec)
  
  mysql> select count(*) from tbl_dept;
  +----------+
  | count(*) |
  +----------+
  |        5 |
  +----------+
  1 row in set (0.00 sec)
  
  # 所以 tbl_emp 数据集 大于 tbl_dept
  
  # exists 使用
  mysql> select * from tbl_emp e where exists (select id from tbl_dept d where e.deptId = d.id);
  +----+------+--------+
  | id | NAME | deptId |
  +----+------+--------+
  |  1 | z3   |      1 |
  |  2 | z4   |      1 |
  |  3 | z5   |      1 |
  |  4 | w5   |      2 |
  |  5 | w6   |      2 |
  |  6 | s7   |      3 |
  |  7 | s8   |      4 |
  +----+------+--------+
  7 rows in set (0.00 sec)
  
  # in 使用
  mysql> select * from tbl_emp e where e.deptId in (select id from tbl_dept);
  +----+------+--------+
  | id | NAME | deptId |
  +----+------+--------+
  |  1 | z3   |      1 |
  |  2 | z4   |      1 |
  |  3 | z5   |      1 |
  |  4 | w5   |      2 |
  |  5 | w6   |      2 |
  |  6 | s7   |      3 |
  |  7 | s8   |      4 |
  +----+------+--------+
  7 rows in set (0.00 sec)
  ```

### order by 优化



### group by 优化



## 慢查询日志

### 慢查询介绍



### 慢查询日志开启



### 慢查询日志示例



## 批量数据脚本



## Show Profile



## 全局查询日志

