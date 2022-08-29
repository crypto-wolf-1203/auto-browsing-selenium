package com.pplvn.util;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String makeSlug(String input) {
        return makeSlug(input, 0, true);
    }
    public static String makeSlug(String input, int maxLength, boolean lowerCase) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return lowerCase ? slug.toLowerCase(Locale.ENGLISH) : slug;
    }

    public static String trimUpperCase(String s) {
        return StringUtils.trimToEmpty(s).toUpperCase();
    }
}
