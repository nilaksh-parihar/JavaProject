package homeenergy.gui;

import homeenergy.models.Appliance;
import homeenergy.services.BillEstimator;
import homeenergy.services.EnergyManager;
import homeenergy.services.AuthService;
import homeenergy.utils.FileUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AdminPanel extends JPanel {
    private EnergyManager energyManager;
    private BillEstimator billEstimator;
    private AuthService authService;
    private Runnable backCallback;
    private JTabbedPane tabbedPane;
    private JTextArea allUsersArea;
    private JButton refreshAllUsersButton;
    private JTextField lookupUserField;
    private JButton viewUserAppliancesButton;
    private JButton viewUserBillButton;
    private JTextArea lookupResultArea;
    private JButton backButton;
    private JButton exitButton;

    public AdminPanel(EnergyManager energyManager, BillEstimator billEstimator, AuthService authService,
            Runnable backCallback) {
        this.energyManager = energyManager;
        this.billEstimator = billEstimator;
        this.authService = authService;
        this.backCallback = backCallback;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Panel - User Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane();
        allUsersArea = new JTextArea();
        allUsersArea.setEditable(false);
        JScrollPane allUsersScroll = new JScrollPane(allUsersArea);
        JPanel allUsersPanel = new JPanel(new BorderLayout(5, 5));
        refreshAllUsersButton = new JButton("Refresh All Users");
        allUsersPanel.add(refreshAllUsersButton, BorderLayout.NORTH);
        allUsersPanel.add(allUsersScroll, BorderLayout.CENTER);
        tabbedPane.addTab("All Users", allUsersPanel);

        JPanel lookupPanel = new JPanel(new BorderLayout(5, 5));
        JPanel lookupInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lookupInputPanel.add(new JLabel("Username:"));
        lookupUserField = new JTextField(15);
        lookupInputPanel.add(lookupUserField);
        viewUserAppliancesButton = new JButton("View Appliances");
        viewUserBillButton = new JButton("View Bill");
        lookupInputPanel.add(viewUserAppliancesButton);
        lookupInputPanel.add(viewUserBillButton);
        lookupPanel.add(lookupInputPanel, BorderLayout.NORTH);

        lookupResultArea = new JTextArea();
        lookupResultArea.setEditable(false);
        JScrollPane lookupScroll = new JScrollPane(lookupResultArea);
        lookupPanel.add(lookupScroll, BorderLayout.CENTER);
        tabbedPane.addTab("User Lookup", lookupPanel);

        add(tabbedPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        backButton = new JButton("Back");
        exitButton = new JButton("Exit");
        bottomPanel.add(backButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshAllUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allUsersArea.setText(getAllUsersDetails());
            }
        });

        viewUserAppliancesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = lookupUserField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Please enter a username.");
                    return;
                }
                String appliances = getUserAppliances(username);
                lookupResultArea.setText("Appliances for " + username + ":\n" + appliances);
            }
        });

        viewUserBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = lookupUserField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Please enter a username.");
                    return;
                }
                double bill = getUserBill(username);
                lookupResultArea.setText("Bill for " + username + ": $" + bill);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backCallback.run();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        allUsersArea.setText(getAllUsersDetails());
    }

    // Returns details for all users: appliances and computed bill
    private String getAllUsersDetails() {
        StringBuilder sb = new StringBuilder();
        try {
            String usersContent = FileUtil.readFromFile("users.txt");
            String[] lines = usersContent.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                String username = parts[0].trim();
                String role = (parts.length >= 3) ? parts[2].trim() : "user";
                if (role.equals("admin")) {
                    sb.append("Admin: ").append(username).append("\n");
                } else {
                    sb.append("User: ").append(username).append("\n");
                }
                String applianceFilename = "appliances_" + username + ".txt";
                String applianceData = FileUtil.readFromFile(applianceFilename);
                if (applianceData.trim().isEmpty()) {
                    sb.append("  No appliances found.\n");
                } else {
                    sb.append("  Appliances:\n");
                    String[] applianceLines = applianceData.split("\n");
                    double totalEnergy = 0.0;
                    for (String aLine : applianceLines) {
                        sb.append("    ").append(aLine).append("\n");
                        String[] tokens = aLine.split(" - ");
                        if (tokens.length >= 4) {
                            String energyStr = tokens[3].replace("kWh/day", "").trim();
                            try {
                                double energy = Double.parseDouble(energyStr);
                                totalEnergy += energy;
                            } catch (NumberFormatException nfe) {
                            }
                        }
                    }
                    double bill = totalEnergy * 0.15; // using same rate as BillEstimator
                    sb.append("  Total Energy: ").append(totalEnergy).append(" kWh, Bill: $").append(bill).append("\n");
                }
                sb.append("\n");
            }
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    // Returns a particular user's appliances by reading the file
    private String getUserAppliances(String username) {
        try {
            String applianceFilename = "appliances_" + username + ".txt";
            String data = FileUtil.readFromFile(applianceFilename);
            return (data.trim().isEmpty()) ? "No appliances found." : data;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Computes the bill for a particular user by instantiating a temporary
    // EnergyManager
    private double getUserBill(String username) {
        EnergyManager tempManager = new EnergyManager(username);
        ArrayList<Appliance> appliances = tempManager.getAppliances();
        double totalEnergy = 0.0;
        for (Appliance a : appliances) {
            totalEnergy += a.calculateEnergy();
        }
        return totalEnergy * 0.15;
    }
}