
FROM eclipse-temurin:17-jre-noble

WORKDIR /app

COPY target/day1-demo-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
