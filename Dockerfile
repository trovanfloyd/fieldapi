FROM openjdk:16-jdk-slim

MAINTAINER fernando.freitas

WORKDIR /usr/local/fielddataapi/

COPY ./target/fielddataapi-*.jar fielddataapi.jar

EXPOSE 8080
#EXPOSE 9999

ENTRYPOINT ["/bin/bash", "-c", "java $JAVA_OPTS -jar fielddataapi.jar"]