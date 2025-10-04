FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Tüm dosyaları kopyala
COPY . .

# Maven wrapper'ı çalıştırılabilir yap
RUN chmod +x mvnw

# Uygulamayı derle
RUN ./mvnw clean package -DskipTests -B

# --- Runtime image ---
FROM eclipse-temurin:17-jre

WORKDIR /app

# Jar'ı builder aşamasından kopyala
COPY --from=builder /app/target/ecom-stock-sync-0.0.1-SNAPSHOT.jar app.jar

# Start script'i kopyala ve çalıştırılabilir yap
COPY start.sh .
RUN chmod +x start.sh

ENV PORT=8080
EXPOSE 8080

CMD ["./start.sh"]