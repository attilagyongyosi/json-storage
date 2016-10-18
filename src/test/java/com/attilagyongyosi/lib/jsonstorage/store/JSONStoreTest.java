package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONStoreTest {
    private static final String LOCAL_DB_NAME = "local-db.db";
    private static final String LOCAL_DB_OUTSIDE_PROJECT_NAME = "../local-db-outside.db";

    @Test
    public void canCreateStore() throws StoreCreationException {
        createStoreAndCheck(LOCAL_DB_NAME);
    }

    @Test
    public void canCreateStoreAnywhere() throws StoreCreationException {
        createStoreAndCheck(LOCAL_DB_OUTSIDE_PROJECT_NAME);
    }

    private void createStoreAndCheck(final String fileName) throws StoreCreationException {
        JSONStore<String> store = JSONStoreBuilder
            .builder()
            .path(fileName)
            .build();

        Assert.assertTrue(Files.exists(Paths.get(fileName)));

        store.destroy();
    }
}
