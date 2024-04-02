FROM eclipse-temurin:8-jdk AS paper-builder

WORKDIR /paper

RUN wget -O spigot.jar https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar
# Will fail because of missing eula.txt
RUN java -jar spigot.jar || true

FROM eclipse-temurin:8-jre-alpine

EXPOSE 25565
EXPOSE 5005
EXPOSE 9010
WORKDIR /server
ARG ONLINE_MODE=true

# Copy server jars
COPY --from=paper-builder /paper/spigot.jar spigot.jar
COPY --from=paper-builder /paper/cache/ cache/

RUN if [ "$ONLINE_MODE" = 'true' ]; then apk add --no-cache libgcc; fi
COPY . .
RUN echo "online-mode=${ONLINE_MODE}" >> server.properties

ENTRYPOINT ["/bin/sh", "start.sh"]