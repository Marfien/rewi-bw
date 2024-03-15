package dev.marfien.rewibw.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static void copyFolder(Path source, Path target) throws IOException {
        if (Files.exists(target)) {
            Files.delete(target);
        }

        Files.copy(source, target);
    }

}
