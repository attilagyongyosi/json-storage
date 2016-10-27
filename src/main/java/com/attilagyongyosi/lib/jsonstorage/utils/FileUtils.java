package com.attilagyongyosi.lib.jsonstorage.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for file operations.
 *
 * @author attilagyongyosi
 */
public final class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Reads the contents of the file specified by the given {@link Path} instance.
     * Resulting {@code String} will be UTF-8 encoded.
     *
     * @param filePath
     *      a {@link Path} instance containing the absolute path of the file to read.
     *
     * @return an UTF-8 encoded {@code String} containing the file's contents.
     *
     * @throws IOException
     */
    public static String readContents(final Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
    }

    /**
     * Creates a new file at the path denoted by {@code path} if it doesn't exist already.
     *
     * @param path
     *      the file's absolute path.
     *
     * @return the {@link Path} of the created file. Essentially the input parameter.
     *
     * @throws IOException
     */
    public static Path createIfNotExists(final Path path) throws IOException {
        if (Files.notExists(path)) {
            return Files.createFile(path);
        }

        LOG.debug("File {} already exists, no need to create it.", path);
        return path;
    }

    private FileUtils() {}
}
