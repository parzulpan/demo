# 前言

记录开发和办公环境搭建，持续更新。

## 开发环境

### Java 环境



### Python 环境



### C/C++ 环境



### Node.js 环境



### 虚拟机环境

#### 安装 VirtualBox

[下载地址](https://www.virtualbox.org/)

#### 安装 Vagrant

[Vagrant](https://www.vagrantup.com/) 可以帮助我们快速创建出一个虚拟机，可以在 [Vagrant Boxes](https://app.vagrantup.com/boxes/search) 查看镜像。

安装完 Vagrant 后，使用 `vagrant init centos/7`，初始化一个 CentOS7 系统，值得注意的是，这个命令在那个路径下执行，生成的 Vagrantfile 就在这个路径。

使用 `vagrant up`启动虚拟机环境，启动后出现 `default folder:… =>/vagrant`，表示启动成功，然后 ctrl+c 退出。使用 `vagrant ssh` 连上虚拟机，默认账户密码都是 vagrant，可以使用 exit 退出。

不过她使用的网络方式是网络地址转换 NAT（端口转发），如果其他主机要访问虚拟机，必须要进行端口映射，这样太过麻烦。可以给虚拟机一个固定的 ip 地址，Windows 和虚拟机可以互相 ping 通。具体操作是：打开 Vagrantfile 更改虚拟机ip，修改其中的`config.vm.network "private_network",ip:"192.168.56.56"`。

#### 安装 Docker

在 CentOS7 系统安装 [Docker](https://docs.docker.com/engine/install/centos/)，具体步骤为：

```shell
# root
su

# 卸载系统之前的 docker 
yum remove docker \
		  docker-client \
		  docker-client-latest \
		  docker-common \
		  docker-latest \
		  docker-latest-logrotate \
		  docker-logrotate \
		  docker-engine
                  
# 安装软件包
yum install -y yum-utils

# 设置存储库
yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo

# 安装 docker 引擎
yum install docker-ce docker-ce-cli containerd.io

# 启动 docker
systemctl start docker

# 设置开机自启动
systemctl enable docker

# 检查是否安装成功
docker -v

# 配置阿里云镜像加速，参考 https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors
mkdir -p /etc/docker

tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://wh1acsne.mirror.aliyuncs.com"]
}
EOF

systemctl daemon-reload
systemctl restart docker

# 创建文件夹保存 Docker 所有镜像数据
cd /
mkdir docker

```

**安装 [MySQL](https://registry.hub.docker.com/_/mysql)**，具体步骤为：

```shell
# 安装镜像
docker pull mysql:5.7.32

# 创建并编辑 MySQL 配置
touch /docker/mysql5.7.32/conf/my.cnf
vi /docker/mysql5.7.32/conf/my.cnf
# 初始配置为
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# 启动 MySQL
# --name 指定容器名字 -v 目录挂载 -p 指定端口映射  -e 设置mysql参数 -d 后台运行
sudo docker run -p 3306:3306 --name mysql5.7.32.3306 \
-v /docker/mysql5.7.32/log:/var/log/mysql \
-v /docker/mysql5.7.32/data:/var/lib/mysql \
-v /docker/mysql5.7.32/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7.32

# 查看运行情况
docker ps -a

# 设置 MySQL 自启动
docker update mysql5.7.32.3306 --restart=always

# 进入 MySQL
docker exec -it mysql5.7.32.3306 /bin/bash

```

**安装 [Redis](https://registry.hub.docker.com/_/redis)**，具体步骤为：

```shell
# 安装镜像
docker pull redis:3.0.7
docker pull redis:6.0.9

# 创建并编辑 Redis 配置
touch /docker/redis3.0.7/redis.conf
vi /docker/redis3.0.7/redis.conf
# 初始配置为
appendonly yes
port 6379
# 创建多个 Redis 配置文件，并更改多个配置的端口，方便后面使用
cd /docker/redis3.0.7
cp redis.conf /redis6379/redis.conf /redis6380/redis.conf /redis6381/redis.conf

# 启动多个 Redis
docker run -p 6379:6379 --name redis3.0.7.6379 -v /docker/redis3.0.7/redis6379/data:/redis6379/data -v /docker/redis3.0.7/redis6379/redis.conf:/etc/redis6379/redis.conf -d redis:3.0.7 redis-server /etc/redis6379/redis.conf

docker run -p 6380:6380 --name redis3.0.7.6380 -v /docker/redis3.0.7/redis6380/data:/redis6380/data -v /docker/redis3.0.7/redis6380/redis.conf:/etc/redis6380/redis.conf -d redis:3.0.7 redis-server /etc/redis6380/redis.conf

docker run -p 6381:6381 --name redis3.0.7.6381 -v /docker/redis3.0.7/redis6381/data:/redis6381/data -v /docker/redis3.0.7/redis6381/redis.conf:/etc/redis6381/redis.conf -d redis:3.0.7 redis-server /etc/redis6381/redis.conf

# 查看运行情况
docker ps -a

# 设置 Redis 自启动
docker update redis3.0.7.6379 --restart=always
docker update redis3.0.7.6380 --restart=always
docker update redis3.0.7.6381 --restart=always

# 进入 Redis
docker exec -it redis3.0.7.6379 /bin/bash [redis-cli]
docker exec -it redis3.0.7.6380 /bin/bash [redis-cli]
docker exec -it redis3.0.7.6381 /bin/bash [redis-cli]

```

### Jetbrains 套件

#### 安装 IntelliJ IDEA

#### 安装 PyCharm

#### 安装 CLion

#### 安装 WebStorm



## 办公环境

## 其他环境

## 总结