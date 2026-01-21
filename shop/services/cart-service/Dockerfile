FROM maven:3.9.4-eclipse-temurin-17 AS build
ARG SERVICE_PATH
WORKDIR /work

COPY .mvn/settings.xml /root/.m2/settings.xml
COPY . /work

RUN mvn -B -e -f /work/pom.xml -pl ${SERVICE_PATH} -am -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.httpconnectionManager.ttl=60 \
    -Dmaven.wagon.http.pool=false \
    clean package

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
ARG SERVICE_PATH
COPY --from=build /work/${SERVICE_PATH}/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
