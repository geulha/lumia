# Estágio 1: Build da aplicação com Maven e JDK 21
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia arquivos do projeto e compila o executável
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final enxuta com JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o jar compilado
COPY --from=build /app/target/lumia-portal-1.0.0-SNAPSHOT.jar app.jar

# Cria diretório para uploads de anexos
RUN mkdir -p /app/uploads

# Porta padrão (Render injeta dinamicamente via variável $PORT)
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
