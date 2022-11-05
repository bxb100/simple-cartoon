FROM openjdk:19-jdk-slim AS builder

WORKDIR /app

COPY . .

RUN ./mvnw package -DskipTests

FROM openjdk:19-jdk-slim AS runner

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE docker

ENTRYPOINT ["java", "-jar", "app.jar"]
