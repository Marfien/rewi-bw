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

ENV GRALDE_USER_HOME=/home/grade/cache
RUN ./gradlew

COPY --from=spigot-builder /root/.m2/repository/org/spigotmc/ /root/.m2/repository/org/spigotmc/

# Copy plugin source

# build shadow jar
COPY --from=project-dir fakeentities fakeentities/
RUN ./gradlew fakeentities:jar --parallel

COPY --from=project-dir shared shared/
RUN ./gradlew shared:jar --parallel

COPY . bedwars/
RUN ./gradlew bedwars:shadowJar --parallel

FROM anti-reduce-server

RUN mkdir plugins && wget -O plugins/NoteBlockAPI.jar https://jitpack.io/com/github/koca2000/NoteBlockAPI/1.6.2/NoteBlockAPI-1.6.2.jar

COPY --from=plugin-builder /build/bedwars/build/libs/*-all.jar plugins/rewibw.jar