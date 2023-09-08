# Use a base image, like AdoptOpenJDK or OpenJDK, depending on your project's requirements
FROM eclipse-temurin:11-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target folder into the container's /app folder
COPY target/server.jar /app/server.jar

COPY appconfig/application.yml /app/application.yml

# Copy the application.yml file from the docker folder into the container's /app folder
#COPY docker/application.yml /app/

# Set the command to run when the container starts
CMD ["java", "-jar", "-D'file.encoding=UTF-8'", "/app/server.jar"]