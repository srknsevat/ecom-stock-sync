FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Tüm dosyaları kopyala
COPY . .

# Bağımlılıkları indir ve uygulamayı derle
RUN mvn clean package -DskipTests -B

# Jar'ı çalıştırılabilir hale getir
RUN mv target/ecom-stock-sync-0.0.1-SNAPSHOT.jar app.jar

# Start script'i çalıştırılabilir yap
RUN chmod +x start.sh

ENV PORT=8080
EXPOSE 8080

CMD ["./start.sh"]