package com.attilagyongyosi.lib.jsonstorage.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
    public static String readContents(final Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
    }

    public static Path createIfNotExists(final Path path) throws IOException {
        return Files.notExists(path) ? Files.createFile(path) : path;
    }

    private FileUtils() {}
}
