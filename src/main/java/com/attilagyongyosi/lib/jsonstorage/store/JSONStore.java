package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StorageException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import com.attilagyongyosi.lib.jsonstorage.utils.FileUtils;
import com.attilagyongyosi.lib.jsonstorage.utils.JSONUtils;
import com.attilagyongyosi.lib.jsonstorage.utils.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JSONStore<T> {
    private static final Logger LOG = LoggerFactory.getLogger(JSONStore.class);

    private static final ObjectMapper MAPPER = JSONUtils.getMapper();

    private Path filePath;
    private BufferedWriter writer;
    private Map<String, T> data;

    public void setFilePath(final Path filePath) {
        this.filePath = filePath;
    }

    public JSONStore<T> create(final Class<T> type) throws StoreCreationException {
        LOG.debug("Creating JSON store in file {}..", this.filePath);
        createStoreFileIfNotExists();

        try {
            this.writer = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (final IOException ioe) {
            LOG.error("Could not create writer to file {}!", this.filePath);
            throw new StoreCreationException("File not found!", ioe);
        }

        this.data = new HashMap<>();

        try {
            final String fileContents = FileUtils.readContents(this.filePath);
            if (!StringUtils.isEmpty(fileContents)) {
                TypeFactory typeFactory = TypeFactory.defaultInstance();
                this.data = MAPPER.readValue(fileContents, typeFactory.constructMapType(Map.class, String.class, type));
            }
        } catch (final JsonParseException jpe) {
            LOG.error("Could not parse file contents as JSON!", jpe);
            throw new StoreCreationException(jpe);
        } catch (final IOException ioe) {
            LOG.error("Error while reading file {} as JSON!", this.filePath, ioe);
            throw new StoreCreationException(ioe);
        }

        return this;
    }

    public T store(final String key, final T object) throws StorageException {
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

    private void sync() throws StorageException {
        try {
            MAPPER.writeValue(this.writer, this.data);
        } catch (final IOException ioe) {
            LOG.error("Error while syncing to file {}!", this.filePath, ioe);
            throw new StorageException(ioe);
        }
    }

}
