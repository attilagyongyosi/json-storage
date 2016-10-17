package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import com.attilagyongyosi.lib.jsonstorage.store.impl.JSONStore;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONStoreTest {
    private static final String LOCAL_DB_NAME = "local-db.db";
    private static final String LOCAL_DB_OUTSIDE_PROJECT_NAME = "../local-db-outside.db";

    private Store<String> jsonStore = new JSONStore<>();

    @Test
    public void canCreateStore() throws StoreCreationException {
        createStoreAndCheck(LOCAL_DB_NAME);
    }

    @Test
    public void canCreateStoreAnywhere() throws StoreCreationException {
        createStoreAndCheck(LOCAL_DB_OUTSIDE_PROJECT_NAME);
    }

    private void createStoreAndCheck(final String fileName) throws StoreCreationException {
        jsonStore.create(fileName);
        Assert.assertTrue(Files.exists(Paths.get(fileName)));
    }
}
