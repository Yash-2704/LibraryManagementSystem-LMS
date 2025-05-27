package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Database.DBConnection;

public class ReturnBookByUserDialog extends JDialog {
    public ReturnBookByUserDialog(JFrame parent, int userId) {
        super(parent, "Return a Book", true);
        setSize(350, 150); // Adjusted size to be more compact
        setLayout(new BorderLayout(10, 10)); // Changed to BorderLayout

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 5, 5)); // Panel for input field
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Add some padding

        JTextField issueIdField = new JTextField();
        formPanel.add(new JLabel("Enter Issue ID:"));
        formPanel.add(issueIdField);
        
        add(formPanel, BorderLayout.CENTER); // Add form panel to the center

        JButton returnButton = new JButton("Return");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Panel for the button, centered
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding around button panel
        buttonPanel.add(returnButton);

        add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom

        returnButton.addActionListener(e -> {
            int issueId;
            try {
                issueId = Integer.parseInt(issueIdField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Issue ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false); // Start transaction

                // Check if the issue ID belongs to the logged-in user
                String checkQuery = "SELECT book_id, DATEDIFF(CURDATE(), DATE_ADD(issue_date, INTERVAL 15 DAY)) * 50 AS current_fine FROM IssuedBooks WHERE issue_id = ? AND user_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, issueId);
                    checkStmt.setInt(2, userId);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            int bookId = rs.getInt("book_id");
                            double fine = rs.getDouble("current_fine");
                            if (fine < 0) fine = 0; // No negative fine

                            // Insert into ReturnedBooks table
                            String insertReturnedQuery = "INSERT INTO ReturnedBooks (issue_id, user_id, book_id, return_date, fine) " +
                                                         "VALUES (?, ?, ?, CURDATE(), ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertReturnedQuery)) {
                                insertStmt.setInt(1, issueId);
                                insertStmt.setInt(2, userId);
                                insertStmt.setInt(3, bookId);
                                insertStmt.setDouble(4, fine);
                                int rowsInserted = insertStmt.executeUpdate();
                                if (rowsInserted == 0) {
                                    JOptionPane.showMessageDialog(this, "Failed to update ReturnedBooks table.", "Error", JOptionPane.ERROR_MESSAGE);
                                    conn.rollback(); // Rollback transaction
                                    return;
                                }
                            }

                            // Remove from IssuedBooks table
                            String deleteIssuedQuery = "DELETE FROM IssuedBooks WHERE issue_id = ?";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteIssuedQuery)) {
                                deleteStmt.setInt(1, issueId);
                                deleteStmt.executeUpdate();
                            }

                            // Update available quantity in Books table
                            String updateBookQuery = "UPDATE Books SET available = available + 1 WHERE book_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {
                                updateStmt.setInt(1, bookId);
                                updateStmt.executeUpdate();
                            }

                            conn.commit(); // Commit transaction
                            String fineMessage = fine > 0 ? " Your fine is: " + fine + " rupees." : "";
                            JOptionPane.showMessageDialog(this, "Book returned successfully!" + fineMessage);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid Issue ID or you are not authorized to return this book.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    conn.rollback(); // Rollback transaction on error
                    throw ex;
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