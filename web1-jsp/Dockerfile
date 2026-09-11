FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /src
COPY . .
RUN mvn -q test package
FROM tomcat:9.0-jdk11-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /src/target/web1.war /usr/local/tomcat/webapps/web1.war
