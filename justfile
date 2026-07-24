set lazy

build:
    ./gradlew shadowJar

clean-build:
    ./gradlew clean shadowJar

bake:
    docker bake --file ./docker-bake.hcl --file ./docker-bake-dev.hcl

build-bake: build bake

run-server:
    docker compose up rewi-bw-server

run-setup-tool:
    docker compose up rewi-bw-setup-tool

stop-all:
    docker compose down

get-spigot:
    #!/bin/sh
    TMP_DIR=$(mktemp -d)
    cd "$TMP_DIR"
    curl -o BuildTools.jar 'https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar'
    java -jar BuildTools.jar --rev 1.8.8
