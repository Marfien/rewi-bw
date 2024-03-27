FROM eclipse-temurin:8-jdk AS paper-builder

WORKDIR /paper

RUN wget -O spigot.jar https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar
# Will fail because of missing eula.txt
RUN java -jar spigot.jar || true

FROM alpine:3

# Install jre
RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk
RUN echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories
RUN apk add --no-cache temurin-8-jdk

EXPOSE 25565
WORKDIR /server
ARG ONLINE_MODE=true

# Copy server jars
COPY --from=paper-builder /paper/spigot.jar spigot.jar
COPY --from=paper-builder /paper/cache/ cache/

COPY . .
RUN echo "online-mode=${ONLINE_MODE}" >> server.properties

ENTRYPOINT ["/bin/sh", "start.sh"]