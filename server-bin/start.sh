#!/usr/bin/env sh

if [ "$EULA" != 'true' ]; then
  echo 'EULA is not set to true. Exiting.'
  exit 1
fi

echo "eula=$EULA" > eula.txt

if [ -z "$MEMORY" ]; then
  MEMORY="1024"
fi

if [ -z "$ONLINE_MODE" ]; then
  echo "online-mode=$ONLINE_MODE" >> server.properties
fi

COMMAND="java -Xmx${MEMORY}M -Xms${MEMORY}M"

if [ "$AIKARS_FLAGS" = 'true' ]; then
  COMMAND="$COMMAND -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1HeapRegionSize=8M -XX:G1HeapWastePercent=5 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=4 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1NewSizePercent=30 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -XX:MaxGCPauseMillis=200 -XX:MaxTenuringThreshold=1 -XX:SurvivorRatio=32 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true"
fi
if [ "$ANTI_REDUCE" = 'true' ]; then
  COMMAND="$COMMAND -javaagent:agent.jar"
fi

if [ "$DEBUG" = 'true' ]; then
  COMMAND="$COMMAND -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005  -Dlog4j.configurationFile=log4j2.debug.xml"
else
  COMMAND="$COMMAND -Dlog4j.configurationFile=log4j2.xml"
fi

if [ "$PROFILING" = 'true' ]; then
  COMMAND="$COMMAND -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010"
fi

echo "Starting server with ${MEMORY}MB of RAM"
eval "$COMMAND -jar spigot.jar nogui"