# 先打包 出来 chat2db-server/chat2db-server-start/target/chat2db-server-start.jar
# 打包
docker build -t chat2db/chat2db:test -f docker/Dockerfile .