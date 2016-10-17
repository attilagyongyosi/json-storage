package com.attilagyongyosi.lib.jsonstorage.store.impl;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StorageException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import com.attilagyongyosi.lib.jsonstorage.store.Store;
import com.attilagyongyosi.lib.jsonstorage.utils.FileUtils;
import com.attilagyongyosi.lib.jsonstorage.utils.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JSONStore implements Store<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(JSONStore.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Path filePath;
    private BufferedWriter writer;
    private Map<String, Object> data;

    @Override
    public void create(final String fileName) throws StoreCreationException {
        LOG.debug("Creating JSON store in file {}..", fileName);

        filePath = Paths.get(fileName);
        createStoreFileIfNotExists(filePath);

        try {
            writer = new BufferedWriter(new PrintWriter(fileName));
        } catch (final FileNotFoundException fnfe) {
            LOG.error("File {} not found while trying to create writer!", fileName);
            throw new StoreCreationException("File not found!", fnfe);
        }

        data = new HashMap<>();

        try {
            final String fileContents = FileUtils.readContents(filePath);
            if (!StringUtils.isEmpty(fileContents)) {
                data = MAPPER.readValue(fileContents, new TypeReference<Map<String, Object>>() {});
            }
        } catch (final JsonParseException jpe) {
            LOG.error("Could not parse file contents as JSON!", jpe);
            throw new StoreCreationException(jpe);
        } catch (final IOException ioe) {
            LOG.error("Error while reading file {} as JSON!", fileName, ioe);
            throw new StoreCreationException(ioe);
        }
    }

    @Override
    public Object store(final String key, final Object object) throws StorageException {
        try {
            data.put(key, object);
            MAPPER.writeValue(writer, data);
        } catch (final IOException ioe) {
            LOG.error("Error while writing to file {}!", filePath);
            throw new StorageException(ioe);
        }

        return object;
    }

    private void createStoreFileIfNotExists(final Path filePath) throws StoreCreationException {
        if (Files.notExists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (final IOException ioe) {
                LOG.error("Could not create store file at {}!", filePath);
                throw new StoreCreationException("Could not create store file!", ioe);
            }
        }
    }


}
