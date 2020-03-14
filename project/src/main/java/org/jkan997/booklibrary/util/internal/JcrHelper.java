package org.jkan997.booklibrary.util.internal;

public class JcrHelper {

    public static String escapeName(String newName) {
        newName = newName.replace("/", " ");
        newName = newName.replace(":", " ");
        newName = newName.replace("?", "");
        //newName = newName.replace("\\s+", "_");
        newName = newName.replaceAll("[^A-Za-z0-9]", "");
        System.out.println(newName);
        //   newName = newName.replace("(", "");
        //   newName = newName.replace(")", "");
        return newName;
    }
}
