package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.store.impl.JSONStore;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONStoreTest {
    private static final String LOCAL_DB_NAME = "local-db.db";
    private Store<String> jsonStore = new JSONStore<>();

    @Test
    public void canCreateStore() {
        jsonStore.create(LOCAL_DB_NAME);
        Assert.assertTrue(Files.exists(Paths.get(LOCAL_DB_NAME)));
    }
}
