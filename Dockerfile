FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Bağımlılık cache'i için önce sadece pom'u kopyala
COPY pom.xml ./
RUN mvn -q -e -B dependency:go-offline

# Kaynak kodunu kopyala ve derle
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# --- Runtime image ---
FROM eclipse-temurin:17-jre

WORKDIR /app

# Jar'ı builder aşamasından kopyala
COPY --from=builder /app/target/ecom-stock-sync-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["sh", "-c", "java -jar app.jar --spring.profiles.active=railway --server.port=${PORT}"]