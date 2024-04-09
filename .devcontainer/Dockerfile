FROM maven:3.9-eclipse-temurin-8 AS spigot-maven-builder

WORKDIR /spigot
# Download BuildTools
RUN wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
# Build Spigot 1.8.8
RUN java -jar BuildTools.jar --rev 1.8.8 --generate-source

FROM mcr.microsoft.com/devcontainers/java:2-8-bookworm

COPY --from=spigot-maven-builder /root/.m2/repository/ /root/.m2/repository/