FROM maven:3.9-eclipse-temurin-8 AS spigot-builder

WORKDIR /spigot
# Download BuildTools
RUN wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
# Build Spigot 1.8.8
RUN java -jar BuildTools.jar --rev 1.8.8

FROM eclipse-temurin:8-jdk AS paper-builder

WORKDIR /paper

RUN wget -O spigot.jar https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar
# Will fail because of missing eula.txt
RUN java -jar spigot.jar || true

FROM eclipse-temurin:8-jdk AS plugin-builder

WORKDIR /build
COPY --from=spigot-builder /root/.m2/repository/org/spigotmc/ /root/.m2/repository/org/spigotmc/

COPY gradle/ ./gradle/
COPY settings.gradle.kts build.gradle.kts ./
COPY gradlew ./
COPY anti-reduce-agent/build.gradle.kts anti-reduce-agent/
COPY bedwars/build.gradle.kts bedwars/

RUN ./gradlew --no-daemon

# Copy plugin source
COPY . .
# build shadow jar
RUN ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:8-jre-alpine

EXPOSE 25565
WORKDIR /server
ARG ONLINE_MODE=true

# Copy ssl certificates
COPY --from=spigot-builder /etc/ssl/certs/ /etc/ssl/certs/
# Copy server jars
COPY --from=paper-builder /paper/spigot.jar spigot.jar
COPY --from=paper-builder /paper/cache/ cache/

COPY server-bin/ ./
RUN echo "online-mode=${ONLINE_MODE}" >> server.properties

COPY --from=plugin-builder /build/bedwars/build/libs/*-all.jar plugins/rewibw.jar
COPY --from=plugin-builder /build/anti-reduce-agent/build/libs/*-all.jar agent.jar

ENTRYPOINT ["/bin/sh", "start.sh"]