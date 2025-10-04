FROM eclipse-temurin:17-jre

WORKDIR /app

# Maven wrapper'ı kopyala
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Bağımlılıkları indir
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Kaynak kodunu kopyala
COPY src ./src

# Uygulamayı derle
RUN ./mvnw clean package -DskipTests -B

# Jar'ı çalıştırılabilir hale getir
RUN mv target/ecom-stock-sync-0.0.1-SNAPSHOT.jar app.jar

# Start script'i kopyala ve çalıştırılabilir yap
COPY start.sh .
RUN chmod +x start.sh

ENV PORT=8080
EXPOSE 8080

CMD ["./start.sh"]