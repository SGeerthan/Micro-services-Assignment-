FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . /app
RUN ./mvnw -DskipTests package -q
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
