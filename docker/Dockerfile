# 基于jdk17的镜像
FROM openjdk:17
# 定义进入容器时默认位置，接下来后续操作的工作位置
WORKDIR /app
# 将当前目录下的jar包复制到docker容器的/app 目录下
ADD chat2db-server/chat2db-server-start/target/chat2db-server-start.jar chat2db-server-start.jar
# 将当前目录下的lib包复制到docker容器的/app/lib 目录下
ADD chat2db-server/chat2db-server-start/target/lib lib
# 让当前容器暴露10824
EXPOSE 10824
# 运行jar包
ENTRYPOINT ["java","-Dloader.path=lib","-Dspring.profiles.active=release","-jar","chat2db-server-start.jar"]
