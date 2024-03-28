FROM scratch AS project-dir

FROM eclipse-temurin:8-jdk AS agent-builder

WORKDIR /build

COPY --from=project-dir gradle/ gradle/
COPY --from=project-dir gradlew settings.gradle.kts build.gradle.kts ./

ENV GRALDE_USER_HOME=/home/grade/cache
RUN ./gradlew

# Copy plugin source
COPY . anti-reduce-agent/
# build shadow jar
RUN ./gradlew anti-reduce-agent:shadowJar

FROM server-base

COPY --from=agent-builder /build/anti-reduce-agent/build/libs/*-all.jar agent.jar