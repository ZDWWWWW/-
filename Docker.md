# Docker

**搜索镜像**

```java
docker search 镜像名称:镜像TAG
```

**拉取镜像**

```java
docker pull 镜像NAME：镜像TAG
```

**查看所有镜像**

```java
docker images
```



**生成容器**

```java
docker run -d -p 8099:8080  --name my_tomcat_2 tomcat:8.5
```

-d：后台运行

--name：指定容器名

-p：端口映射 ，把容器中的端口（8080）映射到宿主机上相应的端口（8099）



**容器挂载本地目录**

```
docker run -d -p 80:80 -v /www:/www -v /config:/etc/nginx/sites-enabled  -v /logs:/var/log/nginx dockerfile/nginx
```

不支持创建后再挂载



**查看容器**

```java
docker ps
docker ps -a
```



**进入容器**

```
docker exec -it 容器ID /bin/bash
```

**容器复制本地文件**



**启动/停止/重启容器**

```
docker start 容器ID
docker stop 容器ID   OR   docker kill 容器ID
docker restart 容器ID
```



**删除容器/镜像**

```
docker rm 容器id

docker rmi 镜像id 
```

删除镜像前必须要删除其生成的所有容器