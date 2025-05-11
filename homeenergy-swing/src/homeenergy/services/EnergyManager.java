package homeenergy.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import homeenergy.models.Appliance;
import homeenergy.models.HeavyAppliance;
import homeenergy.models.LightAppliance;

public class EnergyManager {
    private String filename;
    private ArrayList<Appliance> appliances = new ArrayList<>();

    public EnergyManager(String username) {
        // Each user’s appliances are stored in a separate file
        this.filename = "appliances_" + username + ".txt";
        loadAppliancesFromFile();
    }

    private void loadAppliancesFromFile() {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return; // Start with an empty list if file doesn't exist
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // Expecting format: name - powerW - hours h/day - energy kWh/day
                String[] parts = line.split(" - ");
                if (parts.length >= 4) {
                    String name = parts[0].trim();
                    int power = Integer.parseInt(parts[1].replace("W", "").trim());
                    int hours = Integer.parseInt(parts[2].replace("h/day", "").trim());
                    double energy = Double.parseDouble(parts[3].replace("kWh/day", "").trim());
                    Appliance appliance = (energy > 1.0)
                        ? new HeavyAppliance(name, power, hours)
                        : new LightAppliance(name, power, hours);
                    appliances.add(appliance);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading appliances: " + e.getMessage());
        }
    }

    private void saveAppliancesToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (Appliance appliance : appliances) {
                bw.write(appliance.getData());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error saving appliances: " + e.getMessage());
        }
    }

    public void registerAppliance(String name, int power, int hours, boolean isHeavy) {
        Appliance appliance = isHeavy 
            ? new HeavyAppliance(name, power, hours) 
            : new LightAppliance(name, power, hours);
        appliances.add(appliance);
        saveAppliancesToFile();
    }

    public ArrayList<Appliance> getAppliances() {
        return appliances;
    }

    public String viewData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < appliances.size(); i++) {
            sb.append(i).append(": ").append(appliances.get(i).getData()).append("\n");
        }
        return sb.toString();
    }

    public void deleteAppliance(int index) {
        if (index >= 0 && index < appliances.size()) {
            appliances.remove(index);
            saveAppliancesToFile();
        }
    }
}