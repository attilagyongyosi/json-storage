package com.attilagyongyosi.lib.jsonstorage.utils;

import com.attilagyongyosi.lib.jsonstorage.exceptions.InvalidJsonException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;

public final class JSONUtils {
    private static final ObjectMapper MAPPER;

    static {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        MAPPER = new ObjectMapper(jsonFactory);
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static <T> T parse(final Path file, final JavaType type) throws InvalidJsonException, IOException {
        try {
            final String fileContents = FileUtils.readContents(file);
            if (!StringUtils.isEmpty(fileContents)) {
                return MAPPER.readValue(fileContents, type);
            }
        } catch (final JsonMappingException | JsonParseException je) {
            throw new InvalidJsonException("Invalid JSON input!", je);
        }

        return null;
    }

    private JSONUtils() {}
}
