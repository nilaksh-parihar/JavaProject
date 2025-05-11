package homeenergy.utils;

import java.io.*;

public class FileUtil {
    public static void writeToFile(String filename, String data) throws IOException {
        File file = new File(filename);
        // If the file doesn't exist, create it
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, true);
        fw.write(data + "\n");
        fw.close();
    }
    
    public static String readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        File file = new File(filename);
        // If the file doesn't exist, return empty string
        if (!file.exists()) {
            return "";
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }
}