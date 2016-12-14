package hu.applicationgenerator.helper;

import java.io.File;

public class IOHelper {

    public static void createDirectory(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println(directory + " directory is created!");
            } else {
                System.out.println("Failed to create directory: " + directory);
            }
        }        
    }

    
}
