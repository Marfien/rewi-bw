variable "ONLINE_MODE" {
  default = "true"
}

variable "TAG" {
  default = "latest"
}

group "default" {
  targets = ["rewibw-server", "rewibw-setup-tool"]
}

target "server-base" {
  context = "server-bin/"
  tags = [createTag("paper-base")]
}

target "anti-reduce-server" {
  context = "anti-reduce-agent/"
  contexts = {
    "server-base" = "target:server-base"
    "project-dir" = "./"
  }
  tags = [createTag("paper-anti-reduce")]
}

target "rewibw-server" {
  context = "bedwars/"
  contexts = {
    "anti-reduce-server" = "target:anti-reduce-server"
    "project-dir" = "./"
  }
  tags = [createTag("rewibw-server")]
}

target "rewibw-setup-tool" {
  context = "map-setup-tool/"
  contexts = {
    "server-base" = "target:server-base"
    "project-dir" = "./"
  }
  tags = [createTag("rewibw-setup-tool")]
}

function "createTag" {
  params = [image_name]
  result = equal("true", ONLINE_MODE) ? "docker.io/marfiens/${image_name}:${TAG}" : "docker.io/marfiens/${image_name}:${TAG}-offline"
}