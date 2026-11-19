# ---- Etapa 1: compilar el JAR ejecutable (no hace falta tener Maven instalado) ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Etapa 2: imagen liviana de ejecución ----
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
