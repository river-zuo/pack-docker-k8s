# 基于java镜像创建新镜像
FROM openjdk:8
# 作者
MAINTAINER rl
# 将jar包添加到容器中并更名为app.jar
ADD  target/pack-docker-1.0-SNAPSHOT.jar /root/docker_test/app.jar
# 运行jar包
ENTRYPOINT ["nohup","java","-jar","/root/docker_test/app.jar","&"]
