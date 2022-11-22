FROM gradle:7.5-alpine AS build
WORKDIR /home/gradle/src
COPY . /home/gradle/src
RUN cd /home/gradle/src && gradle build && gradle bootJar

FROM openjdk:19-jdk-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/poPay-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java", "-jar", "poPay-0.0.1-SNAPSHOT.jar"]
