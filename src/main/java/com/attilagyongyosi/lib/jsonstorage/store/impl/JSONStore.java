package com.attilagyongyosi.lib.jsonstorage.store.impl;

import com.attilagyongyosi.lib.jsonstorage.store.Store;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JSONStore<T> implements Store {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BufferedWriter writer;
    private BufferedReader reader;
    private Map<String, T> data;

    @Override
    public void create(final String fileName) {
        Path filePath = Paths.get(fileName);

        if (Files.notExists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();    // TODO: custom exception
                System.exit(1);
            }
        }

        try {
            writer = new BufferedWriter(new PrintWriter(fileName));
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();        // TODO: custom exception
            System.exit(2);
        }

        data = new HashMap<>();
        try {
            final String fileContents = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            if (fileContents != null && !fileContents.isEmpty()) {
                data = MAPPER.readValue(fileContents, new TypeReference<Map<String, T>>(){});
            }
        } catch (IOException e) {
            e.printStackTrace();        // TODO: custom exception
            System.exit(3);
        }
    }
}
