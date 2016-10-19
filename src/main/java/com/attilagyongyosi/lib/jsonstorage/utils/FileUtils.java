package com.attilagyongyosi.lib.jsonstorage.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    public static String readContents(final Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
    }

    public static Path createIfNotExists(final Path path) throws IOException {
        if (Files.notExists(path)) {
            return Files.createFile(path);
        }

        LOG.debug("File {} already exists, no need to create it.", path);
        return path;
    }

    private FileUtils() {}
}
