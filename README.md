# RewiBW Clone
### Disclaimer
This is a clone of the BedWars plugin of the Rewinside.tv Minecraft server. This plugin is not affiliated with Rewinside.tv in any way. This plugin is not intended to be used for commercial.

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
spawn: # Spawn location
  x: 16.5
  y: 64.0
  z: 2.5
  pitch: 0.0
  yaw: 180.0
cps: # Where the cps tester is located
  x: 14.5
  y: 64.0
  z: 2.5
  pitch: 0.0
  yaw: 0.0
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

### maps/<map name>/config.yaml
```yaml
spawn: # Spectator spawn
  x: -72.5
  y: 120.0
  z: -6.5
  pitch: 90.0
world:
  generator: empty # Map generator (currently only empty is supported)
map:
  icon: log_2:1 # icon in format <id>[:<data>]
  displayName: "Medieval Mansion" # display name
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
      x: -150.0
      y: 82.0
      z: 0.0
      direction: north
    spawn: # The team spawn location
      x: -144.5
      y: 75.0
      z: -6.5
      yaw: -90.0
```