package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.InvalidJsonException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StorageException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import com.attilagyongyosi.lib.jsonstorage.utils.FileUtils;
import com.attilagyongyosi.lib.jsonstorage.utils.JSONUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code JSONStore} is an easy way to persist a collection of objects
 * in a JSON file.
 *
 * JSONStore is essentially a {@link Map} instance which automatically gets
 * synchronized with a backing JSON file.
 *
 * @param <T>
 *     Type of the objects you want to store in the backing JSON file.
 *
 * @author Attila_Gyongyosi
 */
public class JSONStore<T> {
    private static final Logger LOG = LoggerFactory.getLogger(JSONStore.class);

    private static final ObjectMapper MAPPER = JSONUtils.getMapper();
    private static final StandardOpenOption[] OPEN_OPTIONS = new StandardOpenOption[] {
        StandardOpenOption.APPEND, StandardOpenOption.DSYNC
    };

    /**
     * The absolute path of the backing JSON file.
     */
    private Path filePath;

    /**
     * {@link java.io.Writer} instance used to serialize objects into
     * the JSON file.
     */
    private BufferedWriter writer;

    /**
     * Data structure being synchronized.
     */
    private Map<String, T> data;

    public void setFilePath(final Path filePath) {
        this.filePath = filePath;
    }

    public JSONStore<T> create(final Class<T> type) throws StoreCreationException {
        LOG.debug("Creating JSON store in file {}...", this.filePath);
        createStoreFileIfNotExists();
        createWriterFromStoreFile();
        readStoreContents(type);
        return this;
    }

    public T store(final String key, final T object) throws StorageException {
        LOG.debug("Storing {} as key {}...", object, key);
        this.data.put(key, object);
        this.sync();
        return object;
    }

    public Collection<T> retrieveAll() {
        return this.data.values();
    }

    public T retrieve(final String key) {
        return this.data.get(key);
    }

    public boolean clear() throws StorageException {
        LOG.debug("Clearing JSON storage at {}...", this.filePath);
        this.data.clear();
        this.sync();
        return true;
    }

    public boolean destroy() {
        LOG.debug("Destroying JSON store at {}...", this.filePath);
        try {
            writer.close();
            return Files.deleteIfExists(filePath);
        } catch (final IOException ioe) {
            LOG.error("Could not destroy JSON storage at {}!", filePath, ioe);
            return false;
        }
    }

    private void createStoreFileIfNotExists() throws StoreCreationException {
        try {
            this.filePath = FileUtils.createIfNotExists(this.filePath);
        } catch (final IOException ioe) {
            LOG.error("Could not create store file at {}!", this.filePath, ioe);
            throw new StoreCreationException("Could not create store file!", ioe);
        }
    }

    private void createWriterFromStoreFile() throws StoreCreationException {
        try {
            this.writer = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8, OPEN_OPTIONS);
        } catch (final IOException ioe) {
            LOG.error("Could not create writer to file {}!", this.filePath);
            throw new StoreCreationException("File not found!", ioe);
        }
    }

    private void readStoreContents(final Class<T> type) throws StoreCreationException {
        this.data = new HashMap<>();
        try {
            JavaType dataType = TypeFactory.defaultInstance().constructMapType(Map.class, String.class, type);
            this.data = JSONUtils.parse(this.filePath, dataType);
        } catch (final IOException | InvalidJsonException e) {
            LOG.error("Could not parse file contents as JSON!", e);
            throw new StoreCreationException(e);
        }
    }

    private void sync() throws StorageException {
        final Path backup = Paths.get(this.filePath.toString().concat("-backup"));

        try {
            Files.copy(this.filePath, backup);

            this.writer.close();
            this.writer = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8);

            MAPPER.writeValue(this.writer, this.data);
            Files.deleteIfExists(backup);
        } catch (final IOException  e) {
            LOG.error("Error while syncing to file {}! Restoring backup...", this.filePath, e);
            restoreBackup(backup);
            throw new StorageException(e);
        }
    }

    private void restoreBackup(final Path backup) {
        try {
            Files.move(backup, this.filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ioe) {
            LOG.error("Could not restore the previous state of the backing JSON file!", ioe);
        }
    }

}
