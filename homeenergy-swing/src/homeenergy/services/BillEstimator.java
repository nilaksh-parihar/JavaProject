package homeenergy.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import homeenergy.models.Appliance;

public class BillEstimator {
    private final String appliancesFile = "appliances.txt";
    private final String billHistoryFile = "bill_history.txt";
    private final double rate = 0.15; // cost per kWh

    public double estimateBill(ArrayList<Appliance> appliances) {
        double totalEnergy = 0.0;
        for (Appliance appliance : appliances) {
            totalEnergy += appliance.calculateEnergy();
        }
        return totalEnergy * rate;
    }

    public void logBill(String username, double totalEnergy, double billAmount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(billHistoryFile, true))) {
            bw.write(username + " - Total Energy: " + totalEnergy + " kWh - Bill: $" + billAmount);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error logging bill history: " + e.getMessage());
        }
    }

    public String viewAllBillHistory() {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(billHistoryFile);
            if (!file.exists()) {
                return "No bill history found.";
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
        } catch (Exception e) {
            sb.append("Error reading bill history: " + e.getMessage());
        }
        return sb.toString();
    }

    public String viewUserBillHistory(String username) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(billHistoryFile);
            if (!file.exists()) {
                return "No bill history found.";
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(username + " -")) {
                    sb.append(line).append("\n");
                }
            }
            br.close();
            if (sb.length() == 0) {
                return "No bill history found for user: " + username;
            }
        } catch (Exception e) {
            sb.append("Error reading bill history: " + e.getMessage());
        }
        return sb.toString();
    }
}