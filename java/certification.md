## 认证机制
### HTTP Basic Auth
* HTTP Basic Auth简单点说明就是每次请求API时都提供用户的username和password，简言之，Basic Auth是配合RESTful API 使用的最简单的认证方式，只需提供用户名密码即可，但由于有把用户名密码暴露给第三方客户端的风险，在生产环境下被使用的越来越少。因此，在开发对外开放的RESTful API时，尽量避免采用HTTP Basic Auth


### OAuth
* OAuth（开放授权）是一个开放的授权标准，允许用户让第三方应用访问该用户在某一web服务上存储的私密的资源（如照片，视频，联系人列表），而无需将用户名和密码提供给第三方应用。

* OAuth允许用户提供一个令牌，而不是用户名和密码来访问他们存放在特定服务提供者的数据。每一个令牌授权一个特定的第三方系统（例如，视频编辑网站)在特定的时段（例如，接下来的2小时内）内访问特定的资源（例如仅仅是某一相册中的视频）。这样，OAuth让用户可以授权第三方网站访问他们存储在另外服务提供者的某些特定信息，而非所有内容

### Cookie Auth
* Cookie认证机制就是为一次请求认证在服务端创建一个Session对象，同时在客户端的浏览器端创建了一个Cookie对象；通过客户端带上来Cookie对象来与服务器端的session对象匹配来实现状态管理的。默认的，当我们关闭浏览器的时候，cookie会被删除。但可以通过修改cookie 的expire time使cookie在一定时间内有效；


### Token Auth
* Token Auth的优点
Token机制相对于Cookie机制又有什么好处呢？

* 支持跨域访问: Cookie是不允许垮域访问的，这一点对Token机制是不存在的，前提是传输的用户认证信息通过HTTP头传输.
无状态(也称：服务端可扩展行):Token机制在服务端不需要存储session信息，因为Token 自身包含了所有登录用户的信息，只需要在客户端的cookie或本地介质存储状态信息.
* 更适用CDN: 可以通过内容分发网络请求你服务端的所有资料（如：javascript，HTML,图片等），而你的服务端只要提供API即可.
* 去耦: 不需要绑定到一个特定的身份验证方案。Token可以在任何地方生成，只要在你的API被调用的时候，你可以进行Token生成调用即可.
* 更适用于移动应用: 当你的客户端是一个原生平台（iOS, Android，Windows 8等）时，Cookie是不被支持的（你需要通过Cookie容器进行处理），这时采用Token认证机制就会简单得多。
CSRF:因为不再依赖于Cookie，所以你就不需要考虑对CSRF（跨站请求伪造）的防范。
* 性能: 一次网络往返时间（通过数据库查询session信息）总比做一次HMACSHA256计算 的Token验证和解析要费时得多.
不需要为登录页面做特殊处理: 如果你使用Protractor 做功能测试的时候，不再需要为登录页面做特殊处理.
* 基于标准化:你的API可以采用标准化的 JSON Web Token (JWT). 这个标准已经存在多个后端库（.NET, Ruby, Java,Python, PHP）和多家公司的支持（如：Firebase,Google, Microsoft）.


### 基于JWT的Token认证机制实现
* JSON Web Token（JWT）是一个非常轻巧的规范。这个规范允许我们使用JWT在用户和服务器之间传递安全可靠的信息


### URL参数的sign签名认证
* 给客户端分配对应的key、secret
* Sign签名，调用API时需要对请求参数进行签名验证，签名方式如下：
* a.按照请求参数名称将所有请求参数按照字母先后顺序排序得到：keyvaluekeyvalue...keyvalue字符串如：将arong=1,mrong=2,crong=3排序为：arong=1,crong=3,mrong=2然后将参数名和参数值进行拼接得到参数字符串：arong1crong3mrong2。
* b.将secret加在参数字符串的头部后进行MD5加密,加密后的字符串需大写。即得到签名Sign
* 新api接口代码:
    * app调用：http://api.test.com/getproducts?key=app_key&sign=BCC7C71CF93F9CDBDB88671B701D8A35&参数1=value1&参数2=value2.
* secret 仅作加密使用,为了保证数据安全请不要在请求参数中使用。

#### 微信支付流程
* 重要节点第十步微信通知商户, 商户必须对通知结果进行安全校验,反正别人伪造。
* 特别提醒：服务端系统对于结果通知的内容一定要做签名验证。
* 参考地址：https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7&index=8

![image](https://note.youdao.com/yws/public/resource/cd8746ef85c2bd1dac3ca11ec681a27d/xmlnote/031B722576374101816270B666C2CD40/11366)
