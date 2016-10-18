package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StorageException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class JSONStoreTest {
    private static final String LOCAL_DB_NAME = "local-db.db";
    private static final String LOCAL_DB_OUTSIDE_PROJECT_NAME = "../local-db-outside.db";

    private JSONStore<String> store;

    @Before
    public void setUp() throws StoreCreationException, StorageException {
        store = JSONStoreBuilder.builder().path(LOCAL_DB_NAME).build();
        store.store("test1", "test-data-1");
        store.store("test2", "test-data-2");
    }

    @Test
    public void canCreateStoreAnywhere() throws StoreCreationException {
        createStoreAndCheck(LOCAL_DB_OUTSIDE_PROJECT_NAME);
    }

    @Test
    public void canStore() throws StorageException {
        final String stored = store.store("something", "someObject");
        Assert.assertEquals("someObject", stored);
    }

    @Test
    public void canRetrieve() throws StorageException {
        final String stored = store.retrieve("test1");
        Assert.assertEquals("test-data-1", stored);
    }

    @Test
    public void canRetrieveAll() throws StorageException {
        final Collection<String> stored = store.retrieveAll();
        Assert.assertEquals(2, stored.size());
        Assert.assertTrue(stored.contains("test-data-1"));
        Assert.assertTrue(stored.contains("test-data-2"));
    }

    @After
    public void tearDown() {
        store.destroy();
    }

    private void createStoreAndCheck(final String fileName) throws StoreCreationException {
        JSONStore<String> outsideStore = JSONStoreBuilder
            .builder()
            .path(fileName)
            .build();

        Assert.assertTrue(Files.exists(Paths.get(fileName)));

        outsideStore.destroy();
    }
}
