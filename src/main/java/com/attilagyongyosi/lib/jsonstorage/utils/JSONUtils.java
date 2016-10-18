package com.attilagyongyosi.lib.jsonstorage.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONUtils {

    public static ObjectMapper getMapper() {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        return new ObjectMapper(jsonFactory);
    }

    private JSONUtils() {}
}
