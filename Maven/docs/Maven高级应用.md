# Maven 高级应用

## Maven 基础

Maven 是一个项目管理工具，它有如下**好处**：

* 节省磁盘空间
* 可以一键构建
* 可以跨平台使用
* 依赖传递和管理，提高开发效率

**一键构建**：Maven 自身集成了 Tomcat 插件，可以对项目进行编译、测试、打包、安装、发布等操作。

**依赖传递和管理**：Maven工程真正的 jar 包放置在仓库中，项目中只需要配置上 jar 包的坐标即可。坐标的书写规范，`groupId` 公司或组织域名的倒序，`artifactId` 项目名或模块名，`version` 版本号

**坐标示例**：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.4</version>
</dependency>
```

**仓库的种类和关系**：

* 分为本地仓库，远程仓库（私服），中央仓库
* 启动一个 Maven 工程的时候，pom.xml 会去本地仓库寻找对应的 jar 包，默认情况下，如果本地仓库没有对应的 jar 包，Maven 工程会先私服（如果有）下载 jar 包到本地，如果没有私服，则会从 **[中央仓库](https://mvnrepository.com/)** 下载 jar 包到本地

**Maven 依赖的作用域**：

* `compile` 默认值，参与当前项目的编译、测试、运行等周期，是一个比较强的依赖，会被打包到最终的 artifact 中。
* `provided` 假定对应的依赖会由运行这个应用的 JDK 或者容器来提供，例如 ServletAPI 和 数据库连接。理论上参与编译、测试、运行等周期，不会被打包到最终的 artifact 中。
* `runtime` 参与测试、运行期等周期，不参与项目的编译，会被打包到最终的 artifact 中。
* `test` 参与测试工作过程中的测试和执行，不会被打包到最终的 artifact 中。
* `system` 和 provide 类似，唯一的区别在于它需要使用者告诉 Maven 如何去找到这个依赖。
* `import` 从其它的 pom 文件中导入依赖设置，它只在 `dependencyManagement` 元素下使用，表示从其他 pom 中导入 dependency 的配置。

**Maven 常用命令**：

* `compile` 编译源代码
* `test` 运行应用程序中的单元测试
* `package` 依据项目生成 jar 包
* `install` 在本地 Repository 中安装 jar 包
* `deploy` 将 jar 包 上传到私服
* `clean` 清除目标目录中的生成结果

**Maven 生命周期**：

* **清理生命周期** 在开始真正的项目构建之前进行一些清理工作。
* **默认生命周期** 构建项目的核心部分，包括编译、测试、打包、部署等。
* **站点生命周期** 生成项目报告、站点，发布站点。

## 传统构建工程

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SSM-Integration)

### 定义项目 pom.xml

### DAO 层

DAO 层 大致步骤为：

* **实体类：**
* **持久层接口：**
* **DAO 层 Spring 配置文件：**
* **单元测试：**

### Service 层

Service 层 大致步骤为：

* **业务层接口：**
* **业务层接口实现类：**
* **Service 层 Spring 配置文件：**
* **单元测试：**

### Web 层

Web 层 大致步骤为：

* **控制器类：**
* **SpringMVC 配置文件：**
* **web.xml 配置文件：**

### JSP 页面

### 运行和调试

添加 tomcat7 插件，直接点击 run 即可

```xml
  <build>
    <plugins>
      <!-- 添加 tomcat7 插件 -->
      <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.2</version>
        <configuration>
          <path>/</path>
          <port>8080</port>
        </configuration>
      </plugin>
    </plugins>

  </build>
```

## 分模块构建工程

[本节源码](https://github.com/parzulpan/demo/tree/main/Maven/src/MavenModular)

使用 IDEA 新建一个 Maven 工程，选择 `maven-archetype-webapp`

* 创建 ssm_parent 父工程（打包方式选择 pom，必须的）
* 创建 ssm_web 子模块（打包方式是 war 包）
* 创建 ssm_service 子模块（打包方式是 jar 包）
* 创建 ssm_dao 子模块（打包方式是 jar 包）
* 创建 ssm_domain 子模块（打包方式是 jar 包）
* **web 依赖于 service，service 依赖于 dao，dao 依赖于 domain**
* 在 ssm_parent 的 pom.xml 文件中引入坐标依赖

![Maven 分模块构建工程](https://images.cnblogs.com/cnblogs_com/parzulpan/1906920/o_201226084834Maven%E5%88%86%E6%A8%A1%E5%9D%97%E6%9E%84%E5%BB%BA%E5%B7%A5%E7%A8%8B.png)

这就是继承和聚合，通常继承和聚合同时使用。**继承**，创建一个 parent 工程将所需的依赖都配置在 pom 中。**聚合**，聚合多个模块运行。

继承是为了消除重复，如果将 dao、service、web 分开创建独立的工程则每个工程的 pom.xml 文件中的内容存在重复，比如：设置编译版本、锁定 spring 的版本等，可以将这些重复的配置提取出来在父工程的 pom.xml 中定义。

项目开发通常是分组分模块开发，每个模块开发完成要运行整个工程需要将每个模块聚合在一起运行，比如：dao、service、web 三个工程最终会打一个独立的 war 运行。

### 运行和调试

**启动方式一**：本地 Tomcat 启动

这种方式需要注意选择正确的 Artifacts，比如该项目就应该选择 web 模块。

---

**启动方式二**：Tomcat 插件 启动 root，直接点击 run 即可

---

**启动方式三**：Tomcat 插件 启动  web 模块，需要先 root install

## 总结和练习
