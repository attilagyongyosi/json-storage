package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class JSONStoreBuilder<T> {
    private final JSONStore<T> jsonStore;

    public static <T> JSONStoreBuilder builder() {
        return new JSONStoreBuilder<T>();
    }

    public JSONStoreBuilder() {
        this.jsonStore = new JSONStore<>();
    }

    public JSONStoreBuilder<T> path(final String pathAsString) {
        final Path path = Paths.get(pathAsString);
        this.jsonStore.setFilePath(path);
        return this;
    }

    public JSONStore<T> build() throws StoreCreationException {
        return this.jsonStore.create();
    }
}
