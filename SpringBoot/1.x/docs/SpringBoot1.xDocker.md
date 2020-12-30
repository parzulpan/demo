# SpringBoot1.x Docker

## 核心概念

Docker 是一个开源的应用容器引擎，是一个轻量级容器技术。Docker 支持将软件编译成一个镜像，然后在镜像中各种软件做好配置，将镜像发布出去，其他使用者可以直接使用这个镜像。运行中的这个镜像称为容器，容器启动是非常快速的。

Docker 镜像（Images） 用于创建 Docker 容器的模版。

Docker 容器（Container） 镜像启动后的实例称为一个容器，是独立运行的一个或一组应用。

Docker 客户端（Client） 通过命令行或者其他工具使用 Docker API 与 Docker 的守护进程通信。

Docker 主机（Host） 一个物理或者虚拟的机器用于执行 Docker 守护进程和容器。

Docker 仓库（Registry） 用来保存镜像，可以理解为代码仓库。

从概念上来看 Docker 和我们传统的虚拟机比较类似，只是更加轻量级，更加方便使，Docker 和虚拟机最主要的区别有以下几点：

* 虚拟化技术依赖的是物理CPU和内存，是硬件级别的；而我们的 Docker 是构建在操作系统层面的，利用操作系统的容器化技术，所以 Docker 同样的可以运行在虚拟机上面。
* 我们知道虚拟机中的系统就是我们常说的操作系统镜像，比较复杂；而 Docker 比较轻量级，我们可以用 Docker 部署一个独立的 Redis，就类似于在虚拟机当中安装一个 Redis 应用，但是我们用 Docker 部署的应用是完全隔离的。
* 我们都知道传统的虚拟化技术是通过快照来保存状态的；而 Docker 引入了类似于源码管理的机制，将容器的快照历史版本一一记录下来，切换成本非常之低。
* 传统虚拟化技术在构建系统的时候非常复杂；而 Docker 可以通过一个简单的 Dockerfile 文件来构建整个容器，更重要的是 Dockerfile 可以手动编写，这样应用程序开发人员可以通过发布 Dockerfile 来定义应用的环境和依赖，这样对于持续交付非常有利。

### Docker Engine

![Docker Engine](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201230021945docker-engine.png)

Docker Engine 是一个 C/S 架构的应用程序，主要包含下面几个组件：

* 常驻后台进程 Dockerd
* 一个用来和 Dockerd 交互的 REST API Server
* 命令行 CLI 接口，通过和 REST API 进行交互（我们经常使用的 docker 命令）

### Docker 架构

![Docker 架构](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201230021956docker-structrue.png)

Docker 使用 C/S （客户端/服务器）体系的架构，Docker 客户端与 Docker 守护进程通信，Docker 守护进程负责构建，运行和分发 Docker 容器。Docker 客户端和守护进程可以在同一个系统上运行，也可以将 Docker 客户端连接到远程 Docker 守护进程。Docker 客户端和守护进程使用 REST API 通过UNIX套接字或网络接口进行通信。

