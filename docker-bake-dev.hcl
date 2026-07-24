variable "BASE_IMAGE" {
    default = "eclipse-temurin:8-jdk"
}

target "docker-metadata-action-server" {
    tags = [
        "marfiens/rewibw-server:main"
    ]
}

target "docker-metadata-action-setup-tool" {
    tags = [
        "marfiens/rewibw-setup-tool:main"
    ]
}
