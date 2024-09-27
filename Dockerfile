# Etapa 1: Construir la aplicación
FROM gradle:7.5.1-jdk17 AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar todos los archivos del proyecto al contenedor
COPY --chown=gradle:gradle . .

# Ejecutar el build con Gradle para compilar el proyecto
RUN gradle build --no-daemon

# Etapa 2: Crear la imagen final
FROM eclipse-temurin:17-jdk-jammy

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado desde la etapa de construcción
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto que utilizará el contenedor
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
