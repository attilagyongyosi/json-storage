package com.attilagyongyosi.lib.jsonstorage.store;

import com.attilagyongyosi.lib.jsonstorage.exceptions.StoreCreationException;

public interface Store<T> {
    void create(String fileName) throws StoreCreationException;
}
