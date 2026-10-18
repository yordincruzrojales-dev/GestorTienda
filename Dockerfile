# Etapa 1: Compilación
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar archivos de Gradle y código fuente
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Dar permisos de ejecución a gradlew y compilar saltando los tests (para ir directo)
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el ejecutable generado en la etapa de compilación
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer el puerto por el que escucha Spring Boot
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]