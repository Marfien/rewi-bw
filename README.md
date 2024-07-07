# RewiBW Clone
### Disclaimer
This is a clone of the BedWars plugin of the Rewinside.tv Minecraft server. This plugin is not affiliated nor related to Rewinside.tv in any way. This plugin is not intended to be used for commercial.

### Run locally
```shell
docker compose run --rm --service-ports rewi-bw-server
```

### How to build from source
First of all, you need to make sure that you have the spigot server in your local maven repository.
You can archive this by using [SpigotMC's BuildTools](https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar).
Just run it with `java -jar BuildTools.jar --rev 1.8.8`. Note that you need to have Java 8 and Maven installed.

This projects uses gradle(-wrapper) as build tool. Therefore, you can use the following commands to build the project:
On shell:
```shell
./gradlew clean build
```
On Windows:
```shell
.\gradlew.bat clean build
```

The plugin can be found inside the `bedwars/build/libs` directory. The setup tool will be located in the `map-setup-tool/build/libs` directory.

### Server up and running

1. Download the [latest release](https://github.com/Marfien/rewi-bw/releases/latest)
2. Create an empty directory for the server
3. Download Spigot 1.8.8 or any fork of it (perferably [PaperSpigot](https://api.papermc.io/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar)) and place the jar file in the server directory
4. Put the `bedwars-*-all.jar` into the `plugins` directory inside your server directory (you might need to create it)
5. Add the [NoteblocksAPI](https://jitpack.io/com/github/koca2000/NoteBlockAPI/1.6.2/NoteBlockAPI-1.6.2.jar) plugin to the `plugins` directory
6. Create a file called `eula.txt` in the server directory and add `eula=true` to it to accept the EULA of Mojang
7. Start the server with `java -jar <your_server_jar>.jar

#### Bonus: Anti-Reducing
7. Download the `anti-reduce-agent-*-all.jar` from the [latest release](https://github.com/Marfien/rewi-bw/releases/latest)
and put it into the server directory
8. Start the server with `java -javaagent:<your_agent_jar>.jar -jar <your_server_jar>.jar`

If you are feeling adventurous, you can take a look at the `server-bin` directory.
It contains an example start script for the server and some preconfigured server files for performance improvements.

Alternatively, you can use Docker to run the server. If you are familiar with Docker, you can use the `docker-compose.yml` file in the root directory to run the server. \
The Image is provided on [DockerHub](https://hub.docker.com/r/marfiens/rewibw-server).
You might also take a look at [the Dockerfile of the BedWars server](bedwars/Dockerfile) and [the Dockerfile of the server base](server-bin/Dockerfile).

## Configuration

### plugins/rewi-bw/config.yml
```yaml
teams:
  players: 2 # amount of players per team
  colors: # which teams are available
    - red
    - green
    - purple
    - yellow
voting:
  votable-slots: [1, 4, 7] # which slots in the vote inventory are filled with a map
maps:
  path: /data/maps/ # path to the map pool collection (maps will be copied to the server)
  lobby: /data/lobby/ # path to the lobby map
```

### lobby/config.yaml
```yaml
world:
  generator: empty # Map generator (currently only empty is supported)
  environment: normal # Map environment (normal, nether, the_end)
  init-time: 0 # Time of day when the map is loaded
  difficulty: normal # Map difficulty (peaceful, easy, normal, hard)
spawn: # Spawn location
  x: 16.5
  y: 64.0
  z: 2.5
  yaw: 180.0
cps-tester: # Optional: Where the cps tester is located
  x: 14.5
  y: 64.0
  z: 2.5
jump-and-run: # Optional: Where the jump and run is located
  start:
    x: 12.5
    y: 64.0
    z: 2.5
  finish:
    x: 10.5
    y: 64.0
    z: 2.5
teams:
  <team color>:
    joiner: # The ArmorStand to join the team
      x: 9.5
      y: 64.0
      z: -5.5
    displays: # An array of positions for the display npc's 
      - x: 8.5
        y: 65.0
        z: -8.5
        pitch: 0.0
        yaw: 0.0
```

### maps/\<map name>/config.yaml
This might not be the most convenient way to configure a map, but it gives you the most control about the locations.
If you want to have a more user-friendly way to configure the map, you can use the Map Setup Tool to generate the configuration file.

```yaml
world: # Same as lobby config
spectator-pawn:
  x: -72.5
  y: 120.0
  z: -6.5
  pitch: 90.0
map:
  icon: log_2:1 # icon in format <id>[:<data>]
  display-name: "Medieval Mansion" # display name
  border: # The border of the map
    x1: 21
    x2: -167
    z1: -101
    z2: 87
shops: # array of shop locations
  - x: -76.5
    y: 75.0
    z: -80.5
    yaw: -45.0
spawner:
  bronze: # array of bronze spawning locations
    - x: -80.5
      y: 75.0
      z: 67.5
  silver: # array of gold iron locations
    - x: -71.5
      y: 73.0
      z: 20.5
  gold: # array of gold spawning locations
    - x: -66.5
      y: 87.0
      z: 70.5
teams:
  <team color>:
    bed: # bed position and direction
      x: -150 # These need to be integers
      y: 82
      z: 0
      direction: north
    spawn: # The team spawn location
      x: -144.5
      y: 75.0
      z: -6.5
      yaw: -90.0
```
