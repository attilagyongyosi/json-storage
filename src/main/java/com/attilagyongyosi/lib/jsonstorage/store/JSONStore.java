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
 * {@link JSONStore} is an easy way to persist a collection of objects
 * in a JSON file.
 *
 * JSONStore is essentially a {@link Map} instance which automatically gets
 * synchronized with a backing JSON file.
 *
 * @param <T>
 *     Type of the objects you want to store in the backing JSON file.
 *
 * @author attilagyongyosi
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

    /**
     * Creates a new {@link JSONStore} instance.
     *
     * This method acts as a special constructor and will create a new file on the disk at the
     * path denoted by {@code filePath} if it does not yet exist.
     *
     * Then the file's contents will be read and deserialized into the backing {@link Map} instance.
     *
     * @param type
     *      the type of objects this JSON store will contain.
     *
     * @return itself
     *
     * @throws StoreCreationException
     *      when there is an issue during the creation and initialization of the store
     */
    public JSONStore<T> create(final Class<T> type) throws StoreCreationException {
        LOG.debug("Creating JSON store in file {}...", this.filePath);
        createStoreFileIfNotExists();
        createWriterFromStoreFile();
        readStoreContents(type);
        return this;
    }

    /**
     * Stores an object in the JSON store.
     *
     * @param key
     *      the String key where the object should be stored.
     *
     * @param object
     *      the object itself to store as JSON.
     *
     * @return the stored object if operation was successful
     *
     * @throws StorageException
     *      when an error occurs during the storage operation
     */
    public T store(final String key, final T object) throws StorageException {
        LOG.debug("Storing {} as key {}...", object, key);
        this.data.put(key, object);
        this.sync();
        return object;
    }

    /**
     * Returns all stored object.
     *
     * This method will not actually fetch data from the backing JSON file,
     * it will simply return the values stored in the {@code Map}.
     *
     * This is to avoid unnecessary file read operations as the Map should
     * always be in sync with the backing file.
     *
     * @return a collection of objects currently stored in the storage.
     */
    public Collection<T> retrieveAll() {
        return this.data.values();
    }

    /**
     * Returns a single object from the store, specified by the supplied key.
     *
     * This method will not actually fetch data from the backing JSON file,
     * it will simply return the values stored in the {@link Map}.
     *
     * This is to avoid unnecessary file read operations as the Map should
     * always be in sync with the backing file.
     *
     * @param key
     *      the key to retrieve the stored object from
     *
     * @return the object contained at the supplied key or {@code null}.
     */
    public T retrieve(final String key) {
        return this.data.get(key);
    }

    /**
     * Clears both the underlying {@link Map} instance and the backing
     * JSON file.
     *
     * @return {@code true} if the operation was successful, {@code false} otherwise.
     *
     * @throws StorageException when operation fails
     */
    public boolean clear() throws StorageException {
        LOG.debug("Clearing JSON storage at {}...", this.filePath);
        this.data.clear();
        this.sync();
        return true;
    }

    /**
     * Destroys the current JSON storage.
     *
     * It will close opened resources and will delete the backing JSON file.
     *
     * @return {@code true} if the operation was successful, {@code false} otherwise.
     */
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
        try {
            JavaType dataType = TypeFactory.defaultInstance().constructMapType(Map.class, String.class, type);
            this.data = JSONUtils.parse(this.filePath, dataType);
            if (this.data == null) {
                this.data = new HashMap<>();
            }
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
