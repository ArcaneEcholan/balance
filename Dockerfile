# Use a base image, like AdoptOpenJDK or OpenJDK, depending on your project's requirements
FROM adoptopenjdk:11-jre-hotspot

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target folder into the container's /app folder
COPY target/server.jar /app/server.jar

# Copy the application.yml file from the docker folder into the container's /app folder
#COPY docker/application.yml /app/

# Copy the start.sh script from the docker folder into the container's /app folder
COPY docker/start.sh /app/

# Give execute permissions to the start.sh script
RUN chmod a+x /app/start.sh

# Set the command to run when the container starts
CMD ["/app/start.sh"]