FROM scratch AS project-dir

FROM maven:3.9-eclipse-temurin-8 AS spigot-builder

WORKDIR /spigot
# Download BuildTools
RUN wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
# Build Spigot 1.8.8
RUN java -jar BuildTools.jar --rev 1.8.8

FROM eclipse-temurin:8-jdk AS plugin-builder

WORKDIR /build

COPY --from=project-dir gradle/ gradle/
COPY --from=project-dir gradlew settings.gradle.kts build.gradle.kts ./

RUN ./gradlew --no-daemon

COPY --from=spigot-builder /root/.m2/repository/org/spigotmc/ /root/.m2/repository/org/spigotmc/

# Copy plugin source
COPY --from=project-dir fakeentities fakeentities/
COPY --from=project-dir shared shared/
COPY . bedwars/

# build shadow jar
RUN ./gradlew bedwars:shadowJar --no-daemon

FROM anti-reduce-server

COPY --from=plugin-builder /build/bedwars/build/libs/*-all.jar plugins/rewibw.jar