package com.zipwhip.util;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA. User: Ali Date: Mar 22, 2010 Time: 2:04:58 PM
 * <p/>
 * A collection of useful utilities for working with strings.
 */
public class StringUtil {

    public static final String EMPTY_STRING = "";

    // private static final List<String> VALID_NUMBERS = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8",
    // "9");
    private static final String PLUS_MOBIFONE = "+84";
    private static final String PLUS = "+";
    private static final String NANP = "+1";

    public static enum CapitalizeMode {
        /**
         * Smart Title Case: If the string is all upper case or all lower case, then convert it to title case.
         */
        SMART_TITLE_CASE,

        /**
         * Ignore Case: Do not change the case of the string.
         */
        IGNORE,

        TITLE_CASE,

        UPPER_CASE,

        LOWER_CASE
    }

    public static String capitalize(final String string, final CapitalizeMode capitalizeMode) {
        if (isNullOrEmpty(string) || null == capitalizeMode || CapitalizeMode.IGNORE == capitalizeMode) {
            return string;
        } else if (CapitalizeMode.TITLE_CASE == capitalizeMode) {
            return StringUtils.capitalize(string);
        } else if (CapitalizeMode.SMART_TITLE_CASE == capitalizeMode) {
            // determine if we need to.
            if (StringUtils.isAllLowerCase(string) || StringUtils.isAllUpperCase(string)) {
                return StringUtils.capitalize(string);
            }
        } else if (CapitalizeMode.LOWER_CASE == capitalizeMode) {
            return StringUtils.lowerCase(string);
        } else if (CapitalizeMode.UPPER_CASE == capitalizeMode) {
            return StringUtils.upperCase(string);
        }

        // This should not be possible.
        return string;
    }

    public static boolean equals(String string1, String string2) {
        if (string1 == string2) {
            return true; // covers both null, or both same instance
        } else if (string1 == null) {
            return false; // covers 1 null, other not.
        }

        return (string1.equals(string2)); // covers equals
    }

    public static String toString(Map object) {
        if (CollectionUtil.isNullOrEmpty(object)) {
            return null;
        }

        return toString((Object) object);
    }

    public static String toString(Collection object) {
        if (CollectionUtil.isNullOrEmpty(object)) {
            return null;
        }

        return toString((Object) object);
    }

    public static String toString(final Object[] object) {
        if (CollectionUtil.isNullOrEmpty(object)) {
            return null;
        }

        return toString((Object) object);
    }

    public static String toString(final Object object) {
        if (null == object) {
            return null;
        }

        return String.valueOf(object);
    }

    public static boolean equalsIgnoreCase(String string1, String... strings) {

        if (CollectionUtil.isNullOrEmpty(strings)) {
            if (StringUtil.isNullOrEmpty(string1)) {
                return true;
            }
            return false;
        }

        for (String string : strings) {

            if (string1 == string) {
                return true; // covers both null, or both same instance
            }

            if ((string1 == null) || (string1 == "") || (string == null) || (string == "")) {
                return false;
            }

            if (string1 == null) {
                return false; // covers 1 null, other not.
            }

            if (string1.equalsIgnoreCase(string)) {
                // covers equals
                return true;
            }
        }
        return false;
    }

    public static String[] split(String s, String regex) {
        if (isNullOrEmpty(s) || isNullOrEmpty(regex)) {
            String[] out = new String[1];
            out[0] = s;
            return out;
        }

        return s.split(regex);
    }

    /**
     * Strips all characters that are not numbers (0 - 9) and returns a new
     * string. Returns and empty string if the mobile number is null or empty.
     * Assumes US format cleaning "1" from the beginning if it exists.
     *
     * @param mobileNumber - mobile number string to parse
     * @return String - parsed mobile number
     */
    public static String safeCleanMobileNumber(String mobileNumber) {
        return safeCleanMobileNumber(mobileNumber, false);
    }

