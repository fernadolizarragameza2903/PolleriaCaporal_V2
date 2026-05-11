# Fase de compilación
FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Fase de ejecución
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/polleria-caporal-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
