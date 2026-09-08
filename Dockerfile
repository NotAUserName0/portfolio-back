# COMPILATION AND BUILD INSTRUCTIONS FOR DOCKER IMAGE
FROM eclipse-temurin:21-jdk-alpine AS build
# DEFINE WORKING DIRECTORY
WORKDIR /app
# COPY CONFIG FILES
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# DOWNLOAD DEPENDENCIES
RUN ./mvnw dependency:go-offline -B

# COPY SOURCE FILES
COPY src ./src

# BUILD THE APPLICATION
RUN ./mvnw clean package -DskipTests

# BUILD EXEUTION
FROM eclipse-temurin:21-jre-alpine AS execution
WORKDIR /app

# CREATE IMAGE REPOSITORY
RUN mkdir -p /app/upload

# JAR COPY
COPY --from=build /app/target/*.jar app.jar

# EXPOSE PORT
EXPOSE 8081

# EXECUTE APPLICATION
ENTRYPOINT ["java","-jar","app.jar"]
