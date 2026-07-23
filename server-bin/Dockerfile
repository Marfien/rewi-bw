FROM badouralix/curl-jq AS downloader

RUN <<EOF
DOWNLOAD_URL=$(curl 'https://fill.papermc.io/v3/projects/paper/versions/1.8.8/builds/445' -H 'accept: application/json' |
   jq -r '.downloads."server:default".url')
curl -o spigot.jar "$DOWNLOAD_URL"
EOF
      

FROM base-image

EXPOSE 25565
EXPOSE 5005
EXPOSE 9010
WORKDIR /server

# Install libgcc when running in alpine (for ssl certificates)
RUN apk add --no-cache libgcc || true
COPY --from=downloader spigot.jar .
# Precreate patched jars to speed up server startup
RUN java -Dpaperclip.patchonly=true -jar spigot.jar

COPY . .

ENTRYPOINT ["/bin/sh", "start.sh"]
