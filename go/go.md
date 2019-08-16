## 学习文档
[go学习文档](https://github.com/astaxie/build-web-application-with-golang/blob/master/zh/preface.md)
[beego学习文档](https://beego.me)
[awesome-go](https://github.com/hackstoic/golang-open-source-projects)
[bookstack](https://www.bookstack.cn/)

## 重点知识
### panic /recover
* 已知异常尽量使用error, 少用panic



### channel \ select
* select处理channel的事情
* select表达式原则自上而下，从左到右

```go
package channel

import (
	"fmt"
	"math/rand"
	"time"
)

func generator() chan int {
	out := make(chan int)
	go func() {
		i := 0
		for {
			time.Sleep(time.Duration(rand.Intn(1500)) * time.Millisecond)
			out <- 'a' + i
			i++
		}
	}()
	return out
}

func work(i int, work chan int) {
	for v := range work {
		time.Sleep(time.Second)
		fmt.Printf("workIndex: %d msg: %c \n", i, v)
	}
}

func CreateSelectWorker(i int) chan<- int {
	c := make(chan int)
	go work(i, c)
	return c
}

func DemoSelect() {
	//send to channel
	c1, c2 := generator(), generator()
	work := CreateSelectWorker(0)

	var cache []int
	tm := time.After(10 * time.Second)
	tick := time.Tick(time.Second)
	for {
		var activeChan chan<- int
		var activeValue int
		if len(cache) > 0 {
			activeChan = work
			activeValue = cache[0]
		}

		select {
		case v := <-c1:
			cache = append(cache, v)
		case v := <-c2:
			cache = append(cache, v)
		case activeChan <- activeValue:
			cache = cache[1:]
		case <-tick:
			//每隔一秒打印缓存长度
			fmt.Printf("cache len:%d\n", len(cache))
		case <-tm:
			//停止程序
			return
		}
	}
}
```

### 异常处理
* go的异常处理通过panic()、recover()、error接口组成
    - ==panic()== 严重异常，会导致进程直接挂掉.
    - ==recover()== 会再panic中断执行的时候恢复回来,避免进程挂掉
    - ==error== 则是异常接口处理已知异常,调用者处理异常. 例如： 打开文件不存在，查询SQL异常等。
* ==pain()== 是中断当前正在执行函数或方法，然后向上层回退回退，直到最顶层调用者，然后中断进程.
* ==recover()==结合==defer==关键字来恢复

```go
func Test(){

    fmt.Print("testing ...")

    //恢复异常
    defer RecoverError()

    fmt.Print("Open File ...")

    //调用出现异常
    TestAdd()
}

func TestAdd(){
    fmt.Print("Add Error")
    panic()
}

func RecoverError(){
    if err := recover(); err != nil {
    	if err == ErrAbort {
    		return
    	}
    }
}
```

* 关于**defer**损耗情况可参考如下文章
    - [Go defer性能损耗](http://148.70.135.158/article/10)


### 匿名结构
* 特点
    - 继承匿名函数所有属性和方法
    - 如果当前struct结构有相同的属性、方法，则会覆盖基类属性、方法, 除非通过this.base结构访问属性、方法，则是访问基类属性、方法
* 自定义的类型、内置类型、结构都可以作为匿名字段

```go
package base

import "fmt"
//user.go
type baseUser struct {
	Id         int64
	UserName   string
	PassWord   string
	salt       string
	createTime string
}

func (this *baseUser) GetName() string {
	fmt.Println("base UserName")
	return "base UserName"
}

// wxuser.go
package base

import "fmt"

type WxUser struct {
	baseUser
	WxName string
	WxIcon string
	//覆盖baseUser里属性
	PassWord string
}

func (this *WxUser) GetName() string {
	fmt.Println("current user Name")
	return this.UserName
}

//wxuser_test.go
func TestWxUser_GetName(t *testing.T) {
	user := &WxUser{PassWord: "current Password"}
	user.GetName()
	user.baseUser.GetName()

	fmt.Println(user.PassWord)
	fmt.Println("base->" + user.baseUser.PassWord)
}
```


### 面向对象
### 空接口
### 反射
### 并发
### web框架beego/gin

## go依赖管理
### go版本是v1.11.x版本,如果是v1.12.x是默认开启
* 请设置环境变量

```bash
# 启用模块管理
export GO111MODULE=on
source .bash_profile
```

* 模块操作
```bash
#启用了 module 机制的包（库）或者可执行文件，它们的代码都必需放在 非GOPATH 的目录里面，这是必需条件，不是可选的条件。 如果对 GOPATH 目录里面的项目 执行 go mod init mod 那么将会报错： go: modules disabled inside GOPATH/src by GO111MODULE=auto; see 'go help modules'

# 模块初始化,在目录下创建mod.go
go mod init blog

# 会自动下载依赖
go build
```

* 如果使用go模块化，你会发现使用`go get -u xxx.com/bat`下载编译命令是不能使用的,你需要如下操作才行.
```bash
# 编译后文件存放在go/bin目录下
# mac/linux 在.bash_profile增加$GOPATH/bin
export PATH=$PATH:$M2_HOME/bin:$MYSQL_HOME/bin:$DOCKER_HOST:$GOPATH/bin
```


* 如果你是用go mod download下载了依赖包之后，就可以在$GOPATH/pkg/mod/下发现和之前$GOPATH/src类似的目录结构，并且包路径上都包含了版本号
* 默认情况下，Go 不会自己更新模块，这是一个好事因为我们希望我们的构建是有可预见性（predictability）的。如果每次依赖的包一有更新发布，Go 的 module 就自动更新，那么我们宁愿回到 Go v1.11 之前没有 Go module 的荒莽时代了。所以，我们需要更新 module 的话，我们要显式地告诉 Go。
* 运行 go get -u 将会升级到最新的次要版本或者修订版本（比如说，将会从 1.0.0 版本，升级到——举个例子——1.0.1 版本，或者 1.1.0 版本，如果 1.1.0 版本存在的话）
运行 go get -u=patch 将会升级到最新的修订版本（比如说，将会升级到 1.0.1 版本，但不会升级到 1.1.0 版本）
运行 go get package@version 将会升级到指定的版本号（比如说，github.com/robteix/testmod@v1.0.1）
(译注：语义化版本号规范把版本号如 v1.2.3 中的 1 定义为主要版本号，2 为次要版本号，3 为修订版本号 )
上述列举的情况，似乎没有提到如何更新到最新的主要版本的方法。这么做是有原因的，我们之后会说到。
因为我们的程序使用的是包 1.0.0 的版本，并且我们刚刚创建了 1.0.1 版本，下面任意一条命令都可以让我们程序使用的包更新到 1.0.1 版本：

```bash
$ go get -u
$ go get -u=patch
$ go get github.com/robteix/testmod@v1.0.1
```


### Vendor机制
* Go modules 会忽略 vendor/ 目录

```bash
#添加 vendor 机制管理依赖
go mod vendor
#go build 默认还是会忽略这个目录的内容，如果你想要构建的时候从 vendor/ 目录中获取依赖的代码来构建，那么你需要明确的指示
go build -mod vendor

#https://www.cnblogs.com/f-ck-need-u/
#http://xiaorui.cc/
```
