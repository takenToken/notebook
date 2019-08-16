## nginx转发服务忽略http header的问题
* nginx针对自定义http头部带下划线进行忽略
    - 例如：Header app_id = dop_id, tf_token=dxsdsds 进行忽略
* nginx另外一种方式放行http头部下划线的方式
    - 设置nginx.conf的 **underscores_in_headers on;** 开启支持下划线的Http Header
    - 注意：docker版本nginx更改nginx.conf需要重启容器才生效，include的Reload即可
```config

user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;


events {
    worker_connections  65535;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;
    proxy_buffer_size 512k;
    proxy_buffers   32 512k;
    proxy_busy_buffers_size 512k;
    client_body_buffer_size 50m;
    gzip  on;
    gzip_comp_level  3;    # 压缩比例，比例越大，压缩时间越长。默认是1
    gzip_types    text/xml text/plain text/css application/javascript application/x-javascript application/rss+xml;      #哪些文件可以被压缩
    gzip_disable    "MSIE [1-6]\.";     # IE6无效

    underscores_in_headers on;
    include /etc/nginx/conf.d/*.conf;
}
```

* 建议采用中划线代替下划线方案，自然解决忽略问题
