package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Database.DBConnection;

public class DeleteBookDialog extends JDialog {
    public DeleteBookDialog(JFrame parent) {
        super(parent, "Delete Book", true);
        setSize(350, 150); // Adjusted size for consistency
        setLayout(new BorderLayout(10, 10)); // Changed to BorderLayout

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 5, 5)); // Panel for input field
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Add some padding

        JTextField bookIdField = new JTextField();
        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);

        add(formPanel, BorderLayout.CENTER); // Add form panel to the center

        JButton deleteButton = new JButton("Delete");
        // Apply button styling
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 53, 69)); // Red color for delete
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Panel for the button, centered
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding around button panel
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom

        deleteButton.addActionListener(e -> {
            int bookId;
            try {
                bookId = Integer.parseInt(bookIdField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Book ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Delete the book from Books table
                String deleteQuery = "DELETE FROM Books WHERE book_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, bookId);
                    int rowsDeleted = deleteStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Book ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
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