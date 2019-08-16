## go性能分析
### PProf
* runtime/pprof：采集程序（非 Server）的运行数据进行分析
    - pprof 是用于可视化和分析性能分析数据的工具
    - pprof 以 profile.proto 读取分析样本的集合，并生成报告以可视化并帮助分析数据（支持文本和图形报告）
    - 支持什么使用模式
        - Report generation：报告生成
        - Interactive terminal use：交互式终端使用
        - Web interface：Web 界面
* net/http/pprof：采集 HTTP Server 的运行时数据进行分析
* 可以做什么
    - CPU Profiling：CPU 分析，按照一定的频率采集所监听的应用程序 CPU（含寄存器）的使用情况，可确定应用程序在主动消耗 CPU 周期时花费时间的位置
    - Memory Profiling：内存分析，在应用程序进行堆分配时记录堆栈跟踪，用于监视当前和历史内存使用情况，以及检查内存泄漏
    - Block Profiling：阻塞分析，记录 goroutine 阻塞等待同步（包括定时器通道）的位置
    - Mutex Profiling：互斥锁分析，报告互斥锁的竞争情况

### 例子
* 访问路径：http://127.0.0.1:6060/debug/pprof/
* go tool pprof http://localhost:6060/debug/pprof/profile\?seconds\=60

```go
//关键代码是引入 _ "net/http/pprof"
import (
	"demo/data"
	"log"
	"net/http"
	_ "net/http/pprof"
	"time"
)

func main() {
	go func() {
		for {
			log.Print(data.Add("https://github.com/EDDYCJY"))
			time.Sleep(1 * time.Second)
		}
	}()

	http.ListenAndServe("0.0.0.0:6060", nil)
}

//  data/d.go
package data

var datas []string

//
func Add(str string) string {
	data := []byte(str)
	sData := string(data)
	datas = append(datas, sData)

	return sData
}

```

* cpu（CPU Profiling）: $HOST/debug/pprof/profile，默认进行 30s 的 CPU Profiling，得到一个分析用的 profile 文件
* block（Block Profiling）：$HOST/debug/pprof/block，查看导致阻塞同步的堆栈跟踪
* goroutine：$HOST/debug/pprof/goroutine，查看当前所有运行的 goroutines 堆栈跟踪
* heap（Memory Profiling）: $HOST/debug/pprof/heap，查看活动对象的内存分配情况
* mutex（Mutex Profiling）：$HOST/debug/pprof/mutex，查看导致互斥锁的竞争持有者的堆栈跟踪
* threadcreate：$HOST/debug/pprof/threadcreate，查看创建新OS线程的堆栈跟踪


* 生成prof： go test -bench=. -cpuprofile=cpu.prof

```bash
package data

import "testing"

const url = "https://github.com/EDDYCJY"

func TestAdd(t *testing.T) {
	s := Add(url)
	if s == "" {
		t.Errorf("Test.Add error!")
	}
}

func BenchmarkAdd(b *testing.B) {
	for i := 0; i < b.N; i++ {
		Add(url)
	}
}

```
### 可视化界面
* 生成的pprof 可以通过google的pprof或者python的graphviz查看
    - 缺少安装，出现错误：Failed to execute dot. Is Graphviz installed?
* 安装火焰图：
    - go get -u github.com/google/pprof
    - 查看: pprof -http=:8080 cpu.prof
* 查看排名： http://localhost:8080/ui/top
    - 线图线越粗消耗时间越长：http://localhost:8080/ui
    - 窥看：http://localhost:8080/ui/peek
    - 查看源码： http://localhost:8080/ui/source