    /**
     * Strips all characters that are not numbers (0 - 9) and returns a new
     * string. Returns and empty string if the mobile number is null or empty.
     *
     * @param mobileNumber        - mobile number string to parse
     * @param appendInternational appends (1) at the beginning of mobile number (international format)
     * @return String - parsed mobile number
     */
    public static String safeCleanMobileNumber(String mobileNumber, boolean appendInternational) {
        String cleanMobileNumber = cleanMobileNumber(mobileNumber);

        if (isNullOrEmpty(cleanMobileNumber)) {
            return EMPTY_STRING;
        }

        if (appendInternational && (cleanMobileNumber.length() == 10)) {
            return "1" + cleanMobileNumber;
        } else if ((cleanMobileNumber.length() == 11) && equals(cleanMobileNumber.charAt(0), "1")) {
            return cleanMobileNumber.substring(1);
        } else if (!appendInternational && (cleanMobileNumber.length() == 13) && cleanMobileNumber.startsWith("001")) {
            return cleanMobileNumber.substring(3);
        }

        return cleanMobileNumber;
    }

    private static boolean equals(char string1, String string2) {
        return equals(new String(new char[]{string1}), string2);
    }

    /**
     * Strips all characters that are not numbers (0 - 9) and returns a new
     * string. Returns an empty string if the mobile number is null or empty.
     *
     * @param mobileNumber - mobile number string to parse
     * @return String - parsed mobile number
     */
    private static final Pattern validMobileNumber = Pattern.compile("^(\\+84)?\\d+$");

    public static String cleanMobileNumber(String mobileNumber) {
        if (isNullOrEmpty(mobileNumber)) {
            return EMPTY_STRING;
        }

        Matcher match = validMobileNumber.matcher(mobileNumber);
        if (match.find()) {
            return mobileNumber;
        } else {
            StringBuilder cleanMobileNumber = new StringBuilder();

            int index = 0;
            if (mobileNumber.startsWith(PLUS_MOBIFONE)) {
                index++;
                cleanMobileNumber.append(PLUS);
            } else if (mobileNumber.startsWith(NANP)) {
                index = 2;
            }

            for (int i = index; i < mobileNumber.length(); i++) {
                char c = mobileNumber.charAt(i);
                if ((i == 0) && ((c == '+') || (((c >= 0x30) && (c <= 0x39))))) {
                    // if (contains(VALID_NUMBERS, mobileNumber.charAt(i))) {
                    cleanMobileNumber.append(c);
                } else if (((c >= 0x30) && (c <= 0x39))) {
                    cleanMobileNumber.append(c);
                }
            }

            return cleanMobileNumber.toString();
        }
    }

    private static boolean contains(List<String> validNumbers, char toFind) {
        if (CollectionUtil.isNullOrEmpty(validNumbers)) {
            return false;
        }

        String string = new String(new char[]{toFind});

        return validNumbers.contains(string);
    }

    public static boolean exists(String string) {
        return !isNullOrEmpty(string);
    }

