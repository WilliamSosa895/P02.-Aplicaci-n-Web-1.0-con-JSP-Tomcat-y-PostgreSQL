FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /src
COPY . .
RUN mvn -q test package
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /src/target/api-spring-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
