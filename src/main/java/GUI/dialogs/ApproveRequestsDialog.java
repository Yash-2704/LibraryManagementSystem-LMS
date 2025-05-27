package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ApproveRequestsDialog extends JDialog {
    public ApproveRequestsDialog(JFrame parent) {
        super(parent, "Approve/Deny Book Requests", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Table to display book requests
        String[] columnNames = {"Request ID", "User ID", "Book ID", "Request Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable requestsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        TableUtils.styleTable(requestsTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch book requests from the IssueRequests table
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM IssueRequests WHERE status = 'pending'")) {

            while (rs.next()) {
                int requestId = rs.getInt("request_id");
                int userId = rs.getInt("user_id");
                int bookId = rs.getInt("book_id");
                Date requestDate = rs.getDate("request_date");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{requestId, userId, bookId, requestDate, status});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Approve and Deny buttons
        JPanel buttonPanel = new JPanel();
        JButton approveButton = new JButton("Approve");
        JButton denyButton = new JButton("Deny");

        // Style Approve Button
        approveButton.setFont(new Font("Arial", Font.BOLD, 14));
        approveButton.setBackground(new Color(40, 167, 69)); // Green color
        approveButton.setForeground(Color.WHITE);
        approveButton.setFocusPainted(false);
        approveButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Style Deny Button
        denyButton.setFont(new Font("Arial", Font.BOLD, 14));
        denyButton.setBackground(new Color(220, 53, 69)); // Red color
        denyButton.setForeground(Color.WHITE);
        denyButton.setFocusPainted(false);
        denyButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Set layout for the panel
        buttonPanel.add(approveButton);
        buttonPanel.add(denyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Approve request
        approveButton.addActionListener(e -> {
            int selectedRow = requestsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to approve.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int requestId = (int) tableModel.getValueAt(selectedRow, 0);
            int userId = (int) tableModel.getValueAt(selectedRow, 1);
            int bookId = (int) tableModel.getValueAt(selectedRow, 2);

            try (Connection conn = DBConnection.getConnection()) {
                // Update request status
                String updateRequestQuery = "UPDATE IssueRequests SET status = 'approved' WHERE request_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateRequestQuery)) {
                    stmt.setInt(1, requestId);
                    stmt.executeUpdate();
                }

                // Decrease available quantity of the book
                String updateBookQuery = "UPDATE Books SET available = available - 1 WHERE book_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }

                // Add the issued book to the IssuedBooks table
                String insertIssuedBookQuery = "INSERT INTO IssuedBooks (user_id, book_id, issue_date, due_date, return_date, fine) " +
                                               "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), NULL, 0)";
                try (PreparedStatement stmt = conn.prepareStatement(insertIssuedBookQuery)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, bookId);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Request approved and book issued successfully!");
                tableModel.removeRow(selectedRow);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Deny request
        denyButton.addActionListener(e -> {
            int selectedRow = requestsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to deny.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int requestId = (int) tableModel.getValueAt(selectedRow, 0);

            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE IssueRequests SET status = 'denied' WHERE request_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, requestId);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Request denied successfully!");
                tableModel.removeRow(selectedRow);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}