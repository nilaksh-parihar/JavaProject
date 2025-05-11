package homeenergy.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import homeenergy.models.Appliance;
import homeenergy.services.BillEstimator;
import homeenergy.services.EnergyManager;

public class UserPanel extends JPanel {
    private EnergyManager energyManager;
    private BillEstimator billEstimator;
    private Runnable backCallback;
    private JTextArea applianceDataArea;
    private JTextField applianceNameField;
    private JTextField appliancePowerField;
    private JTextField applianceHoursField;
    private JCheckBox heavyCheckBox;
    private JButton addApplianceButton;
    private JButton removeApplianceButton;
    private JButton viewApplianceButton;
    private JButton estimateBillButton;
    private JButton backButton;
    private JButton exitButton;

    public UserPanel(EnergyManager energyManager, BillEstimator billEstimator, Runnable backCallback) {
        this.energyManager = energyManager;
        this.billEstimator = billEstimator;
        this.backCallback = backCallback;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("User Panel - Appliance Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        JPanel addPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add Appliance"));

        addPanel.add(new JLabel("Name:"));
        applianceNameField = new JTextField();
        addPanel.add(applianceNameField);

        addPanel.add(new JLabel("Power (W):"));
        appliancePowerField = new JTextField();
        addPanel.add(appliancePowerField);

        addPanel.add(new JLabel("Hours/Day:"));
        applianceHoursField = new JTextField();
        addPanel.add(applianceHoursField);

        addPanel.add(new JLabel("Heavy Appliance?"));
        heavyCheckBox = new JCheckBox();
        addPanel.add(heavyCheckBox);

        addApplianceButton = new JButton("Add Appliance");
        addPanel.add(addApplianceButton);

        removeApplianceButton = new JButton("Remove Appliance (by index)");
        addPanel.add(removeApplianceButton);

        add(addPanel, BorderLayout.WEST);

        applianceDataArea = new JTextArea();
        applianceDataArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(applianceDataArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Appliances"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        viewApplianceButton = new JButton("View Appliances");
        estimateBillButton = new JButton("Estimate Bill");
        backButton = new JButton("Back");
        exitButton = new JButton("Exit");
        bottomPanel.add(viewApplianceButton);
        bottomPanel.add(estimateBillButton);
        bottomPanel.add(backButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        addApplianceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = applianceNameField.getText().trim();
                    int power = Integer.parseInt(appliancePowerField.getText().trim());
                    int hours = Integer.parseInt(applianceHoursField.getText().trim());
                    boolean isHeavy = heavyCheckBox.isSelected();
                    energyManager.registerAppliance(name, power, hours, isHeavy);
                    JOptionPane.showMessageDialog(UserPanel.this, "Appliance added successfully!");
                    applianceNameField.setText("");
                    appliancePowerField.setText("");
                    applianceHoursField.setText("");
                    heavyCheckBox.setSelected(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserPanel.this, "Error: " + ex.getMessage());
                }
            }
        });

        removeApplianceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(UserPanel.this, "Enter appliance index to remove:");
                try {
                    int index = Integer.parseInt(input.trim());
                    energyManager.deleteAppliance(index);
                    JOptionPane.showMessageDialog(UserPanel.this, "Appliance removed successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserPanel.this, "Error removing appliance: " + ex.getMessage());
                }
            }
        });

        viewApplianceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applianceDataArea.setText(energyManager.viewData());
            }
        });

        estimateBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Appliance> appliances = energyManager.getAppliances();
                double totalEnergy = 0.0;
                for (Appliance appliance : appliances) {
                    totalEnergy += appliance.calculateEnergy();
                }
                double bill = billEstimator.estimateBill(appliances);
                billEstimator.logBill("Current User", totalEnergy, bill);
                JOptionPane.showMessageDialog(UserPanel.this, "Your bill is estimated at: $" + bill);
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
    }
}