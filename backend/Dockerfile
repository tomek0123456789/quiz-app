FROM gradle AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN ./generate_keys
RUN ./gradlew build --no-daemon

FROM amazoncorretto:17-alpine3.17

EXPOSE 8080

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]

