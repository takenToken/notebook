# JWT介绍
* JWT(json web token)是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准
* 声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，比如用在用户登录。

# 基于session的登录认证
* 在传统的用户登录认证中，因为http是无状态的，所以都是采用session方式。用户登录成功，服务端会保证一个session，当然会给客户端一个sessionId，客户端会把sessionId保存在cookie中，每次请求都会携带这个sessionId。
* cookie+session这种模式通常是保存在内存中，而且服务从单服务到多服务会面临的session共享问题，随着用户量的增多，开销就会越大。而JWT不是这样的，只需要服务端生成token，客户端保存这个token，每次请求携带这个token，服务端认证解析就可

# JWT的构成部分
* **头部Header**
    * 声明类型，这里是jwt
    * 声明加密的算法通常直接使用HMAC SHA256
    * 头部进行base64加密(对称加密)

```json
header = '{"alg":"HS256","typ":"JWT"}'
payload = '{"loggedInAs":"admin","iat":1422779638}'//iat表示令牌生成的时间
key = 'secretkey'
unsignedToken = encodeBase64(header) + '.' + encodeBase64(payload)
signature = HMAC-SHA256(key, unsignedToken)


token = encodeBase64(header) + '.' + encodeBase64(payload) + '.' + encodeBase64(signature)
# token看起来像这样: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2Mzh9.gzSraSYS8EXBxLN_oWnFSRgCzcmJmMjLiuyu5CSpyHI
```

* 载荷playload,存放自定义数据地方
    * 标准中注册的声明
    * 公共的声明
    * 私有的声明
* 标准中注册的声明 (建议但不强制使用)
    * iss: jwt签发者
    * sub: jwt所面向的用户
    * aud: 接收jwt的一方
    * exp: jwt的过期时间，这个过期时间必须要大于签发时间
    * nbf: 定义在什么时间之前，该jwt都是不可用的
    * iat: jwt的签发时间
    * jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击
* 公共的声明
    * 公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密
* 私有的声明
    * 私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息
* **signature** jwt的第三部分是一个签证信息，这个签证信息由三部分组成
    * header (base64后的)
    * payload (base64后的)
    * secret
    * 这个部分需要base64加密后的header和base64加密后的payload使用.连接组成的字符串，然后通过header中声明的加密方式进行加盐secret组合加密，然后就构成了jwt的第三部分
    * 密钥secret是保存在服务端的，服务端会根据这个密钥进行生成token和验证，所以需要保护好


# 问题
* 加密长度限制

```java
如果密钥大于128, 会抛出上述异常。因为密钥长度是受限制的, java运行时环境读到的是受限的policy文件，文件位于/jre/lib/security下, 这种限制是因为美国对软件出口的控制

用AES加密时出现"java.security.InvalidKeyException: Illegal key size"异常

将下面链接中的jar包下载下来，替换jdk 与jre下两个jar包：local_policy.jar和US_export_policy.jar即可。
jdk对应jar包的路径：D:\Java\jdk1.7.0_25\jre\lib\security
jre对应jar包的路径：D:\Java\jre7\lib\security

JDK7的下载地址: http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
JDK8的下载地址: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
```
