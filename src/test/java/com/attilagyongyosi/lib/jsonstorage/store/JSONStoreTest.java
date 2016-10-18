package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.domain.TestModel;
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

    private static final TestModel MODEL1 = TestModel.builder()
        .id(1)
        .active(false)
        .property("nice")
        .property("cosy")
        .property("model")
        .relative(TestModel.builder()
            .id(2)
            .active(true)
            .property("funk")
            .build())
        .relative(TestModel.builder()
            .id(3)
            .active(true)
            .build())
        .build();

    private static final TestModel MODEL2 = TestModel.builder()
        .id(4)
        .active(true)
        .property("small")
        .property("big")
        .relative(TestModel.builder()
            .id(5)
            .active(false)
            .property("warm")
            .build())
        .build();


    private JSONStore<TestModel> store;

    @Before
    public void setUp() throws StoreCreationException, StorageException {
        store = JSONStoreBuilder.builder().path(LOCAL_DB_NAME).build();
        store.store("test1", MODEL1);
        store.store("test2", MODEL2);
    }

    @Test
    public void canCreateStoreAnywhere() throws StoreCreationException {
        createStoreAndCheck(LOCAL_DB_OUTSIDE_PROJECT_NAME);
    }

    @Test
    public void canStore() throws StorageException {
        final TestModel toBeStored = TestModel.builder()
            .id(11)
            .active(true)
            .property("brand")
            .property("new")
            .build();

        final TestModel stored = store.store("new", toBeStored);
        Assert.assertEquals(stored, toBeStored);
    }

    @Test
    public void canRetrieve() throws StorageException {
        final TestModel stored = store.retrieve("test1");
        Assert.assertEquals(MODEL1, stored);
    }

    @Test
    public void canRetrieveAll() throws StorageException {
        final Collection<TestModel> stored = store.retrieveAll();
        Assert.assertEquals(2, stored.size());
        Assert.assertTrue(stored.contains(MODEL1));
        Assert.assertTrue(stored.contains(MODEL2));
    }

    @After
    public void tearDown() {
        store.destroy();
    }

    private void createStoreAndCheck(final String fileName) throws StoreCreationException {
        JSONStore<String> outsideStore = JSONStoreBuilder.builder()
            .path(fileName)
            .build();

        Assert.assertTrue(Files.exists(Paths.get(fileName)));

        outsideStore.destroy();
    }
}
