package org.jkan997.booklibrary.util.internal;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CsvProcessor {

    public static String[] splitLine(String input) {
        List<String> result = new ArrayList<String>();
        int start = 0;
        boolean inQuotes = false;
        for (int current = 0; current < input.length(); current++) {
            if (input.charAt(current) == '\"') {
                inQuotes = !inQuotes; // toggle state
            }
            boolean atLastChar = (current == input.length() - 1);
            if (atLastChar) {
                result.add(input.substring(start));
            } else if (input.charAt(current) == ',' && !inQuotes) {
                result.add(input.substring(start, current));
                start = current + 1;
            }

        }
        return result.toArray(new String[]{});
    }

    public static List<String[]> readBooks(String resourceName) throws Exception {
        List<String[]> res = new ArrayList<>();
        InputStream is = CsvProcessor.class.getResourceAsStream("/" + resourceName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            String[] arr = splitLine(line);
            String author = arr[1].trim().replace("\"", "");
            String title = arr[2].trim().replace("\"", "");
            res.add(new String[]{author, title});
        }
        is.close();
        return res;
    }

}
