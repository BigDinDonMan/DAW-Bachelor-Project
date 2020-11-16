package utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    private StringUtils() {}

    public static String[] splitUppercase(String s) {
        StringBuilder sb = new StringBuilder();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                String _s = sb.toString();
                if (!_s.isBlank()) {
                    strings.add(_s);
                }
                sb.setLength(0);
            }
            sb.append(c);
        }

        String last = sb.toString();
        if (!last.isBlank()) {
            strings.add(last);
        }
        return strings.toArray(new String[0]);
    }
}
