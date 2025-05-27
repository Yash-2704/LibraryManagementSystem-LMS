package GUI;

// File: LoginFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import Database.DBConnection;
import GUI.utils.ThemeManager;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        // Initialize theme
        ThemeManager.initialize();

        setTitle("Library Management System - Login");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        emailField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);

        loginButton.addActionListener(e -> authenticate());

        setVisible(true);
    }

    private void authenticate() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.DBConnection.getConnection()) {
            String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setString(2, pass);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        int userId = rs.getInt("user_id");
                        JOptionPane.showMessageDialog(this, "Login Successful as " + role);

                        dispose(); // Close the login window
                        switch (role.toLowerCase()) {
                            case "admin":
                                new AdminDashboard();
                                break;
                            case "librarian":
                                new LibrarianDashboard();
                                break;
                            case "user":
                                new UserDashboard(userId);
                                break;
                            default:
                                JOptionPane.showMessageDialog(this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
