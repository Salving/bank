FROM eclipse-temurin:17-alpine

COPY target/bank-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]