FROM maven:3.9-eclipse-temurin-8 AS spigot-builder

WORKDIR /spigot
# Download BuildTools
RUN wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
# Build Spigot 1.8.8
RUN java -jar BuildTools.jar --rev 1.8.8

FROM eclipse-temurin:8-jdk AS plugin-builder

WORKDIR /plugin
COPY --from=spigot-builder /root/.m2/repository/org/spigotmc/ /root/.m2/repository/org/spigotmc/
# Copy plugin source
COPY . .
# build shadow jar
RUN ./gradlew shadowJar

FROM eclipse-temurin:8-jre-alpine

WORKDIR /server

COPY --from=spigot-builder /spigot/spigot-1.8.8.jar spigot.jar
COPY --from=plugin-builder /plugin/build/libs/*-all.jar plugins/rewibw.jar
COPY start.sh .

RUN echo "spawn-protection=0" > server.properties
RUN echo "allow-nether=false" >> server.properties
RUN echo "settings: { allow-end: false }" > bukkit.yml

ENTRYPOINT ["/bin/sh", "start.sh"]