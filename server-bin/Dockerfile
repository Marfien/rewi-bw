FROM base-image

EXPOSE 25565
EXPOSE 5005
EXPOSE 9010
WORKDIR /server

# Install libgcc when running in alpine (for ssl certificates)
RUN apk add --no-cache libgcc || true
RUN wget -O spigot.jar https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar
# Precreate patched jars to speed up server startup
RUN java -Dpaperclip.patchonly=true -jar spigot.jar

COPY . .

ENTRYPOINT ["/bin/sh", "start.sh"]