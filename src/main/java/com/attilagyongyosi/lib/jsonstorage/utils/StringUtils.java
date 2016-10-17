package com.attilagyongyosi.lib.jsonstorage.utils;

public final class StringUtils {
    public static final String EMPTY = "";

    public static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    private StringUtils() {}
}
