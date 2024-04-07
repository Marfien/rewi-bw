package dev.marfien.rewibw.setuptool;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@ConfigSerializable
public class SetupToolConfig {

    private Path importPath = Paths.get("/" , "data", "import");

}
