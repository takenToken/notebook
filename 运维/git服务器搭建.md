## git服务器搭建步骤
1. 安装git软件
2. 新增或者共享Linux服务器用户
3. 初始化git仓库
4. 客户端连接git仓库

### 安装git软件
```bash
yum -y install git
#查看版本
git --version
```

### 新建Linux用户
```bash
useradd 用户名
passwd 密码

```

### 初始化git仓库
```bash
#建立目录
mkdir /root/git/
#初始化仓库
git init --bare 仓库名.git

#授权
chown -R 用户组:用户名 仓库名.git/
```

### 客户端连接
```bash

git clone 用户名@ip地址:/目录/仓库名.git
#例子, 输入正常密码即可正常访问
git clone root@127.0.0.1:/root/git/blog/blog.git
```

### 注意
* ==git的clone源代码默认是linux的22端口， 如果自己远程修改过端口，请使用修改后端口==
