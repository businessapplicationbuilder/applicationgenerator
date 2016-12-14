package hu.applicationgenerator.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringHelper {
    
    private static final List<String> IDENTIFIERCHARACTERS = Collections.unmodifiableList(
        new ArrayList<String>() {{
            add("a");
            add("b");
            add("c");
            add("d");
            add("e");
            add("f");
            add("g");
            add("h");
            add("i");
            add("j");
            add("k");
            add("l");
            add("m");
            add("n");
            add("o");
            add("p");
            add("q");
            add("r");
            add("s");
            add("t");
            add("u");
            add("z");
            add("v");
            add("w");
            add("x");
            add("y");
            add("0");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
        }});

    private static final List<String> NUMERICALCHARACTERS = Collections.unmodifiableList(
        new ArrayList<String>() {{
            add("0");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
        }});
    
    public static boolean isValidIdentifier(String text) {
        for (int i=0; i<text.length(); i++) {
            if (!IDENTIFIERCHARACTERS.contains(text.substring(i, i+1).toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidNumber(String text) {
        for (int i=0; i<text.length(); i++) {
            if (!NUMERICALCHARACTERS.contains(text.substring(i, i+1))) {
                return false;
            }
        }
        return true;
    }
    
    public static Integer numberOfContained(String text, String subText) {
        int j=0;
        for (int i=0; i<text.length(); i++) {
            if (text.substring(i, i+1).equals(subText)) {
                j++;
            }
        }
        return j;
    }
    
}
