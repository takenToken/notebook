# 浏览器消息推送
* 后端给用户推送消息，业务场景包括：实时公告、异步导出通知、私发消息等。
* 技术：
    * WebSocket 是一种网络通信协议,是HTML5 开始提供的一种在单个 TCP 连接上进行全双工通讯的协议。
    * socket.io优点：
        * 兼容各个浏览器、支持多种协议，根据浏览器支持方式不同自动选择支持协议,开发无需关系浏览器兼容问题
        * 最优先使用websocket-->xdr-streaming-->xhr-streaming-->eventsource等。
    * 前端：前端socket.io库实现应用长连接
    * 后端：
        * 基于undertow服务器 + spring-weboscket + socket.io实现长连接.
        * spring-cloud-stream + kafka 实现应用之间消息处理。
