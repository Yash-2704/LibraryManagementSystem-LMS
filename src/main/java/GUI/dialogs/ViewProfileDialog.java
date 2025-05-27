package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Database.DBConnection;

public class ViewProfileDialog extends JDialog {
    public ViewProfileDialog(JFrame parent, int userId) {
        super(parent, "User Profile", true);
        setSize(400, 300);
        setLayout(new BorderLayout(20, 20));
        setLocationRelativeTo(parent);

        // Set dialog icon
        try {
            java.net.URL imgURL = getClass().getResource("/icons/profile.png"); // Default profile icon
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                setIconImage(image);
            } else {
                System.err.println("Couldn't find icon: /icons/profile.png");
                // Optionally add a placeholder text or leave blank if icon not found
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT user_id, name, email, role FROM Users WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Create styled labels for each field
                        JLabel idLabel = createStyledLabel("User ID: " + rs.getInt("user_id"));
                        JLabel nameLabel = createStyledLabel("Name: " + rs.getString("name"));
                        JLabel emailLabel = createStyledLabel("Email: " + rs.getString("email"));
                        JLabel roleLabel = createStyledLabel("Role: " + rs.getString("role"));

                        // Add labels to panel
                        mainPanel.add(idLabel);
                        mainPanel.add(Box.createVerticalStrut(15));
                        mainPanel.add(nameLabel);
                        mainPanel.add(Box.createVerticalStrut(15));
                        mainPanel.add(emailLabel);
                        mainPanel.add(Box.createVerticalStrut(15));
                        mainPanel.add(roleLabel);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching user profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to dialog
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
} 