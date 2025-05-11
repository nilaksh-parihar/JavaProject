package homeenergy.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import homeenergy.models.User;
import homeenergy.utils.FileUtil;

public class AuthService {
    private final String userFile = "users.txt";

    public boolean register(String username, String password, String role) {
        try {
            if (isUserExists(username)) {
                return false;
            }
            String record = username + "," + password + "," + role;
            FileUtil.writeToFile(userFile, record);
            return true;
        } catch (IOException e) {
            System.out.println("Error: Couldn't register user.");
            return false;
        }
    }

    public User login(String username, String password) {
        try {
            String[] lines = FileUtil.readFromFile(userFile).split("\n");
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username) && parts[1].equals(password)) {
                    return new User(username, parts[2].trim());
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("Error: User database not found.");
            return null;
        }
    }

    private boolean isUserExists(String username) throws IOException {
        String content = FileUtil.readFromFile(userFile);
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 1 && parts[0].equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeUser(String username) {
        try {
            String content = FileUtil.readFromFile(userFile);
            String[] lines = content.split("\n");
            List<String> remaining = new ArrayList<>();
            boolean found = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    found = true;
                    continue;
                }
                if (!line.trim().isEmpty()) {
                    remaining.add(line);
                }
            }
            if (!found) {
                return false;
            }
            java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(userFile, false));
            for (String rec : remaining) {
                bw.write(rec);
                bw.newLine();
            }
            bw.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error removing user: " + e.getMessage());
            return false;
        }
    }
}