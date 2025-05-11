package homeenergy.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import homeenergy.models.User;
import homeenergy.services.AuthService;
import homeenergy.services.BillEstimator;
import homeenergy.services.EnergyManager;

public class MainGUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AuthService authService;
    private BillEstimator billEstimator;
    private User currentUser;

    public MainGUI() {
        authService = new AuthService();
        billEstimator = new BillEstimator();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Home Energy Monitoring System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300); // Adjusted size for a cleaner layout

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LoginPanel");
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Home Energy Monitoring System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Input fields
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField(15);

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                currentUser = authService.login(username, password);

                if (currentUser != null) {
                    EnergyManager userEnergyManager = new EnergyManager(currentUser.getUsername());
                    Runnable backCallback = () -> cardLayout.show(mainPanel, "LoginPanel");

                    if (currentUser.getRole().equals("admin")) {
                        mainPanel.add(new AdminPanel(userEnergyManager, billEstimator, authService, backCallback),
                                "AdminPanel");
                        cardLayout.show(mainPanel, "AdminPanel");
                    } else {
                        mainPanel.add(new UserPanel(userEnergyManager, billEstimator, backCallback), "UserPanel");
                        cardLayout.show(mainPanel, "UserPanel");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials. Please try again.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField regUsername = new JTextField();
                JPasswordField regPassword = new JPasswordField();
                JCheckBox adminCheck = new JCheckBox("Register as admin");

                Object[] regForm = {
                        "Username:", regUsername,
                        "Password:", regPassword,
                        "Admin:", adminCheck
                };

                int option = JOptionPane.showConfirmDialog(frame, regForm, "Register", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String username = regUsername.getText().trim();
                    String password = new String(regPassword.getPassword());
                    boolean isAdmin = adminCheck.isSelected();
                    boolean success = authService.register(username, password, isAdmin ? "admin" : "user");

                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Registered successfully! Now you can log in.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Registration failed. Username might already exist.");
                    }
                }
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}