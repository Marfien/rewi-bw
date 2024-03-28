FROM scratch AS project-dir

FROM eclipse-temurin:8-jdk AS plugin-builder

WORKDIR /build

COPY --from=project-dir gradle/ gradle/
COPY --from=project-dir gradlew settings.gradle.kts build.gradle.kts ./

ENV GRALDE_USER_HOME=/home/grade/cache
RUN ./gradlew

# Copy plugin source
COPY . map-setup-tool/
COPY --from=project-dir shared shared/

# build shadow jar
RUN ./gradlew map-setup-tool:shadowJar

FROM server-base

COPY --from=plugin-builder /build/map-setup-tool/build/libs/*-all.jar plugins/rewibw-setup.jar