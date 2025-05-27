package GUI.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import Database.DBConnection;
import GUI.utils.TableUtils;

public class ViewMyBooksDialog extends JDialog {
    public ViewMyBooksDialog(JFrame parent, int userId) {
        super(parent, "My Issued Books & Requests", true);
        setSize(900, 500);
        setLayout(new BorderLayout());

        // Table to display issued books and requests
        String[] columnNames = {"Request/Issue ID", "Book ID", "Title", "Author", "Request Date", "Status", "Issue Date", "Expected Return Date", "Return Date", "Fine"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        TableUtils.styleTable(booksTable, scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        try (Connection conn = DBConnection.getConnection()) {
            // Fetch only pending requests from IssueRequests table
            String requestQuery = "SELECT r.request_id AS id, r.book_id, b.title, b.author, r.request_date, " +
                                  "'requested' AS status, NULL AS issue_date, NULL AS expected_return_date, NULL AS return_date, 0 AS fine " +
                                  "FROM IssueRequests r " +
                                  "JOIN Books b ON r.book_id = b.book_id " +
                                  "WHERE r.user_id = ? AND r.status = 'pending'";
            try (PreparedStatement stmt = conn.prepareStatement(requestQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getDate("request_date"),
                            rs.getString("status"),
                            rs.getDate("issue_date"),
                            rs.getDate("expected_return_date"),
                            rs.getDate("return_date"),
                            rs.getDouble("fine")
                        });
                    }
                }
            }

            // Fetch issued books from IssuedBooks table
            String issuedQuery = "SELECT i.issue_id AS id, i.book_id, b.title, b.author, NULL AS request_date, 'issued' AS status, " +
                                 "i.issue_date, DATE_ADD(i.issue_date, INTERVAL 15 DAY) AS expected_return_date, i.return_date, " +
                                 "CASE " +
                                 "WHEN i.return_date IS NULL AND CURDATE() > DATE_ADD(i.issue_date, INTERVAL 15 DAY) THEN " +
                                 "DATEDIFF(CURDATE(), DATE_ADD(i.issue_date, INTERVAL 15 DAY)) * 50 " +
                                 "WHEN i.return_date > DATE_ADD(i.issue_date, INTERVAL 15 DAY) THEN " +
                                 "DATEDIFF(i.return_date, DATE_ADD(i.issue_date, INTERVAL 15 DAY)) * 50 " +
                                 "ELSE 0 END AS fine " +
                                 "FROM IssuedBooks i " +
                                 "JOIN Books b ON i.book_id = b.book_id " +
                                 "WHERE i.user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(issuedQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getDate("request_date"),
                            rs.getString("status"),
                            rs.getDate("issue_date"),
                            rs.getDate("expected_return_date"),
                            rs.getDate("return_date"),
                            rs.getDouble("fine")
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(108, 117, 125)); // Neutral color
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setVisible(true);
    }
}