# Mirror based on jdk17
FROM openjdk:17
# Define the default location when entering the container and the working location for subsequent operations.
WORKDIR /app
# Copy the jar package in the current directory to the /app directory of the docker container
ADD chat2db-server/chat2db-server-web-start/target/chat2db-server-web-start.jar chat2db-server-web-start.jar
# Copy the lib package in the current directory to the /app/lib directory of the docker container
# ADD chat2db-server/chat2db-server-web-start/target/lib lib
# Let the current container expose 10824
EXPOSE 10824
# Run Jar
ENTRYPOINT ["java","-Dloader.path=lib","-Dspring.profiles.active=release","-jar","chat2db-server-web-start.jar"]
