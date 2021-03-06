# Java 基本语法-小项目：家庭收支记账软件

## 项目目标

* 模拟实现一个基于文本界面的《家庭记账软件》
* 掌握初步的编程技巧和调试技巧
* 主要涉及以下知识点：
  * 变量的定义；
  * 基本数据类型的使用；
  * 循环语句；
  * 分支语句；
  * 方法声明、调用和返回值的接收；
  * 简单的屏幕输出格式控制。

## 需求说明

* 模拟实现基于文本界面的《家庭记账软件》。
* 该软件能够记录家庭的收入、支出，并能够打印收支明细表。
* 项目采用分级菜单方式。主菜单如下：
  * 1 收支明细
  * 2 登记收入
  * 3 登记支出
  * 4 退出
  * 请选择（1-4）：
* 假设家庭起始的生活基本金为10000元。
* 每次登记收入（菜单2）后，收入的金额应累加到基本金上，并记录本次收入明细，以便后续的查询。
* 每次登记支出（菜单3）后，支出的金额应从基本金中扣除，并记录本次支出明细，以便后续的查询。
* 查询收支明细（ 菜单1）时，将显示所有的收入、支出名细列表。

## 实现思路

[FamilyUtil](../../code/src/com/parzulpan/java/ch02/FamilyUtil.java)

[FamilyAccount](../../code/src/com/parzulpan/java/ch02/FamilyAccount.java)