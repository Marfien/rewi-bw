FROM eclipse-temurin:8-jre-alpine

EXPOSE 25565
EXPOSE 5005
EXPOSE 9010
WORKDIR /server

RUN wget -O spigot.jar https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar
# Precreate patched jars to speed up server startup
RUN java -Dpaperclip.patchonly=true -jar spigot.jar

ARG ONLINE_MODE=true
RUN if [ "$ONLINE_MODE" = 'true' ]; then apk add --no-cache libgcc; fi
COPY . .
RUN echo "online-mode=${ONLINE_MODE}" >> server.properties

ENTRYPOINT ["/bin/sh", "start.sh"]