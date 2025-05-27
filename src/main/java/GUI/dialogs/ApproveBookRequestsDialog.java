package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ApproveBookRequestsDialog extends JDialog {
    public ApproveBookRequestsDialog(JFrame parent) {
        super(parent, "Approve/Deny Book Addition Requests", true);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Table to display book requests
        String[] columnNames = {"Request ID", "Title", "Author", "Publisher", "Request Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable requestsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        TableUtils.styleTable(requestsTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch book requests from the BookRequests table
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM BookRequests WHERE status = 'pending'")) {

            while (rs.next()) {
                int requestId = rs.getInt("request_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                Date requestDate = rs.getDate("request_date");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{requestId, title, author, publisher, requestDate, status});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching book requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String author = (String) tableModel.getValueAt(selectedRow, 2);
            String publisher = (String) tableModel.getValueAt(selectedRow, 3);

            try (Connection conn = DBConnection.getConnection()) {
                // Add the book to the Books table
                String addBookQuery = "INSERT INTO Books (title, author, publisher, quantity, available) VALUES (?, ?, ?, 10, 10)";
                try (PreparedStatement stmt = conn.prepareStatement(addBookQuery)) {
                    stmt.setString(1, title);
                    stmt.setString(2, author);
                    stmt.setString(3, publisher);
                    stmt.executeUpdate();
                }

                // Update the request status
                String updateRequestQuery = "UPDATE BookRequests SET status = 'approved' WHERE request_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateRequestQuery)) {
                    stmt.setInt(1, requestId);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Book request approved and added to the library!");
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
                String updateRequestQuery = "UPDATE BookRequests SET status = 'denied' WHERE request_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateRequestQuery)) {
                    stmt.setInt(1, requestId);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Book request denied!");
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