# RewiBW Clone
### Disclaimer
This is a clone of the BedWars plugin of the Rewinside.tv Minecraft server. This plugin is not affiliated with Rewinside.tv in any way. This plugin is not intended to be used for commercial.

### Run locally
```bash
docker compose run --rm --service-ports rewi-bw-server
```

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
cps-tester: # Where the cps tester is located
  x: 14.5
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

### maps/\<map name/>/config.yaml
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