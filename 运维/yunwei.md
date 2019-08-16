## 安装nginx

```bash
yum install nginx
sudo systemctl start nginx.service
sudo systemctl end nginx.service
```

##安装lrzsz

```bash
yum install -y lrzsz
```

## 安装golang

```bash
yum install go

#查看环境
go env
#
vi /etc/profile
#生效
source /etc/profile

# GOROOT
export GOROOT=/usr/lib/golang
# GOPATH
export GOPATH=/root/Work/programmer/go/gopath/
# GOPATH bin
export PATH=$PATH:$GOROOT/bin:$GOPATH/bin
```

## 安装mysql

```bash

cd /usr/src
wget https://dev.mysql.com/get/mysql80-community-release-el7-1.noarch.rpm

#安装yum源
yum localinstall mysql80-community-release-el7-1.noarch.rpm

#更新yum源
yum clean all
yum makecache

#开始安装MySQL
yum install mysql-community-server

#启动MySQL
systemctl start mysqld

#启动成功后可以查看初始化密码随机生成的
cat /var/log/mysqld.log | grep password

#登录MySQL修改mysql用户密码
mysql -u root -p
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'yourpassword';
#远程设置
mysql> use mysql;
mysql> update user set host='%' where user='root';
#授权用户名的权限，赋予任何主机访问数据的权限
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'WITH GRANT OPTION;
mysql> FLUSH PRIVILEGES;
其他的几种mysql用户权限的设置方法

#创建用户
CREATE USER 'blog_root'@'%' IDENTIFIED BY 'ser&blog#RootM5';
#授予权限
GRANT ALL ON *.* TO 'blog_root'@'%' WITH GRANT OPTION;

#允许myuser用户使用mypassword密码从任何主机连接到mysql服务器
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'xxxxxxxx' WITH GRANT OPTION;
#允许用户myuser从ip为192.168.1.6的主机连接到mysql服务器，并使用mypassword作为密码
GRANT ALL PRIVILEGES ON *.* TO 'myuser'@'192.168.1.3'IDENTIFIED BY 'mypassword' WITH GRANT OPTION;
数据库的相关操作

#启动mysql
systemctl start mysqld.service

#结束
systemctl stop mysqld.service

#重启
systemctl restart mysqld.service

#开机自启
systemctl enable mysqld.service
mysql登录用户密码设置好后,需要开发安全组端口



端口开放后就可以进行数据库连接操作了，在使用Navicat for MySQL 连接 Mysql 8.0.12可能会出现问题 Client does not support authentication protocol 错误解决方法

#修改加密规则 （这行我没有写，不过貌似也可以）密码需要设置包含大小写字母符号和数字的格式，否则设置不会超成功
ALTER USER 'root'@'%' IDENTIFIED BY 'password' PASSWORD EXPIRE NEVER;
#更新一下用户的密码
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'password';
#刷新权限
FLUSH PRIVILEGES;
```

## 安装redis

```
1、启用EPEL仓库
## RHEL/CentOS 7 64-Bit ##
# wget http://dl.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-5.noarch.rpm
# rpm -ivh epel-release-7-5.noarch.rpm


要验证EPEL仓库是否建立成功，可以执行：
# yum repolist


2、通过Yum安装Redis
# yum -y update
# yum install redis


如果redis还不是最新的，不要安装，继续下面的设置


3、安装Remi的软件源，安装命令如下：
yum install -y http://rpms.famillecollet.com/enterprise/remi-release/7.rpm   数字为centos版本


4、然后可以使用下面的命令安装最新版本的redis：
yum --enablerepo=remi install redis


5、安装完毕后，即可使用下面的命令启动redis服务并设置为开机自动启动：
service redis start
chkconfig redis on

6、Redis开启远程登录连接
原来是redis默认只能localhost登录，所以需要开启远程登录。解决方法如下：

在redis的配置文件redis.conf中，找到bind localhost注释掉。

注释掉本机,局域网内的所有计算机都能访问。

band localhost 只能本机访问,局域网内计算机不能访问。

bind 局域网IP 只能局域网内IP的机器访问, 本地localhost都无法访问。
我没有注释掉bind 127.0.0.1，而是将bind 127.0.0.1 改成了bind 0.0.0.0。
然后要配置防火墙 开放端口6379
```
