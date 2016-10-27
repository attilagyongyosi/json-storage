package com.attilagyongyosi.lib.jsonstorage.utils;

/**
 * Utility class for dealing with Strings.
 *
 * @author attilagyongyosi
 */
public final class StringUtils {

    /**
     * Checks whether a String is empty.
     *
     * @param string
     *
     * @return {@code false} if the String is empty or null, {@code true} otherwise
     */
    public static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    private StringUtils() {}
}
