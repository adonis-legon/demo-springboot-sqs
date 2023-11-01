# Use maven to compile the java application.
FROM maven:3.9.3-eclipse-temurin-8-alpine AS build

# Set the working directory to /app
WORKDIR /workspace/app

# copy project content
COPY . ./

# Compile the application.
RUN mvn clean package -DskipTests -Dspring.profiles.active=docker -Pdocker

# Unpack the fatjar to create application layers
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Build runtime image.
FROM openjdk:8u191-jdk-alpine

# Install curl for application health check 
RUN apk --no-cache add curl

# Adds an user and group and setting it as current user
RUN addgroup -S app && adduser -S app -G app
USER app

VOLUME /tmp

# Copy the application layers
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Starts java app from the entrypoint
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.demospringbootsqs.DemoSpringbootSqsApplication"]