FROM openjdk:21-jdk-slim

WORKDIR /app
# Copy authentication service .jar
COPY build/libs/dm-service-1.0.0.jar  dm-service.jar

# Copy wait-for-it.sh script
COPY ./wait-for-it.sh  app/wait-for-it.sh

# Make wait-for-it executable
RUN chmod  +x  app/wait-for-it.sh

# Command to execute the JAR, but it will be overridden by entrypoint in docker-compose.yml
ENTRYPOINT ["java", "-jar", "dm-service.jar"]
