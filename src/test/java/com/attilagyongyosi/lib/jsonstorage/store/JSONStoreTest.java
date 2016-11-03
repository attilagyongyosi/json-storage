package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.TestData;
import com.attilagyongyosi.lib.jsonstorage.domain.TestModel;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StorageException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class JSONStoreTest {
    private static final String LOCAL_DB_NAME = "local-db.db";
    private static final String LOCAL_DB_OUTSIDE_PROJECT_NAME = "../local-db-outside.db";
    private static final String LOCAL_DB_OUTSIDE_PROJECT_NAME2 = "../test/local-db-outside.db";
    private static final String EXISTING_DB_NAME = "db/test/valid-db.db";

    private FileLock lock;

    private JSONStore<TestModel> store;

    @Before
    public void setUp() throws Exception {
        store = JSONStoreBuilder.builder().path(LOCAL_DB_NAME).build(TestModel.class);
        store.store("test1", TestData.MODEL1);
        store.store("test2", TestData.MODEL2);
    }

    @Test
    public void canCreateStoreAnywhere() throws Exception {
        createStoreAndCheck(LOCAL_DB_OUTSIDE_PROJECT_NAME);
    }

    @Test
    public void canCreateStoreWithinNewDirectory() throws Exception {
        createStoreAndCheck(LOCAL_DB_OUTSIDE_PROJECT_NAME2);
    }

    @Test(expected = StoreCreationException.class)
    public void throwsErrorWhenCreatingWithInvalidName() throws Exception {
        createStoreAndCheck("test?-db.db");
    }

    @Test
    public void canStore() throws Exception {
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
    public void canRetrieve() throws Exception {
        final TestModel stored = store.retrieve("test1");
        Assert.assertEquals(TestData.MODEL1, stored);
    }

    @Test
    public void canRetrieveAll() throws Exception {
        final Collection<TestModel> stored = store.retrieveAll();
        Assert.assertEquals(2, stored.size());
        Assert.assertTrue(stored.contains(TestData.MODEL1));
        Assert.assertTrue(stored.contains(TestData.MODEL2));
    }

    @Test
    public void canOpenExistingDB() throws Exception {
        final TestModel expected = TestModel.builder()
            .id(1)
            .active(true)
            .property("existing")
            .property("test")
            .relative(TestModel.builder()
                .id(2)
                .active(false)
                .property("funky")
                .property("town")
                .build())
            .build();

        JSONStore<TestModel> existingStore = JSONStoreBuilder.<TestModel>builder()
                .path(EXISTING_DB_NAME)
                .build(TestModel.class);

        Collection<TestModel> stored = existingStore.retrieveAll();
        Assert.assertEquals(1, stored.size());
        Assert.assertEquals(expected, existingStore.retrieve("1"));
    }

    @Test(expected = StoreCreationException.class)
    public void failsWhenOpeningInvalidDb() throws StoreCreationException {
        JSONStoreBuilder.<TestModel>builder()
            .path("db/test/invalid-db.db")
            .build(TestModel.class);
    }

    @Test(expected = StoreCreationException.class)
    public void failsWhenOpeningUnmappableDb() throws StoreCreationException {
        JSONStoreBuilder.<TestModel>builder()
                .path("db/test/valid-unmappable-db.db")
                .build(TestModel.class);
    }

    @Test(expected = StoreCreationException.class)
    public void createFailsWhenStoreCanNotBeAccessed() throws StoreCreationException, IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(EXISTING_DB_NAME, "rw");
        lock = randomAccessFile.getChannel().lock();

        JSONStoreBuilder.<TestModel>builder()
            .path(EXISTING_DB_NAME)
            .build(TestModel.class);
    }

    @Test(expected = StorageException.class)
    public void clearFailsWhenStoreCanNotBeAccessed() throws StoreCreationException, StorageException, IOException {
        JSONStore<TestModel> store = JSONStoreBuilder.<TestModel>builder()
                .path(EXISTING_DB_NAME)
                .build(TestModel.class);

        RandomAccessFile randomAccessFile = new RandomAccessFile(EXISTING_DB_NAME, "rw");
        lock = randomAccessFile.getChannel().lock();

        store.clear();
    }

    @Test
    public void canClear() throws Exception {
        store.clear();
        final Collection<TestModel> stored = store.retrieveAll();
        Assert.assertEquals(0, stored.size());
    }

    @After
    public void tearDown() throws IOException {
        if (lock != null) {
            lock.release();
        }

        store.destroy();
    }

    private void createStoreAndCheck(final String fileName) throws StoreCreationException {
        JSONStore<String> outsideStore = JSONStoreBuilder.builder()
            .path(fileName)
            .build(String.class);

        Assert.assertTrue(Files.exists(Paths.get(fileName)));

        outsideStore.destroy();
    }
}
