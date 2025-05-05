FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
RUN ./gradlew build -x test
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/cepapp.jar ./cepapp.jar
EXPOSE 8080
CMD ["java", "-jar", "cepapp.jar"]
