> * 创建日期: 2018-04-13
> * 版本号: 0.9


## [Git](https://git-scm.com/book/zh/v2)(分布式版本控制系统)
### 介绍

* **git**是一个开源的分布式版本控制系统，可以有效、高速的处理从很小到非常大的项目版本管理
* **git**是Linus Torvalds(*Linux创始人,大神级人物*)为了帮助管理Linux内核开发而开发的一个开放源码的版本控制软件。

### 特点
* 分布式相比于集中式的最大区别在于开发者可以提交到本地，每个开发者通过克隆（git clone），在本地机器上拷贝一个完整的Git仓库
* 开发者角度
    * 从服务器上克隆完整的Git仓库（包括代码和版本信息）到单机上
    * 在自己的机器上根据不同的开发目的，创建分支，修改代码
    * 在单机上自己创建的分支上提交代码
    * 在单机上合并分支
    * 把服务器上最新版的代码fetch下来，然后跟自己的主分支合并
    * 生成补丁（patch），把补丁发送给主开发者
    * 一般开发者之间解决冲突的方法，开发者之间可以使用pull 命令解决冲突，解决完冲突之后再向主开发者提交补丁

### 介绍OSX下git安装，其它自行搜索教程
> 记录: OSX下建议安装[brew](https://www.cnblogs.com/kccdzz/p/7676840.html)软件包管理工具,非常方便,免去了自己手动编译安装的不便

```
// 仓库查询git
brew search git

// 安装git
brew install git

如果拉去代码git@git.xx.cn:xx-common 出现bad permissions
则请给文件授权 chmod 700 id_rsa id_rsa.pub


// 设置全局属性
git config --global user.name XXX
git config --gloabl user.email XXX@xxx.com
git config --list

```

## gitflow工作流
### 介绍
* Git Flow是一套基于git的工作流程，这个工作流程围绕着project的发布(release)定义了一个严格的如何建立分支的模型
* Git建分支是非常cheap的，我们可以任意建立分支，对任意分支再分支，分支开发完后再合并
* 比较推荐、多见的做法是特性驱动(Feature Driven)的建立分支法(Feature Branch Workflow)

### 功能
* 分支: **所有操作基于主分支进行**
    * 主分支： develop[开发] 、master[生产]
    * 功能分支: feature[允许多个]
    * 发版分支: release[只允许一个]
    * 补丁分支: hotfix[紧急修复生产环境bug]
* **develop**分支是重要的开发分支，功能、发版都基于它来实现的，一般情况下不会直接在develop分支上编写代码
* **master**生产分支，与生产环境应用程序保持一致，主要用于修复紧急bug

* 操作流程
    * 创建feature[功能分支]分支：以develop分支为蓝本创建独立feature分支，开发完成代码回流到develop分支,并删除当前feature分支
    * 创建release[发版]分支: 以develop分支为蓝本创建release分支,测试发布完成则把代码合并到develop和master分支，并打上Tag标签,删除当前发版分支。注意: ==当前只能有一个release分支==
    * 创建hotfix[修复补丁]分支：以master分支为蓝本创建hotfix分支,测试发布完成后则把代码合并到develop和master分支，并打上Tag标签，删除当前发版分支。

![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/E57D08FD1D004583B02757A418EF469D/7772)

### OSX安装使用git flow

```
//安装
brew install git-flow
//初始化
git flow init
//创建完成feature分支
git flow feature start 20180413-0001
git flow feature finish 20180413-0001
git flow feature publish 20180413-0001
git flow feature pull origin 20180413-0001

//创建完成release分支
git flow release start v3.1.0
git flow release publish v3.1.0
git flow release finish v3.1.0

//创建完成hotfix分支
git flow hotfix start hotfix-20180413-001
git flow hotfix finish hotfix-20180413-001
```

* git flow命令说明
![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/3C90686491C446929B6A1C64CDC0115F/7932)


## SourceTree工具
* SourceTree 是 Windows 和Mac OS X 下免费的Git和Hg客户端管理工具，同时也是Mercurial和Subversion版本控制系统工具。支持创建、克隆、提交、push、pull 和合并等操作
* [SourceTree下载](https://www.sourcetreeapp.com/)
* [SourceTree使用教程](https://www.cnblogs.com/tian-xie/p/6264104.html)
* SourceTree账号注册需要连接国外服务器，需要通过vpn才能注册成功，OSX可以去appStore下载VPN Plus软件

## [gitlab安装使用](https://about.gitlab.com/installation/)

### 介绍
* GitLab 是一个用于仓库管理系统的开源项目，使用Git作为代码管理工具，并在此基础上搭建起来的web服务。
* 优点
    * 版本高、稳定、bug少、功能丰富
    * 整体类似于github网站,熟悉的人可很方便的使用
    * web化管理项目，非常方便查看搜索项目，对于感兴趣的项目也可fork下来仔细学习改造
    * 方便管理人员、建组、建项目以及项目状况
* 小团队也可选[gogs](https://github.com/gogits/gogs)简单简洁要求低

### 安装
* gitlab可以直接参考官方网址，针对不同的操作系统安装说明。
* 安装最简单可以采用docker化安装gitlab，方便简单，前提你本机已搭建docker环境。

```
//docker命令下载gitlab
docker pull gitlab/gitlab-ce

说明: https://docs.gitlab.com/omnibus/docker/

//启动命令
//注意别用默认目录，可能由于权限问题导致gitlab无法正常启动
sudo docker run --detach \
    --hostname gitlab.example.com \
    --publish 443:443 --publish 80:80 --publish 22:22 \
    --name gitlab \
    --restart always \
    --volume /Users/qiangliu/Documents/docker/gitlab:/etc/gitlab \
    --volume /Users/qiangliu/Documents/docker/gitlab:/var/log/gitlab \
    --volume /Users/qiangliu/Documents/docker/gitlab:/var/opt/gitlab \
    gitlab/gitlab-ce:latest

//设置hostname
127.0.0.1 gitlab.example.com

访问gitlab gitlab.example.com 并修改密码

//进入修改gitlab配置参数,例如: 限制个人创建仓库数量等
sudo docker exec -it gitlab vi /etc/gitlab/gitlab.rb

//参考配置
https://docs.gitlab.com/omnibus/settings/configuration.html
```


## 题外: [gogs](https://github.com/gogits/gogs)
* Gogs 的目标是打造一个最简单、最快速和最轻松的方式搭建自助 Git 服务。
* 使用 Go 语言开发使得 Gogs 能够通过独立的二进制分发，并且支持 Go 语言支持的 所有平台，包括 Linux、Mac OS X、Windows 以及 ARM 平台


* [docker安装](https://github.com/gogits/gogs/tree/master/docker)
```
# Pull image from Docker Hub.
$ docker pull gogs/gogs

# Create local directory for volume.
$ mkdir -p /var/gogs

# Use `docker run` for the first time.
$ docker run --name=gogs -p 10022:22 -p 10080:3000 -v /var/gogs:/data gogs/gogs

# Use `docker start` if you have stopped it.
$ docker start gogs
```

## 流程规范
### 开发版本号规范
* 例子: 3.1.0.[SNAPSHOT  |  RC  | RELEASE | PATH]
* **3.1.0** 代表< 主版本号 >  ------   < 次版本号 > ------ < 增量版本号 >
* **[SNAPSHOT  |  RC  | RELEASE | PATH]** 代表不同情况使用
    * SNAPSHOT是develop、feature分支使用版本号
    * RC是release分支使用的版本号
    * RELEASE是master分支使用的版本号
    * PATH是hotfix分支使用的版本号

* 开发环境自测
    * 自测使用**feature**分支发布到开发环境，相关开发人员自行测试
    * 版本号为: 3.1.0-SNAPSHOT

* 测试环境整体测试
    * 转测使用**release**分支发布到测试环境，测试人员针对本次发版内容进行整理测试
    * ==当前只有一个release分支,不允许同时存在多个release分支==
    * ==release分支以develop和master分支合并后代码为基准进行测试==
    * 版本号为: 3.1.0.RC1  、3.1.0.RC2 、3.1.0.RC3

* 生产环境发布
    * **master**分支发布到生产环境
    * 版本号为: 3.1.0.RELEASE

* 紧急bug修复
    * **hotfix**分支
    * 版本号为: 3.1.0.PATH 、3.1.0.PATH2、3.1.0.PATH3


### feature分支规范
* 例子：feature/20180416-V3.1.0-新增记账单
* feature命名由：固定目录 + 日期 + 版本号 + 功能描述
* feature分支版本号由对应组长负责进行定义

![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/14296FE059F64900822D515BB3877D11/7982)

### release分支规范
* 例子: release/V1.1.0
* release命名由：固定目录 + 版本号


### hotfix分支规范
* 例子: hotfix/20180417-V3.1.0
* hotfix命名由：固定目录 + 日期 + 版本号


## 参考资料
* [Git Flow - A successful branching model](http://nvie.com/posts/a-successful-git-branching-model/)
* [Git Flow of Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows#)
* [sourceTree](https://www.sourcetreeapp.com/)
