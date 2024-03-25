FROM scratch AS project-dir

FROM eclipse-temurin:8-jdk AS plugin-builder

WORKDIR /build

COPY --from=project-dir gradle/ gradle/
COPY --from=project-dir gradlew settings.gradle.kts build.gradle.kts ./

RUN ./gradlew --no-daemon

# Copy plugin source
COPY . map-setup-tool/
COPY --from=project-dir shared shared/

# build shadow jar
RUN ./gradlew map-setup-tool:shadowJar --no-daemon

FROM server-base

COPY --from=plugin-builder /build/map-setup-tool/build/libs/*-all.jar plugins/rewibw-setup.jar