* Docker Damon：dockerd，用来监听 Docker API 的请求和管理 Docker 对象，比如镜像、容器、网络和 Volume。
* Docker Client：docker，docker client 是我们和 Docker 进行交互的最主要的方式方法，比如我们可以通过 docker run 命令来运行一个容器，然后我们的这个 client 会把命令发送给上面的 Dockerd，让他来做真正事情。
* Docker Registry：用来存储 Docker 镜像的仓库，Docker Hub 是 Docker 官方提供的一个公共仓库，而且 Docker 默认也是从 Docker Hub 上查找镜像的，当然你也可以很方便的运行一个私有仓库，当我们使用 docker pull 或者 docker run 命令时，就会从我们配置的 Docker 镜像仓库中去拉取镜像，使用 docker push 命令时，会将我们构建的镜像推送到对应的镜像仓库中。
* Images：镜像，镜像是一个只读模板，带有创建 Docker 容器的说明，一般来说的，镜像会基于另外的一些基础镜像并加上一些额外的自定义功能。比如，你可以构建一个基于 Centos 的镜像，然后在这个基础镜像上面安装一个 Nginx 服务器，这样就可以构成一个属于我们自己的镜像了。
* Containers：容器，容器是一个镜像的可运行的实例，可以使用 Docker REST API 或者 CLI 来操作容器，容器的实质是进程，但与直接在宿主执行的进程不同，容器进程运行于属于自己的独立的命名空间。因此容器可以拥有自己的 root 文件系统、自己的网络配置、自己的进程空间，甚至自己的用户 ID 空间。容器内的进程是运行在一个隔离的环境里，使用起来，就好像是在一个独立于宿主的系统下操作一样。这种特性使得容器封装的应用比直接在宿主运行更加安全。
* 底层技术支持：Namespaces（做隔离）、CGroups（做资源限制）、UnionFS（镜像和容器的分层） the-underlying-technology Docker 底层架构分析

## 常用操作

### 镜像操作

* **检索** `docker search keywords` 去 docker hub 上检索镜像的详细信息
* **拉取** `docker pull image-name:tag` tag 是可选的，tag 表示标签，多为软件的版本，默认是 latest
* **列表** `docker images` 查看所有本地镜
* **删除** `docker rmi image-id` 删除指定的本地镜像

### 容器操作

* **运行** `docker run -it -p host-port:container-port --name container-name -d image-name /bin/bash` -it 需要一个交互式终端，-p host-port:container-port 主机端口映射到容器内部的端口，--name container-name 自定义容器名，-d 后台运行， image-name 指定镜像模板
* **列表** `docker ps -a` 查看所有运行的容器
* **停止** `docker stop container-name/container-id` 停止当前运行的容器
* **启动** `docker start container-name/container-id` 启动以停止的容器
* **删除** `docker rm container-id` 删除指定容器
* **日志** `docker logs container-name/container-id` 查看容器日志

## 安装示例

* 安装容器 `docker pull mysql:5.7.32`
* 运行容器并且建立目录映射 `docker run -it -p 3306:3306 --name mysql -v /usr/local/docker/mysql/conf:/etc/mysql -v /usr/local/docker/mysql/logs:/var/log/mysql -v /usr/local/docker/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7.32`
* 容器外壳访问 `docker exec -it mysqlP /bin/bash` `mysql -uroot -p123456`
* 进入 docker 本地客户端设置远程访问账号 `grant all privileges on *.* to root@'%' identified by "123456";` `flush privileges;`
* 使用远程连接 Mysql 应该访问：

    ```txt
    host: 127.0.0.1 localhost
    port: 3306
    user: root
    password: 123456
    ```

[其他安装和使用可以查看 DockerHub](https://hub.docker.com/?utm_source=docker4mac_3.0.3&utm_medium=hub&utm_campaign=referral)

## 宿主机不能访问 localhost

解决以 MacOS 作为宿主机，Docker 内已经成功启动 Tomcat，但是宿主机浏览器却不能访问 localhost 的问题。

原因就在于，在 docker 容器内 tomcat 目录下真正存放在 webapps 目录下的文件却存放于 webapps.dist中。

创建 Tomcat 容器，端口映射为8888：

```shell
docker run -it --name romcatP -p 8888:8080 -d tomcat /bin/bash
```

进入 tomcat 容器中：

```shell
docker exec -it tomcatP /bin/bash
```

分别查看 webapps 文件夹和 webapps.dist 文件夹，发现 webapps目录下无文件：

```shell
cd webapps
ls
cd ../webapps.dist
ls
```

解决问题：

```shell
cd ..
ls -l
mv webapps webapp2
mv webapps.dist/ webapps
cd bin
./startup.sh
```

## 练习和总结

[参考链接](https://www.qikqiak.com/k8s-book/)