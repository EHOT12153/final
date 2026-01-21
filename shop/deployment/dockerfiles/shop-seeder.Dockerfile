# ===== Сборка jar =====
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Если data-seeder — отдельный модуль со своим pom.xml и src/
# (без многомодульного родителя), то хватит так:
COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

# ===== Рантайм =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Скопировать собранный jar из стадии build
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
