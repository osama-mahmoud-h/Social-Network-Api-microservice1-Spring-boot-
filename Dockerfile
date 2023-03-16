FROM openjdk:11

WORKDIR /app/server

ADD ./target/server-rest-api.jar  .
EXPOSE 8080
# ["/bin/bash","-c"]

ENTRYPOINT ["java","-jar","server-rest-api.jar"]