    public static boolean anyNullOrEmpty(String... strings) {
        if (CollectionUtil.isNullOrEmpty(strings)) {
            return true;
        }

        for (String string : strings) {
            if (StringUtil.isNullOrEmpty(string)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNullOrEmpty(String string) {
        //used string.length() == 0 because not everyone has Java 1.6
        return (string == null) || (string.length() == 0) || string.equalsIgnoreCase("null");
    }

    public static String defaultValue(String value, String defaultValue) {
        return isNullOrEmpty(value) ? defaultValue : value;
    }

    public static String replaceAll(String initialString, String regex, String replacement) {
        if ((initialString == null) || (regex == null) || (replacement == null)) {
            return initialString;
        }
        return initialString.replaceAll(regex, replacement);
    }

    public static String replace(String s, CharSequence target, CharSequence newChar) {
        if ((s == null) || (target == null) || (newChar == null)) {
            return s;
        }
        return s.replace(target, newChar);
    }

    /**
     * Case insensitive.
     *
     * @param source
     * @param toFind
     * @return
     */
    public static boolean startsWith(String source, String toFind) {
        if (source == null) {
            return false;
        }
        if (toFind == null) {
            return false;
        }

        return (source.toLowerCase().startsWith(toFind.toLowerCase()));
    }

    public static boolean contains(String source, String toFind) {
        if (source == null) {
            return false;
        }
        if (toFind == null) {
            return false;
        }

        return (source.toLowerCase().contains(toFind.toLowerCase()));
    }

    public static String join(Collection<Object> parts) {
        if (CollectionUtil.isNullOrEmpty(parts)) {
            return null;
        }
        if (parts.size() > 5) {
            StringBuilder sb = new StringBuilder();
            for (Object part : parts) {
                sb.append(part);
            }
            return sb.toString();
        } else {
            String result = "";
            for (Object part : parts) {
                result += String.valueOf(part);
            }
            return result;
        }
    }

    // adding this method because the default Boolean.ParseBoolean(string s)
    // method sucks and will interpret anything other than true as false
    public static Boolean parseBoolean(String s) {
        if (equals(s, "true")) {
            return true;
        } else if (equals(s, "false")) {
            return false;
        }
        return null;
    }

    public static <T> String join(String joiner, List<T> parts) {
        if (CollectionUtil.isNullOrEmpty(parts)) {
            return null;
        }
        int i = 0;
        if (parts.size() > 5) {
            StringBuilder sb = new StringBuilder();
            for (T part : parts) {
                if (part == null) {
                    continue;
                }

                if (i != 0) {
                    sb.append(joiner);
                }
                sb.append(part);
                i++;
            }
            return sb.toString();
        } else {
            String result = "";
            for (T part : parts) {
                if (part == null) {
                    continue;
                }

                if (i != 0) {
                    result += joiner;
                }
                result += String.valueOf(part);
                i++;
            }
            return result;
        }
    }

    private static String valueOf(Object p) {
        if (p == null) {
            return null;
        }
        if (p instanceof String) {
            return (String) p;
        }
        return String.valueOf(p);
    }

    public static String join(Object... parts) {
        if (CollectionUtil.isNullOrEmpty(parts)) {
            return null;
        }

        if (parts.length == 1) {
            return valueOf(parts[0]);
        }

        if (parts.length > 5) {
            StringBuilder sb = new StringBuilder();
            for (Object part : parts) {
                if (part == null) {
                    continue;
                }

                sb.append(part);
            }
            return sb.toString();
        } else {
            String result = null;
            for (Object part : parts) {
                if (part == null) {
                    continue;
                }

                if (result == null) {
                    result = valueOf(part);
                    continue;
                }

                result += valueOf(part);
            }
            return result;
        }
    }

    public static boolean containsExtendedChars(String data) {

        if (data == null) {
            return false;
        }
        byte[] bytes = data.getBytes();
        for (byte b : bytes) {
            int dec = b & 0xff;

            if ((dec >= 128) && (dec <= 255)) {
                return true;
            }
        }

        return false;
    }

    public static String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if ((in == null) || ("".equals(in))) {
            return ""; // vacancy test.
        }
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
            // here; it should not happen.
            if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF)) || ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            }
        }
        return out.toString();
    }

    public static String convertPatterns(String contents, Map<String, String> keyVals) {

        if (contents == null) {
            throw new NullPointerException("Cannot convert null pattern");
        }

        for (Map.Entry<String, String> entry : keyVals.entrySet()) {
            contents = contents.replaceAll(entry.getKey(), entry.getValue());
        }

        return contents;
    }

    public static String convertPatterns(final String contents, final String hostnamePattern, final String string) {

        final Map<String, String> keyVals = new HashMap<String, String>();
        keyVals.put(hostnamePattern, string);

        return convertPatterns(contents, keyVals);

    }

    public static String joinAfter(List<String> arguments, int index) {
        if (CollectionUtil.isNullOrEmpty(arguments)) {
            return null;
        }

        if (arguments.size() <= (index + 1)) {
            return null;
        }

        // todo: make this safe
        return join(arguments.subList(index + 1, arguments.size() - 1));
    }

    public static String stripStringNull(String body) {
        if (equals(body, "null")) {
            return null;
        }
        return body;
    }

    public static String convertStackTraceToString(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } finally {
            try {
                if (pw != null) pw.close();
                if (sw != null) sw.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * Takes a delimited values string and returns is as a set
     *
     * @param string
     * @param delimiter
     * @return Set<String>
     */
    public static Set<String> splitUnique(String string, String delimiter) {
        if (StringUtil.isNullOrEmpty(string) || StringUtil.isNullOrEmpty(delimiter)) {
            return null;
        }

        String[] toArray = string.split(delimiter);
        if (CollectionUtil.isNullOrEmpty(toArray)) {
            return null;
        }

        Set<String> result = new HashSet<String>(toArray.length);
        for (String value : toArray) {
            // Don't allow blank entries in the set
            if (StringUtil.isNullOrEmpty(value)) {
                continue;
            }

            result.add(value);
        }

        return result;
    }

}