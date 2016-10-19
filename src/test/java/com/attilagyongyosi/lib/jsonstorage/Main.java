package com.attilagyongyosi.lib.jsonstorage;

import com.attilagyongyosi.lib.jsonstorage.domain.TestModel;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StorageException;
import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;
import com.attilagyongyosi.lib.jsonstorage.store.JSONStore;
import com.attilagyongyosi.lib.jsonstorage.store.JSONStoreBuilder;

public class Main {
    public static void main(String[] args) throws StoreCreationException, StorageException {
        JSONStore<TestModel> store = JSONStoreBuilder.<TestModel>builder()
                .path("testy")
                .build(TestModel.class);

        store.store("test1", TestData.MODEL1);
        store.store("test2", TestData.MODEL1);
    }
}
