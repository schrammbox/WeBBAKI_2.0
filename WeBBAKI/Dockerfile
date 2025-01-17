FROM ubuntu:latest

# Installiere Java und bereinige den Cache in einem Schritt
RUN apt update && apt install -y openjdk-17-jre-headless && \
    apt clean && rm -rf /var/lib/apt/lists/*

# Kopiere die JAR-Datei
COPY ./WeBBAKI/target/WeBBAKI-0.0.1-SNAPSHOT.jar /service.jar

# Definiere den Startbefehl
ENTRYPOINT ["java", "-jar", "/service.jar"]

# Dokumentiere den Port
EXPOSE 8080
