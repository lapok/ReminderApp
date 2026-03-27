# Этап сборки
FROM gradle:8.14-jdk17 AS build
WORKDIR /app

# Копируем только конфиги для кеширования зависимостей
COPY build.gradle settings.gradle ./
RUN gradle build -x test --no-daemon || true

# Копируем исходники и собираем
COPY src ./src
RUN gradle clean build -x test --no-daemon

# Этап запуска
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]