FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Maven wrapper ve pom.xml'i kopyala
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Maven wrapper'ı çalıştırılabilir yap
RUN chmod +x mvnw

# Bağımlılıkları indir
RUN ./mvnw dependency:go-offline -B

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