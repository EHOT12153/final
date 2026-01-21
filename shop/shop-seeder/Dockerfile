# ======== BUILD STAGE ========
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Копируем pom и src ИМЕННО для этого модуля
COPY pom.xml .
COPY src ./src

# Собираем jar-файл
RUN mvn -q -DskipTests package


# ======== RUNTIME STAGE ========
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем jar из build stage
COPY --from=build /app/target/*.jar app.jar

# Запуск
ENTRYPOINT ["java", "-jar", "app.jar"]
