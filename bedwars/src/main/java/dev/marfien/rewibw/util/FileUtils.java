package dev.marfien.rewibw.util;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class FileUtils {

    public static void copyFolder(Path source, Path target) throws IOException {
        if (Files.exists(target)) {
            try (Stream<Path> walk = Files.walk(target)) {
                walk.sorted()
                        .map(Path::toFile)
                        .forEach(File::deleteOnExit);
            }
        }

        Files.walkFileTree(source, new CopyFileVisitor(source, target));
    }

    @RequiredArgsConstructor
    public static class CopyFileVisitor extends SimpleFileVisitor<Path> {

        private final Path source;
        private final Path target;

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
            Files.createDirectories(this.target.resolve(this.source.relativize(dir).toString()));
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
            Files.copy(file, this.target.resolve(this.source.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }
    }

}
