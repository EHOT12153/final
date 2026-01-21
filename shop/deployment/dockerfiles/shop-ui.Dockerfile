# ---------- Build stage ----------
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Сначала зависимости (ускоряет последующие сборки)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Потом исходники и сборка
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
# Если финальный артефакт имеет другое имя — поправь строку ниже
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
