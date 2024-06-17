variable "BASE_IMAGE" {
  default = null
}

group "default" {
  targets = ["rewibw-server", "rewibw-setup-tool"]
}

target "paper-base" {
  args = {
    BASE_IMAGE = "$BASE_IMAGE"
  }
  context = "server-bin/"
}

target "anti-reduce-server" {
  context = "anti-reduce-agent/"
  contexts = {
    "server-base" = "target:paper-base"
    "project-dir" = "./"
  }
}

target "docker-metadata-action-server" {}

target "rewibw-server" {
  inherits = ["docker-metadata-action-server"]
  context = "bedwars/"
  contexts = {
    "anti-reduce-server" = "target:anti-reduce-server"
    "project-dir" = "./"
  }
}

target "docker-metadata-action-setup-tool" {}

target "rewibw-setup-tool" {
  inherits = ["docker-metadata-action-setup-tool"]
  context = "map-setup-tool/"
  contexts = {
    "server-base" = "target:paper-base"
    "project-dir" = "./"
  }
}