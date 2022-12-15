FROM gradle:7.5-alpine AS build
COPY . /home/gradle
RUN gradle build && gradle bootJar

FROM openjdk:19-jdk-alpine
WORKDIR /app
COPY --from=build /home/gradle/build/libs/popay-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java", "-jar", "popay-0.0.1-SNAPSHOT.jar"]
