package org.kabeja.util;

public class StringUtils {
    public static boolean isBlank(String string) {
        if (string.length() == 0) {
            return true;
        }
        for (int i = 0; i < string.length() - 1; i++) {
            if (!(Character.isWhitespace(string.charAt(i)) || Character.isSpaceChar(string.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}
