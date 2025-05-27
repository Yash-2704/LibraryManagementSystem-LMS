package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class AddUserDialog extends JDialog {
    public AddUserDialog(JFrame parent) {
        super(parent, "Add Librarian/User", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display current users
        String[] columnNames = {"User ID", "Name", "Email", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        TableUtils.styleTable(userTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch current users from the database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, name, email, role FROM Users")) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");

                tableModel.addRow(new Object[]{userId, name, email, role});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Form to add a new user
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Librarian", "User"});
        JButton addButton = new JButton("Add");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);

        // Button panel for "Add"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(0, 122, 255)); // Example blue color
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15) 
        ));
        buttonPanel.add(addButton);

        // Main content panel for form and button panel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Add user to the database
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = roleComboBox.getSelectedItem().toString();

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO Users (name, email, password, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, password);
                    stmt.setString(4, role);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "User added successfully!");
                        tableModel.addRow(new Object[]{null, name, email, role}); // Add to table
                        nameField.setText("");
                        emailField.setText("");
                        passwordField.setText("");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        setLocationRelativeTo(parent);
        setVisible(true);
    }
} 