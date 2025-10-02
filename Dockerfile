FROM gradle:7.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar  /app/bank-account.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/bank-account.jar"]