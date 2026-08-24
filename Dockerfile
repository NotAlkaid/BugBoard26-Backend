# Stage 1: Build dell'applicazione con JDK 21 e Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia dei file di build per sfruttare la cache dei layer Docker
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Permessi di esecuzione per il wrapper Maven
RUN chmod +x mvnw

# Download delle dipendenze per velocizzare build successive
RUN ./mvnw dependency:go-offline -B

# Copia del codice sorgente
COPY src ./src

# Compilazione del JAR (senza rieseguire i test durante la build dell'immagine)
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime leggero con solo JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creazione di un gruppo e un utente di sistema non-root per sicurezza (Least Privilege)
RUN addgroup -S spring && adduser -S spring -G spring

# Copia dell'eseguibile JAR prodotto nello stage di build assegnandone la proprietà all'utente non-root
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Cambio all'utente non-root per l'esecuzione
USER spring:spring

# Porta su cui risponde l'applicazione Spring Boot
EXPOSE 8080

# Comando di avvio
ENTRYPOINT ["java", "-jar", "app.jar"]