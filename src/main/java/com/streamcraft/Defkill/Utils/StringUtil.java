package com.streamcraft.Defkill.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex
 * Date: 25.10.13  5:11
 */
public class StringUtil {

    public static List<String> wrapWords(String text, int lineLength) {
        String[] intendedLines = text.split("\\n");
        ArrayList<String> lines = new ArrayList<String>();
        for (String intendedLine : intendedLines) {
            String[] words = intendedLine.split(" ");
            StringBuilder buffer = new StringBuilder();

            for (String word : words) {
                if (word.length() >= lineLength) {
                    if (buffer.length() != 0) {
                        lines.add(buffer.toString());
                    }
                    lines.add(word);
                    buffer = new StringBuilder();
                    continue;
                }
                if (buffer.length() + word.length() >= lineLength) {
                    lines.add(buffer.toString());
                    buffer = new StringBuilder();
                }
                if (buffer.length() != 0) {
                    buffer.append(' ');
                }
                buffer.append(word);
            }
            lines.add(buffer.toString());
        }

        return lines;
    }

    public static String plural(int number, String form1, String form2, String form3) {
        int n1 = Math.abs(number) % 100;
        int n2 = number % 10;
        if (n1 > 10 && n1 < 20) return form3;
        if (n2 > 1 && n2 < 5) return form2;
        if (n2 == 1) return form1;
        return form3;
    }
}