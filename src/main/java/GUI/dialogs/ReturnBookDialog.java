package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Database.DBConnection;

public class ReturnBookDialog extends JDialog {
    public ReturnBookDialog(JFrame parent) {
        super(parent, "Mark Book as Returned", true);
        setSize(400, 300);
        setLayout(new GridLayout(3, 2));

        JTextField issueIdField = new JTextField();
        JButton returnButton = new JButton("Mark as Returned");

        add(new JLabel("Issue ID:"));
        add(issueIdField);
        add(new JLabel(""));
        add(returnButton);

        returnButton.addActionListener(e -> {
            int issueId;
            try {
                issueId = Integer.parseInt(issueIdField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Issue ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Fetch details of the issued book
                String fetchQuery = "SELECT user_id, book_id, fine FROM IssuedBooks WHERE issue_id = ?";
                try (PreparedStatement fetchStmt = conn.prepareStatement(fetchQuery)) {
                    fetchStmt.setInt(1, issueId);

                    try (ResultSet rs = fetchStmt.executeQuery()) {
                        if (rs.next()) {
                            int userId = rs.getInt("user_id");
                            int bookId = rs.getInt("book_id");
                            double fine = rs.getDouble("fine");

                            // Insert into ReturnedBooks table
                            String insertReturnedQuery = "INSERT INTO ReturnedBooks (issue_id, user_id, book_id, return_date, fine) " +
                                                         "VALUES (?, ?, ?, CURDATE(), ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertReturnedQuery)) {
                                insertStmt.setInt(1, issueId);
                                insertStmt.setInt(2, userId);
                                insertStmt.setInt(3, bookId);
                                insertStmt.setDouble(4, fine);
                                insertStmt.executeUpdate();
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

                            JOptionPane.showMessageDialog(this, "Book marked as returned successfully!");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid Issue ID.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
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